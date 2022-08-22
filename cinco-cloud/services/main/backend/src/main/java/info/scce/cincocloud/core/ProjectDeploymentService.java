package info.scce.cincocloud.core;

import info.scce.cincocloud.config.Properties;
import info.scce.cincocloud.core.rest.tos.ProjectDeploymentStatus;
import info.scce.cincocloud.core.rest.tos.ProjectDeploymentTO;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.StopProjectPodsTaskDB;
import info.scce.cincocloud.k8s.K8SClientService;
import info.scce.cincocloud.k8s.K8SIngressService;
import info.scce.cincocloud.k8s.K8SUtils;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SDeployment;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SIngress;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SPersistentVolume;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SPersistentVolumeClaim;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SService;
import info.scce.cincocloud.k8s.modeleditor.PyroAppK8SDeployment;
import info.scce.cincocloud.k8s.modeleditor.PyroAppK8SIngressBackend;
import info.scce.cincocloud.k8s.modeleditor.PyroAppK8SIngressFrontend;
import info.scce.cincocloud.k8s.modeleditor.PyroAppK8SPersistentVolume;
import info.scce.cincocloud.k8s.modeleditor.PyroAppK8SPersistentVolumeClaim;
import info.scce.cincocloud.k8s.modeleditor.PyroAppK8SService;
import info.scce.cincocloud.k8s.modeleditor.PyroDatabaseK8SDeployment;
import info.scce.cincocloud.k8s.modeleditor.PyroDatabaseK8SPersistentVolume;
import info.scce.cincocloud.k8s.modeleditor.PyroDatabaseK8SPersistentVolumeClaim;
import info.scce.cincocloud.k8s.modeleditor.PyroDatabaseK8SService;
import info.scce.cincocloud.k8s.shared.K8SPersistentVolumeOptions;
import info.scce.cincocloud.sync.ProjectWebSocket;
import info.scce.cincocloud.util.CDIUtils;
import info.scce.cincocloud.util.WaitUtils;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import java.time.Duration;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Transactional
public class ProjectDeploymentService {

  private static final int TIMEOUT_TIME_MIN = 10;
  private static final int DELAY_TIME_SEC = 1;

  @Inject
  ProjectWebSocket projectWebSocket;

  @Inject
  K8SClientService clientService;

  @Inject
  K8SIngressService ingressService;

  @Inject
  Vertx vertx;

  @Inject
  Properties properties;

  @ConfigProperty(name = "cincocloud.host")
  String host;

  @ConfigProperty(name = "cincocloud.environment")
  String environment;

  @ConfigProperty(name = "archetype.image")
  String archetypeImage;

  @ConfigProperty(name = "archetype.storage-class-name")
  String archetypeStorageClassName;

  @ConfigProperty(name = "archetype.storage")
  String archetypeStorage;

  @ConfigProperty(name = "archetype.host-path")
  String archetypeHostPath;

  @ConfigProperty(name = "archetype.create-persistent-volumes")
  boolean archetypeCreatePersistentVolumes;

  KubernetesClient client;

  K8SPersistentVolumeOptions pvOptions;

  void startup(@Observes StartupEvent event) {
    client = clientService.createClient();
    pvOptions = new K8SPersistentVolumeOptions(
            archetypeStorageClassName,
            archetypeStorage,
            archetypeHostPath,
            archetypeCreatePersistentVolumes);
  }

  public ProjectDeploymentTO deploy(ProjectDB project) {
    if (project.isLanguageEditor()) {
      return deployLanguageEditor(project);
    } else {
      return deployModelEditor(project);
    }
  }

  public ProjectDeploymentTO redeploy(ProjectDB project) {
    stop(project);
    return deploy(project);
  }

  public void stop(ProjectDB project) {
    if (project.isLanguageEditor()) {
      stopLanguageEditor(project);
    } else {
      stopModelEditor(project);
    }
  }

  public void delete(ProjectDB project) {
    if (project.isLanguageEditor()) {
      stopAndDeleteLanguageEditor(project);
    } else {
      stopAndDeleteModelEditor(project);
    }
  }

  private ProjectDeploymentTO deployModelEditor(ProjectDB project) {
    // create modeleditor app resources
    final var appService = new PyroAppK8SService(client, project);
    final var appPersistentVolume = new PyroAppK8SPersistentVolume(client, project, pvOptions);
    final var appPersistentVolumeClaim = new PyroAppK8SPersistentVolumeClaim(client, project, pvOptions);
    final var appDeployment = new PyroAppK8SDeployment(client, appPersistentVolumeClaim,
        host, environment, archetypeImage, properties.getMinioHost(), properties.getMinioPort(),
        properties.getMinioAccessKey(), properties.getMinioSecretKey(), project);
    final var appIngressFrontend = new PyroAppK8SIngressFrontend(client, appService, project, host,
        ingressService.getWorkspaceRootPath());
    final var appIngressBackend = new PyroAppK8SIngressBackend(client, appService, project, host,
        ingressService.getWorkspaceRootPath());

    // create modeleditor database resources
    final var databaseService = new PyroDatabaseK8SService(client, project);
    final var databasePersistentVolume = new PyroDatabaseK8SPersistentVolume(client, project, pvOptions);
    final var databasePersistentVolumeClaim = new PyroDatabaseK8SPersistentVolumeClaim(client, project, pvOptions);
    final var databaseDeployment = new PyroDatabaseK8SDeployment(client,
        databasePersistentVolumeClaim, project);

    final var deployedAppOptional = client.apps().deployments().list().getItems().stream()
        .filter(pod -> pod.getMetadata() != null)
        .filter(pod -> pod.getMetadata().getName()
            .startsWith(appDeployment.getResource().getMetadata().getName()))
        .findFirst();

    // do not redeploy pods if they are still active
    // if any pod removal is scheduled, that task is removed
    if (deployedAppOptional.isPresent() && K8SUtils
        .isDeploymentRunning(deployedAppOptional.get())) {
      removeScheduledTasks(project);
      final var status = new ProjectDeploymentTO(appIngressFrontend.getPath(),
          ProjectDeploymentStatus.READY);
      projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));
      return status;
    }

    if (K8SUtils.getPersistentVolumeClaimByName(client,
        appPersistentVolumeClaim.getResource().getMetadata().getName()).isEmpty()) {
      client.persistentVolumeClaims().create(appPersistentVolumeClaim.getResource());
    }

    if (archetypeCreatePersistentVolumes && K8SUtils.getPersistentVolumeByName(client,
        appPersistentVolume.getResource().getMetadata().getName()).isEmpty()) {
      client.persistentVolumes().create(appPersistentVolume.getResource());
    }

    if (K8SUtils.getPersistentVolumeClaimByName(client,
        databasePersistentVolumeClaim.getResource().getMetadata().getName()).isEmpty()) {
      client.persistentVolumeClaims().create(databasePersistentVolumeClaim.getResource());
    }

    if (archetypeCreatePersistentVolumes && K8SUtils.getPersistentVolumeByName(client,
        databasePersistentVolume.getResource().getMetadata().getName()).isEmpty()) {
      client.persistentVolumes().create(databasePersistentVolume.getResource());
    }

    // start database
    if (K8SUtils.getServiceByName(client, databaseService.getResource().getMetadata().getName()).isEmpty()) {
      client.services().create(databaseService.getResource());
    }
    if (K8SUtils.getStatefulSetByName(client, databaseDeployment.getResource().getMetadata().getName()).isEmpty()) {
      client.apps().statefulSets().create(databaseDeployment.getResource());
    }

    // start modeleditor app
    final var serviceOptional = K8SUtils.getServiceByName(client,
        appService.getResource().getMetadata().getName());

    final var service = serviceOptional.isEmpty()
        ? client.services().create(appService.getResource())
        : serviceOptional.get();

    final var deploymentOptional = K8SUtils.getDeploymentByName(client,
        appDeployment.getResource().getMetadata().getName());

    final var deployment = deploymentOptional.isEmpty()
        ? client.apps().deployments().create(appDeployment.getResource())
        : deploymentOptional.get();

    if (K8SUtils.getIngressByName(client, appIngressFrontend.getResource().getMetadata().getName()).isEmpty()) {
      client.network().v1().ingresses().create(appIngressFrontend.getResource());
    }
    if (K8SUtils.getIngressByName(client, appIngressBackend.getResource().getMetadata().getName()).isEmpty()) {
      client.network().v1().ingresses().create(appIngressBackend.getResource());
    }

    final var status = new ProjectDeploymentTO(appIngressFrontend.getPath(),
        ProjectDeploymentStatus.DEPLOYING);
    projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));

    waitUntilPyroPodIsReady(project, deployment, service, appIngressFrontend);

    return status;
  }

  private ProjectDeploymentTO deployLanguageEditor(ProjectDB project) {
    final var persistentVolumeClaim = new TheiaK8SPersistentVolumeClaim(client, project, pvOptions);
    final var persistentVolume = new TheiaK8SPersistentVolume(client, project, pvOptions);
    final var service = new TheiaK8SService(client, project);
    final var deployment = new TheiaK8SDeployment(client, persistentVolumeClaim, project, archetypeImage,
        environment, properties.getMinioHost(), properties.getMinioPort(), properties.getMinioAccessKey(),
        properties.getMinioSecretKey());
    final var ingress = new TheiaK8SIngress(client, service, project, host, ingressService.getWorkspaceRootPath());

    final var deployedDeploymentOptional = client.apps().statefulSets().list().getItems().stream()
        .filter(pod -> pod.getMetadata() != null)
        .filter(pod -> pod.getMetadata().getName()
            .startsWith(deployment.getResource().getMetadata().getName()))
        .findFirst();

    // do not redeploy pods if they are still active
    // if any pod removal is scheduled, that task is removed
    if (deployedDeploymentOptional.isPresent() && K8SUtils
        .isStatefulSetRunning(deployedDeploymentOptional.get())) {
      removeScheduledTasks(project);
      final var status = new ProjectDeploymentTO(ingress.getPath(),
          ProjectDeploymentStatus.READY);
      projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));
      return status;
    }

    if (K8SUtils.getPersistentVolumeClaimByName(client,
        persistentVolumeClaim.getResource().getMetadata().getName()).isEmpty()) {
      client.persistentVolumeClaims().create(persistentVolumeClaim.getResource());
    }

    if (archetypeCreatePersistentVolumes && K8SUtils
        .getPersistentVolumeByName(client, persistentVolume.getResource().getMetadata().getName())
        .isEmpty()) {
      client.persistentVolumes().create(persistentVolume.getResource());
    }

    final var editorServiceOptional = K8SUtils.getServiceByName(client,
        service.getResource().getMetadata().getName());

    final var editorService = editorServiceOptional.isEmpty()
        ? client.services().create(service.getResource())
        : editorServiceOptional.get();

    final var editorPodOptional = K8SUtils.getStatefulSetByName(client,
        deployment.getResource().getMetadata().getName());

    final var editorPod = editorPodOptional.isEmpty()
        ? client.apps().statefulSets().create(deployment.getResource())
        : editorPodOptional.get();

    if (K8SUtils.getIngressByName(client, ingress.getResource().getMetadata().getName()).isEmpty()) {
      client.network().v1().ingresses().create(ingress.getResource());
    }

    final var status = new ProjectDeploymentTO(ingress.getPath(),
        ProjectDeploymentStatus.DEPLOYING);
    projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));

    waitUntilTheaiPodIsReady(project, editorPod, editorService, ingress);

    return status;
  }

  private void waitUntilPyroPodIsReady(ProjectDB project, Deployment deployment,
      Service service, PyroAppK8SIngressFrontend ingress) {
    WaitUtils.asyncWaitUntil(
        vertx,
        () -> {
          final var s = K8SUtils.getDeploymentByName(client, deployment.getMetadata().getName());
          return s.isPresent() && K8SUtils.isDeploymentRunning(s.get());
        },
        () -> {
          final var webClient = WebClient.create(vertx);
          waitUntilAppIsReady(webClient, project, service, ingress.getPath());
        },
        () -> {
          final var s2 = new ProjectDeploymentTO(ingress.getPath(),
              ProjectDeploymentStatus.FAILED);
          CDIUtils.getBean(ProjectWebSocket.class)
              .send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
        },
        Duration.ofMinutes(TIMEOUT_TIME_MIN),
        Duration.ofSeconds(DELAY_TIME_SEC)
    );
  }

  private void waitUntilTheaiPodIsReady(ProjectDB project, StatefulSet statefulSet,
      Service service, TheiaK8SIngress ingress) {
    WaitUtils.asyncWaitUntil(
        vertx,
        () -> {
          final var s = K8SUtils.getStatefulSetByName(client, statefulSet.getMetadata().getName());
          return s.isPresent() && K8SUtils.isStatefulSetRunning(s.get());
        },
        () -> {
          final var webClient = WebClient.create(vertx);
          waitUntilAppIsReady(webClient, project, service, ingress.getPath());
        },
        () -> {
          final var s2 = new ProjectDeploymentTO(ingress.getPath(), ProjectDeploymentStatus.FAILED);
          CDIUtils.getBean(ProjectWebSocket.class)
              .send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
        },
        Duration.ofMinutes(TIMEOUT_TIME_MIN),
        Duration.ofSeconds(DELAY_TIME_SEC)
    );
  }

  private void waitUntilAppIsReady(WebClient webClient, ProjectDB project, Service service, String path) {
    WaitUtils.asyncWaitUntil(
        vertx,
        webClient
            .get(service.getSpec().getPorts().get(0).getPort(), service.getSpec().getClusterIP(),
                "")
            .send()
            .map(res -> List.of(200, 403).contains(res.statusCode())
                && res.bodyAsString().contains("theia-preload")),
        () -> {
          final var s2 = new ProjectDeploymentTO(path, ProjectDeploymentStatus.READY);
          CDIUtils.getBean(ProjectWebSocket.class)
              .send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
        },
        () -> {
          final var s2 = new ProjectDeploymentTO(path, ProjectDeploymentStatus.FAILED);
          CDIUtils.getBean(ProjectWebSocket.class)
              .send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
        },
        Duration.ofMinutes(TIMEOUT_TIME_MIN),
        Duration.ofSeconds(DELAY_TIME_SEC)
    );
  }

  public void stopModelEditor(ProjectDB project) {
    final var appService = new PyroAppK8SService(client, project);
    final var appPersistentVolume = new PyroAppK8SPersistentVolume(client, project, pvOptions);
    final var appPersistentVolumeClaim = new PyroAppK8SPersistentVolumeClaim(client, project, pvOptions);
    final var appDeployment = new PyroAppK8SDeployment(client, appPersistentVolumeClaim,
        host, environment, archetypeImage, properties.getMinioHost(), properties.getMinioPort(),
        properties.getMinioAccessKey(), properties.getMinioSecretKey(), project);
    final var appIngressFrontend = new PyroAppK8SIngressFrontend(client, appService, project, host,
        ingressService.getWorkspaceRootPath());
    final var appIngressBackend = new PyroAppK8SIngressBackend(client, appService, project, host,
        ingressService.getWorkspaceRootPath());

    final var databaseService = new PyroDatabaseK8SService(client, project);
    final var databasePersistentVolumeClaim = new PyroDatabaseK8SPersistentVolumeClaim(client,
        project, pvOptions);
    final var databaseDeployment = new PyroDatabaseK8SDeployment(client,
        databasePersistentVolumeClaim, project);

    client.services().delete(appService.getResource());
    client.apps().deployments().delete(appDeployment.getResource());
    client.network().v1().ingresses().delete(appIngressFrontend.getResource());
    client.network().v1().ingresses().delete(appIngressBackend.getResource());

    client.services().delete(databaseService.getResource());
    client.apps().statefulSets().delete(databaseDeployment.getResource());
  }

  private void stopLanguageEditor(ProjectDB project) {
    final var persistentVolumeClaim = new TheiaK8SPersistentVolumeClaim(client, project, pvOptions);
    final var service = new TheiaK8SService(client, project);
    final var deployment = new TheiaK8SDeployment(client, persistentVolumeClaim, project, archetypeImage,
        environment, properties.getMinioHost(), properties.getMinioPort(), properties.getMinioAccessKey(),
        properties.getMinioSecretKey());
    final var ingress = new TheiaK8SIngress(client, service, project, host, ingressService.getWorkspaceRootPath());

    client.services().delete(service.getResource());
    client.apps().statefulSets().delete(deployment.getResource());
    client.network().v1().ingresses().delete(ingress.getResource());
  }

  private void stopAndDeleteModelEditor(ProjectDB project) {
    stopModelEditor(project);

    // remove the claim first so that the volume can be deleted
    final var databasePersistentVolumeClaim = new PyroDatabaseK8SPersistentVolumeClaim(client, project, pvOptions);
    final var appPersistentVolumeClaim = new PyroAppK8SPersistentVolumeClaim(client, project, pvOptions);
    forceDeletePersistentVolumeClaim(databasePersistentVolumeClaim.getResource());
    forceDeletePersistentVolumeClaim(appPersistentVolumeClaim.getResource());

    if (archetypeCreatePersistentVolumes) {
      final var databasePersistentVolume = new PyroDatabaseK8SPersistentVolume(client, project, pvOptions);
      final var appPersistentVolume = new PyroAppK8SPersistentVolume(client, project, pvOptions);
      forceDeletePersistentVolume(databasePersistentVolume.getResource());
      forceDeletePersistentVolume(appPersistentVolume.getResource());
    }

    // no need to schedule pod removal if all resources are already deleted.
    removeScheduledTasks(project);
  }

  private void stopAndDeleteLanguageEditor(ProjectDB project) {
    stopLanguageEditor(project);

    // remove the claim first so that the volume can be deleted

    final var persistentVolumeClaim = new TheiaK8SPersistentVolumeClaim(client, project, pvOptions);
    forceDeletePersistentVolumeClaim(persistentVolumeClaim.getResource());

    if (archetypeCreatePersistentVolumes) {
      final var persistentVolume = new TheiaK8SPersistentVolume(client, project, pvOptions);
      forceDeletePersistentVolume(persistentVolume.getResource());
    }

    // no need to schedule pod removal if all resources are already deleted.
    removeScheduledTasks(project);
  }

  private void forceDeletePersistentVolume(PersistentVolume persistentVolume) {
    client.persistentVolumes()
            .withName(persistentVolume.getMetadata().getName())
            .withGracePeriod(0)
            .delete();
  }

  private void forceDeletePersistentVolumeClaim(PersistentVolumeClaim persistentVolumeClaim) {
    client.persistentVolumeClaims()
            .inNamespace(client.getNamespace())
            .withName(persistentVolumeClaim.getMetadata().getName())
            .withGracePeriod(0)
            .delete();
  }

  private void removeScheduledTasks(ProjectDB project) {
    StopProjectPodsTaskDB.delete("projectId", project.id);
  }
}

# Overview

## Repositories

There are several repositories involved in the concept of Cinco Cloud.
Here you see a quick overview of all the repositories and their relations:

![Cinco Cloud Repositories](./assets/repository-overview.png)

[**Cinco Cloud**](https://gitlab.com/scce/cinco-cloud):
The *Cinco-Cloud repository* contains the *main-app*, which orchestrates the image deployment of editors and functions as central administration instances for the user- and project management.

[**Cinco Cloud Archetype**](https://gitlab.com/scce/cinco-cloud-archetype):
The *Cinco-Cloud Archetype repository* holds the sources for the static editor, which is based on *Eclipse Theia*, including all *theia and visual-studio-code* based *extensions*.
One of these extensions can execute the *Cinco Language Server*, thus the project also contains the *Cinco Language Server*-repository as a submodule.
Since this repository represents the most basic deployable Editor for a Cinco-Cloud project, it is continuously built to a docker-image via a CICD pipeline and put into a docker registry (registry.gitlab.com/scce/cinco-cloud-archetype/archetype:latest).
This image will be reused and further expressed for new *Cinco Cloud projects*.

[**Cinco Language Server**](https://gitlab.com/scce/cinco-language-server):
The *Cinco language Server* itself first and foremost is a standalone Java executable.
It serves language support for the *Cinco Languages* (CPD, MGL, Style/MSL) and a Generator to express new *Cinco Cloud Projects* based on these languages.
A great part of this repository is based on the *Cinco*-repository, but without the *Eclipse-IDE dependencies*.

[**Pyro Generator**](https://gitlab.com/scce/pyro-generator):
The *Pyro Generator* is a maven-submodule for both *Cinco* and the *Cinco Language Server*.
It expresses *Cinco Cloud Projects* based on the *Cinco Languages*.
It is based on the formally known *Pyro*-repository.

## Architecture

![Cinco Cloud Architecture](./assets/architecture.png)

### Services

In the following, you can find a list of services that CincoCloud is composed of.

**Main Service** Forked from the Pyro code base, the main service is composed of a Postgres database, a [Quarkus][quarkus] backend and an [AngularDart][angular-dart] frontend that are connected through a RESTful API.
The service can be understood as the orchestrator of the displayed service landscape.
State is stored and managed soley in the database of the *Main Service*, other services can request and manipulate data through a [GRPC][grpc] interface that is exposed.
Further, the service is connected to the Kubernetes API of the cluster which is used for the orchestration of workspaces, i.e. it handles the creation and the garbage collection of inactive workspaces.

**ArtemisMQ** The Artemis service is a message queue that receives jobs from the *Main Service*.
A job contains information on how to build an image for the modelling environment.

**Workspace Builder** Services of this kind build images for concrete Pyro-based modelling environments.
They are connected to the message queue and receive jobs based on which images are build and finally pushed to the registry.
*Workspace Builder* services are ephemeral, meaning they have no internal state such as a database and can be scaled according to the current work load.
Each service can only work on one job at a time.

**Image Registry** The registry service is composed of a [Podman][podman] image registry and a Python web service.
The registry contains images build by the *Workspace Builder* for generated modeling environments.
Additionally, the Python web service offers a single endpoint for triggering the garbage collection of the registry by the *Main Service*.

**Theia Editor**
The *Theia Editor* is a framework for IDEs, based on [Eclipse Theia](https://github.com/eclipse-theia/theia). It represents the artifact that can be built by the *Workspace Builder* and deployed by the *Kubernetes API*. Its most basic form for the *Cinco-Cloud*, without specialization for a language, can be found inside the [*Cinco Cloud Archetype*](https://gitlab.com/scce/cinco-cloud-archetype).

## Secrets

CincoCloud uses private GitLab repositories and its container registries.
In order to pull images from these registries, we need to have the following three secrets (see the instruction on how to create and apply them [here](../installation/)) in the cluster:

* `cinco-cloud-registry-secret` <br>
  Needed so that images for the *main service*, the *workspace builder* and the *image registry* can be pulled from the [Cinco Cloud Container Registry][cinco-cloud-container-registry] from within the Kubernetes cluster.
  The secret has the type `kubernetes.io/dockerconfigjson`.

* `cinco-cloud-archetype-registry-secret` <br>
  Needed so that images for the *Theia editor* can be pulled from the [Cinco Cloud Archetype Container Registry][cinco-cloud-archetype-container-registry] from within the cluster.
  The secret has the type `kubernetes.io/dockerconfigjson`.

* `cinco-cloud-archetype-registry-credentials` <br>
  Needed by the *workspace builder* and contains the base64 encoded username and password to login to the [Cinco Cloud Archetype Container Registry][cinco-cloud-archetype-container-registry] in order to pull the archetype image.
  The secret is of type `opaque`.


[cinco-cloud-archetype-container-registry]: https://gitlab.com/scce/cinco-cloud-archetype/container_registry
[cinco-cloud-container-registry]: https://gitlab.com/scce/cinco-cloud/container_registry
[grpc]: https://grpc.io/
[podman]: https://podman.io/
[quarkus]: https://quarkus.io/
[angular-dart]: https://github.com/angulardart/angular
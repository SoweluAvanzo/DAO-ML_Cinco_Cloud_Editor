package info.scce.cincocloud.k8s.shared;

public class K8SPersistentVolumeOptions {

    public final String storageClassName;

    public final String storage;

    public final String hostPath;

    public final boolean createPersistentVolumes;

    public K8SPersistentVolumeOptions(String storageClassName, String storage, String hostPath, boolean createPersistentVolumes) {
        this.storageClassName = storageClassName;
        this.storage = storage;
        this.hostPath = hostPath;
        this.createPersistentVolumes = createPersistentVolumes;
    }
}

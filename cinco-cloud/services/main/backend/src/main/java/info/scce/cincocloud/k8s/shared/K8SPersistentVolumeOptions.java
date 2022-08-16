package info.scce.cincocloud.k8s.shared;

public class K8SPersistentVolumeOptions {

    public String storageClassName;

    public String storage;

    public String hostPath;

    public K8SPersistentVolumeOptions(String storageClassName, String storage, String hostPath) {
        this.storageClassName = storageClassName;
        this.storage = storage;
        this.hostPath = hostPath;
    }
}

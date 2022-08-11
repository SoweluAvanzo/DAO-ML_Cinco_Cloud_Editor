package info.scce.cincocloud.k8s.shared;

public class K8SPersistentVolumeOptions {

    public String storageClassName;

    public String storage;

    public K8SPersistentVolumeOptions(String storageClassName, String storage) {
        this.storageClassName = storageClassName;
        this.storage = storage;
    }
}

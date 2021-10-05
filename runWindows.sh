#sh
minikube start --cpus 4 --memory 8192 --driver=hyperv --disk-size 60000mb
minikube addons enable default-storageclass && minikube addons enable ingress && minikube addons enable ingress-dns && minikube addons enable storage-provisioner
kubectl apply -f secrets.yaml
skaffold dev

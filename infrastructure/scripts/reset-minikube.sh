#!/usr/bin/env bash
set -euox pipefail
minikube delete
minikube start --cpus 4 --memory 8192 --disk-size 60000mb --driver=docker
minikube addons enable default-storageclass
minikube addons enable ingress
minikube addons enable ingress-dns
minikube addons enable storage-provisioner
kubectl apply -f infrastructure/helm/secrets-local.yaml

# Cert Manager
helm repo add jetstack https://charts.jetstack.io
helm repo update
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.2/cert-manager.crds.yaml
helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.13.2

echo -n Now run "skaffold dev -p local-dev" in another terminal. Press enter when Skaffold is deployed.
read

kubectl get secret cinco-cloud-local-ca-cert-secret -o jsonpath={.data.'tls\.crt'} | base64 -d > cinco-cloud-local-rootCA.pem
echo -n Add cinco-cloud-local-rootCA.pem to your browser"'"s certificate store. Press enter to continue.
read

kubectl port-forward release-minio-0 9001:9001 &
xdg-open http://localhost:9001
echo -n Configure Minio. Press enter to continue.
read
kill -SIGINT $!
kubectl delete pods -l app=main

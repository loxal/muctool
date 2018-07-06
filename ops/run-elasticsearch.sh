#!/usr/bin/env sh

sudo kubeadm reset
sudo apt-get remove kubectl kubeadm kubelet kube* --purge -y
sudo apt-get autoremove --purge -y
sudo rm -rf /etc/cni
sudo rm -rf /opt/cni
sudo rm -rf /etc/kubernetes
sudo rm -rf /var/lib/etcd
sudo rm -rf /var/lib/kubelet
sudo rm -rf /var/lib/etcd
sudo rm -rf /var/lib/dockershim
sudo rm -rf /var/run/kubernetes

sudo apt-get install -y kubeadm=1.10.5-00 kubelet=1.10.5-00 kubectl=1.10.5-00

sudo kubeadm reset
sudo kubeadm init
sudo mkdir ~/.kube
sudo cp /etc/kubernetes/admin.conf ~/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
kubectl get deployments,svc,nodes,pods --all-namespaces

sudo systemctl restart kubelet
sudo systemctl daemon-reload

kubeadm upgrade apply v1.11.0

kubectl apply -f https://docs.projectcalico.org/v3.1/getting-started/kubernetes/installation/hosted/kubeadm/1.7/calico.yaml
kubectl run kubernetes-bootcamp --image=gcr.io/google-samples/kubernetes-bootcamp:v1 --port=8080
kubectl proxy

export POD_NAME=$(kubectl get pods -o go-template --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}')
echo Name of the Pod: $POD_NAME

curl http://localhost:8001/version
curl http://localhost:8001/api/v1/namespaces/default/pods/$POD_NAME/proxy/

kubectl expose deployment/kubernetes-bootcamp --type="NodePort" --port 8080
curl http://10.104.46.119:9200/_cluster/health

kubectl logs $POD_NAME
kubectl exec -ti $POD_NAME bash
curl localhost:8080

kubectl get pods -l run=kubernetes-bootcamp
kubectl get services -l run=kubernetes-bootcamp
kubectl delete service -l run=kubernetes-bootcamp
kubectl delete deployment -l run=kubernetes-bootcamp
kubectl exec -ti $POD_NAME curl localhost:8080
#!/usr/bin/env sh

# TODO not tested yet

add-apt-repository "deb [arch=amd64] https://apt.kubernetes.io kubernetes-xenial main"
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
apt-get update && apt-get install docker-ce=18.06.1~ce~3-0~debian kubeadm -y

kubeadm init
mkdir -p $HOME/.kube && cp -i /etc/kubernetes/admin.conf $HOME/.kube/config && chown $(id -u):$(id -g) $HOME/.kube/config
kubectl apply -f https://docs.projectcalico.org/v3.3/getting-started/kubernetes/installation/hosted/etcd.yaml
kubectl apply -f https://docs.projectcalico.org/v3.3/getting-started/kubernetes/installation/rbac.yaml
kubectl apply -f https://docs.projectcalico.org/v3.3/getting-started/kubernetes/installation/hosted/calico.yaml
kubectl create deployment router --image=nginx:stable
#kubectl create service loadbalancer router --tcp=80:80
#kubectl create service nodeport router --tcp=80:80
kubectl expose deployment router --type=LoadBalancer --name=router --port=80
kubectl taint nodes --all node-role.kubernetes.io/master-
kubectl get svc,node,pvc,deployment,pods
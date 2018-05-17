#!/usr/bin/env sh

# TODO not tested yet

sudo apt-get update && sudo apt-get install -y apt-transport-https
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -

sudo sh -c 'cat <<EOF > /etc/apt/sources.list.d/kubernetes.list
deb http://packages.cloud.google.com/apt cloud-sdk-stretch main
EOF'

sudo apt-get update
sudo apt-get install -y kubectl kubeadm kubelet
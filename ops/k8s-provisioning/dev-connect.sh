#!/usr/bin/env sh

#k8s_master_node=$(terraform output k8s_master_node)
k8s_master_node=116.203.228.233
tenant=kube-system

ssh-keygen -f ~/.ssh/known_hosts -R $k8s_master_node
ssh-keygen -R $k8s_master_node
screen -wipe
ssh -o StrictHostKeyChecking=no root@$k8s_master_node pkill kubectl

screen -dmS elasticsearch ssh root@$k8s_master_node kubectl port-forward service/elasticsearch 9200:9200 -n $tenant

ssh -fNL 9200:localhost:9200 root@$k8s_master_node

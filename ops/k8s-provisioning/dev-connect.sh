#!/usr/bin/env sh

k8s_master_node=es.sitesearch.cloud
tenant=kube-system

ssh-keygen -f ~/.ssh/known_hosts -R $k8s_master_node
ssh-keygen -R $k8s_master_node
screen -wipe
ssh -o StrictHostKeyChecking=no root@$k8s_master_node pkill kubectl

screen -dmS elasticsearch ssh root@$k8s_master_node kubectl port-forward service/elasticsearch 9200:9200 -n $tenant

ssh -o StrictHostKeyChecking=no -fNL 9200:localhost:9200 root@$k8s_master_node

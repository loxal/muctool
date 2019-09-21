#!/usr/bin/env sh

workspace=default
k8s_master_node=sky.loxal.net
helmName=muctool

scp -q -o StrictHostKeyChecking=no root@$k8s_master_node:/etc/letsencrypt/live/loxal.net/cert.pem asset/$helmName
scp -q -o StrictHostKeyChecking=no root@$k8s_master_node:/etc/letsencrypt/live/loxal.net/privkey.pem asset/$helmName

ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node rm -rf /opt/$helmName
scp -q -o StrictHostKeyChecking=no -r asset/$helmName root@$k8s_master_node:/opt/$helmName

ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node helm delete $helmName --purge
ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node helm delete ingress --purge
sleep 13

#  helm upgrade $helmName /opt/$helmName --namespace $workspace \
ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node \
  helm upgrade $helmName /opt/$helmName --install --namespace $workspace --recreate-pods \
  --set app.tenant=$workspace,app.HETZNER_API_TOKEN=$TF_VAR_hetzner_cloud_muctool \
  --set app.dockerRegistrySecret=$TF_VAR_docker_registry_k8s_secret, \
  --set app.meta.scmHash=$SCM_HASH,app.meta.buildNumber=$BUILD_NUMBER, \
  --set-string app.volumeHandle=123

#ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node \
#  helm install --name ingress stable/nginx-ingress \
#  --set rbac.create=true,controller.hostNetwork=true,controller.kind=DaemonSet
#ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node \
#  helm upgrade ingress stable/nginx-ingress \
#  --set rbac.create=true,controller.hostNetwork=true,controller.kind=DaemonSet

#ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node helm test $helmName --cleanup
#ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node helm list --all

if [ "$(whoami)" = "alex" ]
then
  ssh -q -o StrictHostKeyChecking=no minion@$k8s_master_node
fi
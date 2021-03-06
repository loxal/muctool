#!/usr/bin/env sh

workspace=kube-system
k8s_master_node=sky.loxal.net
helmName=muctool

scp -q -o StrictHostKeyChecking=no root@$k8s_master_node:/etc/letsencrypt/live/loxal.net/fullchain.pem asset/$helmName
scp -q -o StrictHostKeyChecking=no root@$k8s_master_node:/etc/letsencrypt/live/loxal.net/privkey.pem asset/$helmName

ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node \
  kubectl create secret docker-registry docker-registry-hub --docker-server docker.io --docker-username loxal --docker-password $DOCKER_PASSWORD -n kube-system
ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node rm -rf /opt/$helmName
scp -q -o StrictHostKeyChecking=no -r asset/$helmName root@$k8s_master_node:/opt/$helmName

#ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node helm delete $helmName --purge
#ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node helm delete ingress --purge
#sleep 13

ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node \
  helm upgrade $helmName /opt/$helmName --install --namespace $workspace --recreate-pods \
  --set app.tenant=$workspace,app.HETZNER_API_TOKEN=$TF_VAR_hetzner_cloud_muctool \
  --set app.dockerRegistrySecret=$DOCKER_REGISTRY_CREDENTIALS_BASE64, \
  --set app.basicAuthBase64=$BASE64_ENCODED_HTPASSWD \
  --set app.meta.scmHash=$SCM_HASH,app.meta.buildNumber=$BUILD_NUMBER, \
  --set app.adminSecret=$ADMIN_SITE_SECRET \
  --set app.serviceSecret=$SERVICE_SECRET, \
  --set app.devSkipFlag=$DEV_SKIP_FLAG, \
  --set app.recaptchaSecret=$INVISIBLE_RECAPTCHA_SITE_SECRET \
  --set-string app.volumeHandle=0

#ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node \
#  helm upgrade ingress stable/nginx-ingress --install --namespace $workspace \
#  --set rbac.create=true,controller.hostNetwork=true,controller.kind=DaemonSet

#ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node helm test $helmName --cleanup
ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node helm list --all

if [ "$(whoami)" = "alex" ]; then
  #  ssh -q -o StrictHostKeyChecking=no root@$k8s_master_node
  kubectl get svc,node,pvc,deployment,pods,pvc,pv,namespace,job -A && helm list
fi

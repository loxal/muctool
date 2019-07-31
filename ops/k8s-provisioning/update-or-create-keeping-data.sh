#!/usr/bin/env sh

echo "Terraform workspace: `terraform output`"
password=`terraform output password`
terraform taint hcloud_server.master
terraform taint hcloud_server.node
terraform taint hcloud_server.master[0]
terraform taint hcloud_server.node[0]
terraform taint hcloud_server.node[1]
terraform apply -auto-approve \
    -var password=$password \
    $1

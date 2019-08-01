#!/usr/bin/env sh

#docker run -w /app -v $(pwd):/app --entrypoint /usr/bin/env -it hashicorp/terraform:light sh
#rm -rf ./terraform
#terraform init

echo "Terraform workspace: `terraform output`"
password=`terraform output password`
sleep 1
terraform destroy -auto-approve
terraform apply -auto-approve \
    -var password=$password \
    $1

`terraform output k8s_ssh`

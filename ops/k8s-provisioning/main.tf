resource "hcloud_network" "cluster" {
  name     = "k8s-${terraform.workspace}"
  ip_range = "10.0.0.0/8"
}
resource "hcloud_network_route" "ingres" {
  network_id  = hcloud_network.cluster.id
  destination = "10.100.1.0/24"
  gateway     = "10.0.2.1"
}
resource "hcloud_network_subnet" "tenant" {
  network_id   = hcloud_network.cluster.id
  type         = "server"
  network_zone = "eu-central"
  ip_range     = "10.0.2.0/24"
}
//resource "hcloud_server_network" "minion" {
//  network_id = hcloud_network.cluster.id
//  server_id  = hcloud_server.minion[0].id
//  ip         = "10.0.2.42"
//}
//resource "hcloud_server_network" "controller" {
//  network_id = hcloud_network.cluster.id
//  server_id  = hcloud_server.controller[0].id
//  ip         = "10.0.2.23"
//}

resource "null_resource" "update-migration" {
  depends_on = [
    hcloud_server.minion
  ]
  connection {
    host        = hcloud_server.controller[0].ipv4_address
    private_key = file("~/.ssh/id_rsa")
  }
  triggers = {
    backup = local.updateTrigger
  }
  provisioner "remote-exec" {
    inline = [
      "helm install /srv/asset/page-finder --name ${terraform.workspace} --namespace ${terraform.workspace} --set app.TENANT=${terraform.workspace},app.HETZNER_API_TOKEN=${var.hetzner_cloud_muctool}",
      //      "kubectl apply -f https://raw.githubusercontent.com/hetznercloud/csi-driver/master/deploy/kubernetes/hcloud-csi.yml",
      "kubectl get svc,node,pvc,deployment,pods,pvc,pv,namespace -A",
      //      "sh /srv/etl-safe-datastore-model-migration-hook.sh ${terraform.workspace} # safe ETL hook to migrate & update datastore schema",
    ]
  }
}

variable "password" {
  type = string
}

variable "hetzner_cloud_muctool" {
  type = string
}

locals {
  dcLocation    = "nbg1"
  dc            = "nbg1-dc3"
  updateTrigger = timestamp()
  password      = var.password == "" ? uuid() : var.password
}

output "k8s_controller" {
  value = [
    hcloud_server.controller.*.ipv4_address
  ]
}

output "password" {
  value = local.password
}

output "updated" {
  value = local.updateTrigger
}

output "k8s_ssh" {
  value = "ssh -o StrictHostKeyChecking=no root@${hcloud_server.controller[0].ipv4_address}"
}

output "k8s_minion" {
  value = [
    hcloud_server.minion.*.ipv4_address
  ]
}

provider "hcloud" {
  token = var.hetzner_cloud_muctool
}

resource "hcloud_server" "minion" {
  location = local.dcLocation
  labels = {
    password = local.password
  }
  name        = "${terraform.workspace}-minion-${count.index}"
  count       = "1"
  image       = "debian-9"
  server_type = "cx21-ceph"
  ssh_keys = [
    "alex",
  ]

  provisioner "local-exec" {
    command = "cat << EOF >> ~/.bash_ssh_connections\nalias muc-${terraform.workspace}-minion-${count.index}='ssh -o StrictHostKeyChecking=no root@${hcloud_server.minion[count.index].ipv4_address}'\n"
  }

  provisioner "remote-exec" {
    connection {
      host        = self.ipv4_address
      private_key = file("~/.ssh/id_rsa")
    }

    inline = [
      "echo 'root:${local.password}' | chpasswd",
      "apt-get update && apt-get install -y curl software-properties-common",
      "curl -s https://download.docker.com/linux/debian/gpg | apt-key add -",
      "curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -",
      "add-apt-repository \"deb [arch=amd64] https://packages.cloud.google.com/apt kubernetes-xenial main\"",
      "add-apt-repository \"deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable\"",
      "apt-get update && apt-get install rsync docker-ce kubeadm sshpass cryptsetup busybox -y",
      "sed -i -e 's/%sudo	ALL=(ALL:ALL) ALL/%sudo	ALL=(ALL:ALL) NOPASSWD:ALL/g' /etc/sudoers",
      //      "iptables -A INPUT -p tcp --match multiport -s 0/0 -d ${hcloud_server.controller[count.index].ipv4_address} --dports 22,80,179,443,2080,2379,4789,5473,6443,8080,9200,9602,9603,6040:55923 -m state --state NEW,ESTABLISHED -j ACCEPT",
      //      "iptables -A OUTPUT -p tcp -s ${hcloud_server.controller[count.index].ipv4_address} -d 0/0 --match multiport --sports 22,80,179,443,2080,2379,4789,5473,6443,8080,9200,9602,9603,6040:55923 -m state --state ESTABLISHED -j ACCEPT",
      "sshpass -p ${local.password} scp -o StrictHostKeyChecking=no root@${hcloud_server.controller[count.index].ipv4_address}:/srv/kubeadm_join /tmp && eval $(cat /tmp/kubeadm_join)",
    ]
  }
}

resource "hcloud_server" "controller" {
  location = local.dcLocation
  labels = {
    password = local.password
  }
  name        = "${terraform.workspace}-controller-${count.index}"
  count       = "1"
  image       = "debian-9"
  server_type = "cx21-ceph"
  ssh_keys = [
    "alex",
  ]

  provisioner "local-exec" {
    command = "cat << EOF >> ~/.bash_ssh_connections\nalias muc-${terraform.workspace}='ssh -o StrictHostKeyChecking=no root@${hcloud_server.controller[count.index].ipv4_address}'\n"
  }

  provisioner "file" {
    connection {
      host        = self.ipv4_address
      private_key = file("~/.ssh/id_rsa")
    }
    source      = "asset"
    destination = "/srv/asset"
  }

  provisioner "remote-exec" {
    connection {
      host        = self.ipv4_address
      private_key = file("~/.ssh/id_rsa")
    }

    inline = [
      "apt-get update && apt-get install -y curl software-properties-common",
      "curl -s https://download.docker.com/linux/debian/gpg | apt-key add -",
      "curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -",
      "add-apt-repository \"deb [arch=amd64] https://packages.cloud.google.com/apt kubernetes-xenial main\"",
      "add-apt-repository \"deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable\"",
      "apt-get update && apt-get install rsync docker-ce kubeadm -y",
      "sed -i -e 's/%sudo	ALL=(ALL:ALL) ALL/%sudo	ALL=(ALL:ALL) NOPASSWD:ALL/g' /etc/sudoers",
      "adduser --disabled-password --gecos '' minion && usermod -aG sudo minion && usermod --unlock minion",
      "echo 'minion:${local.password}' | chpasswd",
      "echo 'root:${local.password}' | chpasswd",
      "kubeadm init",
      "mkdir -p $HOME/.kube && cp -i /etc/kubernetes/admin.conf $HOME/.kube/config && chown $(id -u):$(id -g) $HOME/.kube/config",
      "kubectl apply -f https://docs.projectcalico.org/v3.8/getting-started/kubernetes/installation/hosted/kubernetes-datastore/calico-networking/1.7/calico.yaml",
      "kubectl taint nodes --all node-role.kubernetes.io/master- # override security and enable scheduling of pods on master",
      "kubeadm token create --print-join-command > /srv/kubeadm_join",
      "kubectl apply -f https://raw.githubusercontent.com/hetznercloud/csi-driver/master/deploy/kubernetes/hcloud-csi.yml",
      "kubectl apply -f /srv/asset/init-helm-rbac-config.yaml",
      "curl -L https://git.io/get_helm.sh | bash && helm init",
      //      "iptables -A INPUT -p tcp --match multiport -s 0/0 -d ${hcloud_server.controller[count.index].ipv4_address} --dports 22,80,179,443,2080,2379,4789,5473,6443,8080,9200,9602,9603,6040:55923 -m state --state NEW,ESTABLISHED -j ACCEPT",
      //      "iptables -A OUTPUT -p tcp -s ${hcloud_server.controller[count.index].ipv4_address} -d 0/0 --match multiport --sports 22,80,179,443,2080,2379,4789,5473,6443,8080,9200,9602,9603,6040:55923 -m state --state ESTABLISHED -j ACCEPT",
      "kubectl get svc,node,pvc,deployment,pods,pvc,pv,namespace,serviceaccount,clusterrolebinding -A",
    ]
  }
}

resource "hcloud_rdns" "minion" {
  server_id  = hcloud_server.minion[0].id
  ip_address = hcloud_server.minion[0].ipv4_address
  dns_ptr    = "${hcloud_server.minion[0].name}.muctool.de"
}

resource "hcloud_rdns" "controller" {
  server_id  = hcloud_server.controller[0].id
  ip_address = hcloud_server.controller[0].ipv4_address
  dns_ptr    = "${hcloud_server.controller[0].name}.muctool.de"
}

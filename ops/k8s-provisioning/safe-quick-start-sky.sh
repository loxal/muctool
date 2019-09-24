#!/usr/bin/env sh

sh ../../blockchain/run-komodo.sh
sh ../../blockchain/run-waves.sh
sh ../../blockchain/run-zcash.sh

kubectl apply -f asset/teamcity-ci.yaml --namespace kube-system
./helm-update.sh
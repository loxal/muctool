#!/usr/bin/env sh

kubectl apply -f asset/teamcity-ci.yaml --namespace kube-system
./helm-update.sh
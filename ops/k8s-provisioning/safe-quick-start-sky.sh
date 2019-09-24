#!/usr/bin/env sh

kubectl apply -f asset/teamcity-ci.yaml
./helm-update.sh
#!/usr/bin/env bash

# Switch Java JDK
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)

./mvnw clean package cf:push -P cf-aws-stage


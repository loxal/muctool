#!/usr/bin/env bash

# Switch Java JDK
export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)

./mvnw clean package appengine:update

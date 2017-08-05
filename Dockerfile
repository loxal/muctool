FROM openjdk:8-jre-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

### execute as non-root user
ENV SVC_USR svc_usr
RUN adduser -D -g $SVC_USR $SVC_USR
USER $SVC_USR
WORKDIR /home/$SVC_USR
### /execute as non-root user

ADD build/libs/*.jar .

COPY static .
#COPY static build/resources/main/static
RUN mkdir -p build/resources/main
COPY build/resources/main/keystore.jks build/resources/main
COPY build/resources/main/*.mmdb build/resources/main/

EXPOSE 1443 1180

CMD java -jar -Xmx32m *.jar

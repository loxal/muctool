FROM openjdk:8-jre-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

### execute as non-root user
ENV SVC_USR svc_usr
RUN adduser -D -g $SVC_USR $SVC_USR
USER $SVC_USR
WORKDIR /home/$SVC_USR
### /execute as non-root user

ADD build/libs/*.jar .

COPY static static
RUN mkdir -p src/main/resources
RUN mkdir logs
COPY src/main/resources/keystore.jks src/main/resources
COPY src/main/resources/*.mmdb src/main/resources/

VOLUME /home/svc_usr/logs /home/svc_usr/data
EXPOSE 1443
#EXPOSE 1443 1180

CMD java -jar -Xmx32m *.jar

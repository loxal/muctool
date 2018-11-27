FROM openjdk:11-jre AS service

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

### execute as non-root user # caused Exception in thread "main" java.net.SocketException: Permission denied
#ENV SVC_USR svc_usr
#RUN adduser -D -g $SVC_USR $SVC_USR
#USER $SVC_USR
#WORKDIR /home/$SVC_USR
### /execute as non-root user

WORKDIR /srv
COPY build/libs/*.jar .

COPY static static
RUN mkdir -p src/main/resources
RUN mkdir logs
COPY src/main/resources/*.mmdb src/main/resources/

VOLUME /logs /data
EXPOSE 1180
HEALTHCHECK CMD curl --fail http://localhost:1180 || exit 1

CMD ["java", "-jar", "-Xmx32m", "service-1.0.0.jar"]

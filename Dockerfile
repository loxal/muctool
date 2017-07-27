FROM openjdk:8-jre-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

### execute as non-root user
ENV SVC_USR svc_usr
RUN adduser -D -g $SVC_USR $SVC_USR
USER $SVC_USR
WORKDIR /home/$SVC_USR
### /execute as non-root user

RUN mkdir -p build/resources/main
ADD build/libs/*.jar .
COPY build/resources/main/static build/resources/main/static

EXPOSE 8300

CMD java -jar -Xmx32m *.jar

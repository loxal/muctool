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
#RUN mkdir -p build/resources/main
RUN mkdir -p src/main/resources
COPY src/main/resourceskeystore.jks src/main/resources
#COPY build/resources/main/keystore.jks build/resources/main
COPY src/main/resources/*.mmdb src/main/resources/

EXPOSE 1443 1180

CMD java -jar -Xmx32m *.jar

FROM openjdk:8-jre-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

### execute as non-root user
ENV SVC_USR svc_usr
RUN adduser -D -g $SVC_USR $SVC_USR
USER $SVC_USR
WORKDIR /home/$SVC_USR
### /execute as non-root user

ADD build/install/* svc/
ADD build/resources/main/static svc/src/main/resources/static
RUN ls -lash ~/svc; ls -lash ~/svc/bin; ls -lash ~/svc/lib; ls -lash ~/svc/src/main/resources/static

EXPOSE 8300

CMD ~/svc/bin/muctool

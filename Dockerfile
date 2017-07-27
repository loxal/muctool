FROM openjdk:8-jre-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

### execute as non-root user
ENV APP_USER app_user
RUN adduser -D -g $APP_USER $APP_USER
USER $APP_USER
WORKDIR /home/$APP_USER
### /execute as non-root user

ADD build/libs/*.jar app/

EXPOSE 8300

CMD java -jar -Xmx64m app/*.jar

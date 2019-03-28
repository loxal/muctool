FROM openjdk:13-alpine AS service

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

WORKDIR /srv
COPY build/libs/*.jar .

COPY static static
RUN mkdir -p src/main/resources
RUN mkdir logs
COPY src/main/resources/*.mmdb src/main/resources/

VOLUME /logs /data
EXPOSE 1180
HEALTHCHECK CMD curl --fail http://localhost:1180 || exit 1

CMD ["java", "-jar", "-Xms128m", "-Xmx128m", "service-1.0.0.jar"]

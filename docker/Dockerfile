FROM osgeo/gdal:alpine-normal-3.1.2
LABEL maintainer="juan@gkudos.com"

FROM quay.io/devfile/maven:3.8.1-openjdk-11-slim
WORKDIR /build

# Build dependency offline to streamline build
COPY pom.xml .
COPY settings.xml .
COPY src src
RUN mvn -s settings.xml dependency:go-offline package -Dmaven.test.skip=true

RUN cp ./target/xtf-to-json-1.0.jar xtf-to-json-1.0.jar

EXPOSE 8081
ENTRYPOINT [ "java", "-jar", "xtf-to-json-1.0.jar", "--server.port=8081" ]


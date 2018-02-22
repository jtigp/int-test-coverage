FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD target/multi-module-server-1.0-SNAPSHOT.jar /app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-javaagent:/jacoco/lib/jacocoagent.jar=output=tcpserver,address=*"

ENV md5 c435884e5e7f8b600406ab829ee9302f
ENV jacoco_version 0.8.0

RUN apk update && apk add curl && apk add unzip && \
    curl -f https://repo1.maven.org/maven2/org/jacoco/jacoco/$jacoco_version/jacoco-$jacoco_version.zip -o jacoco.zip && \
    sum=$(cat jacoco.zip | md5sum | cut -d ' ' -f 1) && \
    echo $sum && \
    if [ ! $sum == $md5 ]; then exit 1; fi && \
    mkdir /jacoco && \
    unzip jacoco.zip -d /jacoco && \
    rm jacoco.zip

VOLUME "/jacoco"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar /app.jar" ]
# 基础镜像
FROM  registry.cn-beijing.aliyuncs.com/hummerrisk/alpine:jre17
# author
MAINTAINER Hummer
ARG HMR_VERSION=dev

COPY ./jar/hummer-gateway.jar /opt/apps/hummer-gateway.jar

ENV JAVA_APP_JAR=/opt/apps/hummer-gateway.jar \
    AB_OFF=true \
    HMR_VERSION=${HMR_VERSION} \
    JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true -DHMR_NACOS_SERVER_ADDR=127.0.0.1"

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:8080

CMD ["/deployments/run-java.sh"]

EXPOSE 8080

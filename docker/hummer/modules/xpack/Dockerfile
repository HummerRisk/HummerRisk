# 基础镜像
FROM  registry.cn-beijing.aliyuncs.com/hummerrisk/alpine:jre17
# author
MAINTAINER Hummer
ARG HMR_VERSION=dev

COPY ./jar/hummer-modules-xpack.jar /opt/apps/hummer-modules-xpack.jar

ENV JAVA_APP_JAR=/opt/apps/hummer-modules-xpack.jar \
    AB_OFF=true \
    HMR_VERSION=${HMR_VERSION} \
    JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true -DHMR_NACOS_SERVER_ADDR=127.0.0.1 -DHMR_MYSQL_HOST=127.0.0.1 -DHMR_MYSQL_PORT=3306 -DHMR_MYSQL_DB=hummer_risk -DHMR_MYSQL_USER=root -DHMR_MYSQL_PASSWORD=root"

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:9600

CMD ["/deployments/run-java.sh"]

EXPOSE 9600

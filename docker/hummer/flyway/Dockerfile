# 基础镜像
FROM  registry.cn-beijing.aliyuncs.com/hummerrisk/alpine:jre17
# author
MAINTAINER Hummer
ARG HMR_VERSION=dev

COPY ./jar/hummer-flyway.jar /opt/apps/hummer-flyway.jar

ENV JAVA_APP_JAR=/opt/apps/hummer-flyway.jar \
    AB_OFF=true \
    HMR_VERSION=${HMR_VERSION} \
    JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true -DHMR_MYSQL_HOST=127.0.0.1 -DHMR_MYSQL_PORT=3306 -DHMR_MYSQL_DB_NACOS=hummer_config -DHMR_MYSQL_USER=root -DHMR_MYSQL_PASSWORD=root"

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:9000

CMD ["/deployments/run-java.sh"]

EXPOSE 9000

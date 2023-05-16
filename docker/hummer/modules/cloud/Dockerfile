
FROM registry.cn-beijing.aliyuncs.com/hummerrisk/prowler:v1.1 as prowler-env

FROM registry.cn-beijing.aliyuncs.com/hummerrisk/cloud-custodian:v1.1.0 as custodian-env
# 基础镜像
FROM registry.cn-beijing.aliyuncs.com/hummerrisk/ubuntu22-jre17:py3.8.10

# author
MAINTAINER Hummer

ARG HMR_VERSION=dev

WORKDIR /usr

COPY --from=prowler-env /prowler /prowler

# custodian
COPY --from=custodian-env /src /src

COPY --from=custodian-env /usr/local /usr/local

COPY --from=custodian-env /output /output

RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install -i /usr/local/aws-cli -b /usr/local/bin && \
    mkdir -p /opt/apps

COPY ./jar/hummer-modules-cloud.jar /opt/apps/hummer-modules-cloud.jar

ENV JAVA_APP_JAR=/opt/apps/hummer-modules-cloud.jar \
    AB_OFF=true \
    HMR_VERSION=${HMR_VERSION} \
    JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true -DHMR_NACOS_SERVER_ADDR=127.0.0.1 -DHMR_MYSQL_HOST=127.0.0.1 -DHMR_MYSQL_PORT=3306 -DHMR_MYSQL_DB=hummer_risk -DHMR_MYSQL_USER=root -DHMR_MYSQL_PASSWORD=root"

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:9400

CMD ["/deployments/run-java.sh"]

EXPOSE 9400

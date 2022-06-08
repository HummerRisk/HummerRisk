FROM registry.cn-beijing.aliyuncs.com/hummercloud/nuclei:v1.0.0 as nuclei-env

FROM registry.cn-beijing.aliyuncs.com/hummercloud/prowler:v1.0.0 as prowler-env

FROM registry.cn-beijing.aliyuncs.com/hummercloud/custodian:v1.0.0

ARG HR_VERSION=dev

RUN apk add --no-cache bind-tools ca-certificates && \
    apk --update --no-cache add python3 bash curl jq file coreutils py3-pip && \
    pip3 install --upgrade pip && \
    pip install awscli boto3 detect-secrets

COPY --from=nuclei-env /usr/local/bin/nuclei /usr/local/bin/nuclei

COPY --from=prowler-env /prowler /prowler

RUN mkdir -p /opt/apps

COPY backend/target/backend-1.0.jar /opt/apps

ENV JAVA_APP_JAR=/opt/apps/backend-1.0.jar

ENV AB_OFF=true

ENV HR_VERSION=${HR_VERSION}

ENV JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true"

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:8088

CMD ["/deployments/run-java.sh"]

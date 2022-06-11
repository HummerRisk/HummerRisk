WORKDIR /opt/hummerrisk

FROM registry.cn-beijing.aliyuncs.com/hummerrisk/nuclei:v0.1 as nuclei-env

COPY --from=nuclei-env /usr/local/bin/nuclei /usr/local/bin/nuclei

FROM registry.cn-beijing.aliyuncs.com/hummerrisk/prowler:v0.1 as prowler-env

COPY --from=prowler-env /prowler /prowler

FROM registry.cn-beijing.aliyuncs.com/hummerrisk/dependency-check:v0.1 as dependency-env

COPY --from=dependency-env /usr/share/dependency-check/bin/dependency-check.sh /usr/share/dependency-check/bin/dependency-check.sh

FROM registry.cn-beijing.aliyuncs.com/hummerrisk/xray:v0.1 as xray-env

COPY --from=xray-env /opt/hummerrisk/xray/ /opt/hummerrisk/xray/

FROM registry.cn-beijing.aliyuncs.com/hummerrisk/custodian:v0.1 as custodian-env

RUN mkdir -p /opt/apps

COPY --from=custodian-env backend/target/backend-1.0.jar /opt/apps

ARG HR_VERSION=dev

ENV JAVA_APP_JAR=/opt/apps/backend-1.0.jar

ENV AB_OFF=true

ENV HR_VERSION=${HR_VERSION}

ENV JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true"

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:8088

CMD ["/deployments/run-java.sh"]

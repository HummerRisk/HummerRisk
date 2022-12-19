FROM registry.cn-beijing.aliyuncs.com/hummerrisk/hummerrisk-base:v0.7.0

COPY backend/target/backend-1.0.jar /opt/apps

ARG HR_VERSION=v0.7.0

ENV JAVA_APP_JAR=/opt/apps/backend-1.0.jar \
    AB_OFF=true \
    HR_VERSION=${HR_VERSION} \
    JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true -Xss5m -XX:+UseParallelGC -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=40 -XX:+ExitOnOutOfMemoryError"

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:8088

CMD ["/deployments/run-java.sh"]

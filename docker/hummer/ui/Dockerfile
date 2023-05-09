FROM node:14-alpine3.15 as stage-build

ARG NPM_REGISTRY="https://registry.npmmirror.com"
ENV NPM_REGISTY=$NPM_REGISTRY

RUN set -ex \
    && npm config set registry ${NPM_REGISTRY}

WORKDIR /data

RUN apk update && \
    apk upgrade

COPY ./package.json /data/package.json
#COPY ./package-lock.json /data/package-lock.json
RUN npm install
COPY . /data
RUN npm uninstall webpack
RUN npm install webpack@^4.4.0 --save-dev
RUN npm run build:prod

FROM nginx:alpine

HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -f 127.0.0.1:80

COPY --from=stage-build /data/dist /opt/hmr-ui/
COPY nginx.conf /etc/nginx/conf.d/default.conf

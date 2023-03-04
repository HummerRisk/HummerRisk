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
RUN npm run build

FROM nginx:alpine

COPY --from=stage-build /data/dist /opt/hmr-ui/
COPY nginx.conf /etc/nginx/conf.d/default.conf

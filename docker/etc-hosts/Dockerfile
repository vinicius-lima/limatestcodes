FROM node:12.16.3-alpine3.10

RUN set -ex && apk --no-cache add sudo
RUN mkdir -p /home/node/api && chown -R node:node /home/node/api
WORKDIR /home/node/api
# USER node
COPY --chown=node:node start.sh .

CMD ["/bin/sh", "./start.sh"]

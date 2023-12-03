FROM mongo:latest


RUN mkdir -p /data/db \
    && chown -R mongodb:mongodb /data/db

COPY conf/mongod.conf /etc/mongod.conf
COPY script/init-mongo.js /docker-entrypoint-initdb.d/


CMD ["mongod", "--config", "/etc/mongod.conf"]
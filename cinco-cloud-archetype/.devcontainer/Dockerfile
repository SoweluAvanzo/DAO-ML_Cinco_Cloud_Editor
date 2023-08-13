# build the base image with Python 3.8
# --------------------------------
FROM docker.io/library/node:16-bookworm-slim as base
RUN apt update
RUN apt install -y build-essential bash zlib1g-dev libncurses5-dev libgdbm-dev libnss3-dev libssl-dev libsqlite3-dev libreadline-dev libffi-dev curl libbz2-dev liblzma-dev libx11-dev libxkbfile-dev libsecret-1-dev git
RUN curl -O https://www.python.org/ftp/python/3.8.2/Python-3.8.2.tar.xz
RUN tar -xf Python-3.8.2.tar.xz
RUN cd Python-3.8.2 \
    && ./configure --enable-optimizations --enable-loadable-sqlite-extensions \
    && make -j 4 \
    && make install
RUN corepack enable


RUN groupadd -r developer && useradd -r -g developer -m developer
USER developer
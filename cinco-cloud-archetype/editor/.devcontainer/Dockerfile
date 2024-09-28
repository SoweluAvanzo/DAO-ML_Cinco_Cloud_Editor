# build the base image with Python 3.9.6
# --------------------------------
FROM docker.io/library/node:18-bookworm-slim as base
RUN apt update
RUN apt install -y build-essential lsof bash zlib1g-dev libncurses-dev libgdbm-dev libnss3-dev libssl-dev libsqlite3-dev libreadline-dev libffi-dev curl libbz2-dev liblzma-dev libx11-dev libxkbfile-dev libsecret-1-dev git
RUN curl -O https://www.python.org/ftp/python/3.9.6/Python-3.9.6.tar.xz
RUN tar -xf Python-3.9.6.tar.xz
RUN cd Python-3.9.6 \
    && ./configure --enable-optimizations --enable-loadable-sqlite-extensions \
    && make -j 4 \
    && make install
RUN corepack enable

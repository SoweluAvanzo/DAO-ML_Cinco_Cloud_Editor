# Base image for DAO-ML Editor => cinco editor:fix-docker-build-run-18d5a43276527ff2b3788bb98f280f740c29ed62
ARG BASE_IMAGE=registry.gitlab.com/scce/cinco-projects/cinco-editor/cinco-editor:fix-docker-build-run-18d5a43276527ff2b3788bb98f280f740c29ed62@sha256:3a7ab800ebe4734401be8d204c341b79c72a73746fb45da69b829ea80b1eeafc
FROM ${BASE_IMAGE}

# Image metadata
LABEL name="DAO-ML-Editor" \
      version="1.0" \
      maintainer="DAO-ML Team"
LABEL description="DAO-ML Editor based on Cinco Cloud"

# Build minio-client (required for the editor to run) - This is a workaround until the base image is fixed to include the built minio-client
RUN cd /editor/minio-client && yarn build && cd /editor

# Build the GLSP client (required for the /client endpoint)
RUN cd /editor && yarn build && cd /editor

# Set environment variable to use the languages folder
ENV META_LANGUAGES_FOLDER="/editor/languages"

# Declare the languages folder as a volume mount point
VOLUME /editor/languages

# Expose necessary ports
EXPOSE 3000 3003 5007

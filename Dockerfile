# Base image for DAO-ML Editor
# Using the Cinco Cloud editor base image
ARG BASE_IMAGE=registry.gitlab.com/scce/cinco-projects/cinco-editor/cinco-editor:latest
FROM ${BASE_IMAGE}

# Image metadata
LABEL name="DAO-ML-Editor" \
      version="1.0" \
      maintainer="DAO-ML Team"
LABEL description="DAO-ML Editor based on Cinco Cloud"

# Build minio-client (required for the editor to run) - This is a workaround until the base image is fixed to include the built minio-client
RUN cd /editor/minio-client && yarn build && cd /editor

# Set environment variable to use the languages folder
ENV META_LANGUAGES_FOLDER="/editor/languages"

# Declare the languages folder as a volume mount point
VOLUME /editor/languages

# Expose necessary ports
EXPOSE 3000 3003 5007
    
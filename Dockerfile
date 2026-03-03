# Base image for DAO-ML Editor
# Using the Cinco Cloud editor base image
ARG BASE_IMAGE=registry.gitlab.com/scce/cinco-projects/cinco-editor/cinco-editor:main-e0989d2dabcfa232695d503577f572e25a64b493
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

# Start the editor with proper client path configuration
CMD ["/bin/bash", "-c", "cd /editor/minio-client && node bundle/cinco-minio-client.js --metaFolder /editor/languages & cd /editor/browser-app && CINCO_CLOUD_HOST=\"${CINCO_CLOUD_HOST}\" CINCO_CLOUD_PORT=\"${CINCO_CLOUD_PORT}\" MINIO_HOST=\"${MINIO_HOST}\" MINIO_PORT=\"${MINIO_PORT}\" MINIO_ACCESS_KEY=\"${MINIO_ACCESS_KEY}\" MINIO_SECRET_KEY=\"${MINIO_SECRET_KEY}\" THEIA_WEBVIEW_EXTERNAL_ENDPOINT=\"${THEIA_WEBVIEW_EXTERNAL_ENDPOINT}\" THEIA_WEBVIEW_ENDPOINT_PATTERN=\"${THEIA_WEBVIEW_ENDPOINT_PATTERN}\" THEIA_MINI_BROWSER_HOST_PATTERN=\"${THEIA_MINI_BROWSER_HOST_PATTERN}\" INTERNAL_USE_SSL=\"${INTERNAL_USE_SSL}\" EXTERNAL_USE_SSL=\"${EXTERNAL_USE_SSL}\" WORKSPACE_PATH=\"${WORKSPACE_PATH}\" TRANSPILATION_MODE=\"${TRANSPILATION_MODE}\" EDITOR_TYPE=\"${EDITOR_TYPE}\" ENVIRONMENT=\"${ENVIRONMENT}\" yarn run theia start --port=3000 --CINCO_GLSP=5007 -WEB_SERVER_PORT=3003 --remote-debugging-port=9222 --no-cluster --loglevel=debug --root-dir=/editor/workspace --plugins=local-dir:./plugins --hostname 0.0.0.0 --META_DEV_MODE --META_LANGUAGES_FOLDER=\"${META_LANGUAGES_FOLDER}\""]
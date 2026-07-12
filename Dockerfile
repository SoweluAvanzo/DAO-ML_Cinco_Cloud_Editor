# Base image for DAO-ML Editor => cinco editor:fix-docker-build-run-18d5a43276527ff2b3788bb98f280f740c29ed62
ARG BASE_IMAGE=registry.gitlab.com/scce/cinco-projects/cinco-editor/cinco-editor:fix-support-full-websocket-url-override-ae964aa61451e2e8337c2fa608d0ab713102e271
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

# Copy model-selection index.html so the root URL serves a landing page instead of "Cannot GET /"
COPY patches/index.html /editor/cinco-glsp-standalone/app/index.html

# Patch the websocket-url.ts to use the full URL from the environment variable
# (this is pre-release code and will be removed once the cinco image is updated to include the fix)
COPY patches/websocket-url.ts /editor/cinco-glsp-standalone/src/utils/websocket-url.ts

# Rebuild the standalone client bundle with the patched app.ts
RUN cd /editor/cinco-glsp-standalone && yarn build && cd /editor

# Set environment variable to use the languages folder
ENV META_LANGUAGES_FOLDER="/editor/languages"

# Declare the languages folder as a volume mount point
VOLUME /editor/languages

# Expose necessary ports
EXPOSE 3000 3003 5007

## Docker
1. Build the docker-image:

    `docker build -t editor .`

2. Run the docker-image:

    `docker run -p 0.0.0.0:3000:3000 --rm editor`

3. Open http://localhost:3000/?jwt=test&projectId=1/editor/workspace in the browser.
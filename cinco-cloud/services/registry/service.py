import flask
import os

app = flask.Flask(__name__)
app.config["DEBUG"] = False

@app.route('/api/registry/gc/run', methods=['POST'])
def home():
    os.system('podman exec --storage-driver=vfs -it registry bin/registry garbage-collect /etc/docker/registry/config.yml')
    return 'ok'

app.run(host='0.0.0.0', port=8000)
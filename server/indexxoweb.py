from pathlib import Path

from flask import Flask, request, jsonify
from flask_cors import CORS

from server.indexxocore import Indexxo

app = Flask(__name__)


class IndexxoServer:

    def __init__(self, indexxo: Indexxo):
        self.app = Flask(__name__)
        CORS(self.app)
        self.indexxo = indexxo
        self.app.add_url_rule("/", "index", self.index)
        self.app.add_url_rule("/folder", "folder", self.get_folder_info)

    def run_server(self):
        self.app.run(host="0.0.0.0", debug=False)

    @staticmethod
    def index():
        return "<p>Welcome to Indexxo!</p>"

    def get_folder_info(self):
        path = request.args.get("path")
        if path is None:
            return jsonify({
                "content": self.indexxo.get_spaces()
            })

        try:
            found = self.indexxo.find(Path(path))
            content, parent = self.indexxo.get_content(found.full_path)

            return jsonify({
                "parent": parent,
                "content": content
            })
        except Exception as e:
            print(e)
            return jsonify({
                "error": f"{path} is not found in index"
            }), 404

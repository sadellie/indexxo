import time

from filetype import filetype

from server.indexxocore import Indexxo
from server.indexxoweb import IndexxoServer
import pathlib
import json
from datetime import timedelta

if __name__ == '__main__':

    indexxo = Indexxo({}, json.load(open("./server/filetypes.json")), [])

    start = time.time()

    home_path = pathlib.Path("D:/Elshan")
    ignore_paths = [
        pathlib.Path(p) for p in [
            "D:/Elshan/files/testdir/animal/cats",
            "D:/Elshan/files/resolve",
            "D:/Elshan/files/posts"
        ]
    ]
    indexxo.add_space(home_path, ignore=ignore_paths)
    #
    # print("Indexer elapsed time:", str(timedelta(seconds=time.time() - start)))
    # print("Files: ", len(indexxo.indexed_files.values()))
    #
    # for p, f in indexxo.indexed_files.items():
    #     print(p, f.full_path)
    #
    # print(indexxo.my_spaces)

    IndexxoServer(indexxo).run_server()

import argparse
import os.path
import sys
import time
import threading
from http.server import ThreadingHTTPServer, SimpleHTTPRequestHandler


class HttpServerMain:

    def __init__(self):
        self.server = ThreadingHTTPServer(('0.0.0.0', 8080), SimpleHTTPRequestHandler)
        self._continue = True

    def run(self):
        while self._continue:
            self.server.handle_request()

    def stop(self):
        self._continue = False
        self.server.server_close()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='start an static http server until a file is deleted')
    parser.add_argument('file', type=str, help='file to check for liveness')
    args = parser.parse_args()
    print("server is checking for liveness file %s" % args.file)

    httpServerMain = HttpServerMain()
    httpserverThread = threading.Thread(target=httpServerMain.run)
    httpserverThread.start()
    while os.path.isfile(args.file):
        time.sleep(10)
    print("file is not existing anymore. stopping process")
    httpServerMain.stop()
    sys.exit(0)

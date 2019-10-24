import shutil
import urllib.request
import urllib.error
from http import HTTPStatus
import ssl
import argparse
import os.path
import time
import threading
from http.server import HTTPServer, BaseHTTPRequestHandler
import socket

NEXUS_DOMAIN = 'artifacts-zl.talend.com'
NEXUS_REPOSITORY = 'https://' + NEXUS_DOMAIN + '/nexus/content/unzip/TalendP2UnzipSnapshot/org/talend/studio/talend-tup-p2-repo/7.3.1-SNAPSHOT/talend-tup-p2-repo-7.3.1-SNAPSHOT.zip-unzip'


def install_opener(nexus_server, login, password, verify_certificate=False):
    https_handler = None
    auth_handler = None
    if NEXUS_REPOSITORY.startswith("https"):
        context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
        if verify_certificate:
            context.verify_mode = ssl.CERT_REQUIRED
        https_handler = urllib.request.HTTPSHandler(context=context)
    if login is not None and password is not None:
        manager = urllib.request.HTTPPasswordMgrWithDefaultRealm()
        manager.add_password(None, nexus_server, login, password)
        auth_handler = urllib.request.HTTPBasicAuthHandler(manager)
    if https_handler is not None:
        if auth_handler is not None:
            opener = urllib.request.build_opener(https_handler, auth_handler)
        else:
            opener = urllib.request.build_opener(https_handler)
        urllib.request.install_opener(opener)


class ProxyHTTPRequestHandler(BaseHTTPRequestHandler):

    def __init__(self, request, client_address, server):
        super().__init__(request, client_address, server)

    def do_HEAD(self):
        """
        :return: the http response of nexus
        """
        return self._do_proxy(method='HEAD')

    def do_GET(self):
        """
        :return: the http response of nexus
        """
        return self._do_proxy(method='GET')

    def _do_proxy(self, method='GET'):
        """
        :return: the http response of nexus
        """
        url = NEXUS_REPOSITORY + self.path
        request = urllib.request.Request(url, method=method, headers=self.headers)
        print("proxying %s" % url)
        try:
            response = urllib.request.urlopen(request)
            self.proxy_response(response)
        except urllib.error.HTTPError as e:
            self.send_error(e.code, e.reason)
        except urllib.error.URLError as e:
            print(e.reason)
            self.send_error(HTTPStatus.BAD_GATEWAY)
        except Exception as e:
            print(e)
            self.send_error(HTTPStatus.BAD_GATEWAY)

    def proxy_response(self, response):
        """
        send back the response received.
        :param response: the response we received from the nexus site
        """
        self.send_response(response.code)
        headers = response.headers
        for header in headers:
            self.send_header(header, headers[header])
        self.end_headers()
        if response.chunked:
            chunk = response.read()
            while chunk:
                self.wfile.write(chunk)
                chunk = response.read()
        else:
            self.wfile.write(response.read())
        self.wfile.flush()


class HttpServerMain:

    def __init__(self, nexus_user=None, nexus_password=None, port=8080, nexus_server=NEXUS_DOMAIN):
        self.server = HTTPServer(('0.0.0.0', port), ProxyHTTPRequestHandler)
        self._continue = True
        self.server.timeout = 30

    def run(self):
        while self._continue:
            self.server.handle_request()

    def stop(self):
        self._continue = False
        self.server.server_close()


TARGET_FILE_PATH = 'tbd-target-platform-configuration/tbd-target-platform-configuration.target'


def update_target_platform(port):
    url = "http://%s:%d" % (socket.gethostname(), port)
    print("update target platform to use update site '%s'" % url)
    target_path = TARGET_FILE_PATH
    template_path = target_path + '.template'
    shutil.move(target_path, template_path)
    with open(template_path, 'r') as template_file, open(target_path, 'w') as target_file:
        for line in template_file.readlines():
            target_file.write(
                line if '<repository location="http://localhost:8080/"' not in line else (
                            '			<repository location="%s/"/>\n' % url))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='start an static http server until a file is deleted')
    parser.add_argument('file', type=str, help='file to check for liveness')
    parser.add_argument('--nexus-url', '-s', type=str, help='nexus server url')
    parser.add_argument('--nexus-user', '-u', type=str, help='nexus user')
    parser.add_argument('--nexus-password', '-p', type=str, help='nexus password')
    parser.add_argument('--listening-port', '-l', type=int, default=8080, help='port of http proxy server')
    args = parser.parse_args()
    update_target_platform(args.listening_port)
    print("server is checking for liveness file %s" % args.file)
    install_opener(NEXUS_DOMAIN, args.nexus_user, args.nexus_password)
    httpServerMain = HttpServerMain(args.nexus_user, args.nexus_password, args.listening_port)
    httpserverThread = threading.Thread(target=httpServerMain.run)
    httpserverThread.start()
    try:
        while os.path.isfile(args.file):
            time.sleep(10)
    except KeyboardInterrupt:
        print("process cancelled")
    print("file is not existing anymore. stopping http server process")
    httpServerMain.stop()
    print("ending process")
    quit()

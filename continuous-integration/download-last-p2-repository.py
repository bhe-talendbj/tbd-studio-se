#!/usr/bin/python
import argparse
import os.path
import ssl
import sys
import urllib.request
import zipfile
from html.parser import HTMLParser
from xml.etree import ElementTree

NAMESPACE = '{http://maven.apache.org/POM/4.0.0}'
VERSION = NAMESPACE + 'parent' + '/' + NAMESPACE + 'version'
CI_SERVER = 'newbuild.talend.com'
UPDATE_SITE_SIZE = 2.6 * 1024 * 1024 * 1024

COLOR_RED = '\033[1;31m'
COLOR_YELLOW = '\033[1;33m'
COLOR_BLUE = '\033[1;34m'
COLOR_CYAN = '\033[1;36m'
END_COLOR = '\033[1;m'


class VersionParser(HTMLParser):

    def __init__(self, version_prefix='V'):
        super().__init__()
        self.version_prefix = version_prefix
        self.versions = []

    def handle_starttag(self, tag, attrs):
        if tag == 'a':
            href = [value.replace('/', '') for attribute, value in attrs if
                    attribute == 'href' and value.startswith(self.version_prefix)]
            if len(href) > 0:
                self.versions.append(href[0])


def read_pom_version():
    """
    quick parse of pom file to find the project version
    :return: declared version
    """
    root = ElementTree.parse('pom.xml').getroot()
    return root.findall(VERSION)[0].text


def retrieve_studio_parameters_from_version(project_version):
    """
    calculate root path and version prefix based on project version
    :param project_version: the version of the project
    :return:
    """
    root_url = 'https://' + CI_SERVER + '/builds/'
    if project_version.endswith('-SNAPSHOT'):
        root_url = root_url + '/'
    else:
        root_url = root_url + '/Store/'
    major_version = project_version.replace('-', '')
    studio_version_prefix = "V%s_" % major_version
    return root_url, studio_version_prefix


def login_to_server(login, password, verify_certificate=False):
    """
    install a manager to handle client authentication.
    :param verify_certificate: if we should check for a valid certificate
    """
    context = ssl.SSLContext(ssl.PROTOCOL_TLSv1)
    if verify_certificate:
        context.verify_mode = ssl.CERT_REQUIRED
    https_handler = urllib.request.HTTPSHandler(context=context)
    manager = urllib.request.HTTPPasswordMgrWithDefaultRealm()
    manager.add_password(None, CI_SERVER, login, password)
    auth_handler = urllib.request.HTTPBasicAuthHandler(manager)
    opener = urllib.request.build_opener(https_handler, auth_handler)
    # Used globally for all urllib.request requests.
    # If it doesn't fit your design, use opener directly.
    urllib.request.install_opener(opener)


def read_versions(root_url, version_prefix):
    content = urllib.request.urlopen(root_url)
    content_as_bytes = content.read()
    content_as_string = content_as_bytes.decode("utf8")
    content.close()
    parser = VersionParser(version_prefix)
    parser.feed(content_as_string)
    return parser.versions


def calculate_versions(version):
    """
    calculate several versions formats needed for creating url

    sample of download url for a studio:
    https://newbuild.talend.com/builds/V7.3.1SNAPSHOT_20190923_1940/all/V7.3.1SNAPSHOT/all_731/Talend-Studio-20190923_1940-V7.3.1SNAPSHOT.zip

    so the method calculate with input 'V7.3.1SNAPSHOT_20190923_1940' (name of the main directory)
    the following versions:
    -  project_version : V7.3.1SNAPSHOT
    -  simple_version  : 731
    -  zip_version     : 20190923_1940-V7.3.1SNAPSHOT
    :param version: the top level version
    :return: several version formatting for the same version
    """
    project_version, timestamp = version.split('_', 1)
    simple_version = project_version[1:6].replace('.', '')
    return timestamp + '-' + project_version, project_version, simple_version


def calculate_download_url(root_url, version, studio):
    zip_version, project_version, simple_version = calculate_versions(version)
    if not studio:
        return "%s%s/all/Talend_Full_Studio_p2_repository-%s.zip" % (root_url, version, zip_version)
    else:
        return "%s%s/all/%s/all_%s/Talend-Studio-%s.zip" % (
            root_url, version, project_version, simple_version, zip_version)


def calculate_local_filename(version, studio):
    if not studio:
        return "update-site-%s.zip" % version
    else:
        return 'Talend-Studio-%s.zip' % version


def copyfileobj(fsrc, fdst, callback, length=1024 * 1024):
    copied = 0
    while True:
        buf = fsrc.read(length)
        if not buf:
            break
        fdst.write(buf)
        copied += len(buf)
        callback(copied, False)
    callback(copied, True)
    return True


last_progress = None


def console_percentage_text_progress(copied, is_last):
    global last_progress
    if is_last:
        current_progress = 100
    else:
        current_progress = copied / UPDATE_SITE_SIZE * 100
    progress = '%3d%%' % current_progress
    if last_progress != progress:
        sys.stdout.write('\r%s' % progress)
        last_progress = progress
    sys.stdout.flush()


def console_percentage_bar_progress(copied, is_last):
    if is_last:
        current_progress = 1
    else:
        current_progress = copied / UPDATE_SITE_SIZE
    _console_bar_progress(current_progress)


def very_simple_progress(copied, is_last):
    sys.stdout.write('.')


# update_progress() : Displays or updates a console progress bar
## Accepts a float between 0 and 1. Any int will be converted to a float.
## A value under 0 represents a 'halt'.
## A value at 1 or bigger represents 100%
def _console_bar_progress(progress):
    barLength = 30  # Modify this to change the length of the progress bar
    status = ""
    if isinstance(progress, int):
        progress = float(progress)
    if not isinstance(progress, float):
        progress = 0
        status = "error: progress var must be float\r\n"
    if progress < 0:
        progress = 0
        status = "Halt...\r\n"
    if progress >= 1:
        progress = 1
        status = "Done...\r\n"
    block = int(round(barLength * progress))
    text = "\rPercent: [{0}] {1}{2}%{3} {4}".format("#" * block + "-" * (barLength - block), COLOR_BLUE,
                                                    round(progress * 100, 2), END_COLOR, status)
    sys.stdout.write(text)
    sys.stdout.flush()


def download(updatesite_url, filename='update-site.zip'):
    print("downloading %s%s%s to local file %s%s%s" % (
        COLOR_BLUE, updatesite_url, END_COLOR, COLOR_CYAN, filename, END_COLOR))
    if not os.path.exists(filename):
        # Download the file from `url` and save it locally under `file_name`:
        with urllib.request.urlopen(updatesite_url) as response, open(filename, 'wb') as destination:
            copyfileobj(response, destination, console_percentage_bar_progress)
    else:
        print('%s%s%s file already exists' % (COLOR_CYAN, filename, END_COLOR))


def found_current_site_version(version, login, password, studio):
    root_url, prefix = retrieve_studio_parameters_from_version(version)
    print("login to Talend studio download site (%s%s/builds%s)" % (COLOR_BLUE, CI_SERVER, END_COLOR))
    login_to_server(login, password)
    versions = read_versions(root_url, prefix)
    if len(versions) == 0:
        print('no version found for %s' % version)
        quit()
    else:
        versions.sort()
        current_version = versions[-1]
        if current_version.endswith('backup'):
            current_version = versions[-2]
        print('found latest version is %s%s%s' % (COLOR_YELLOW, current_version, END_COLOR))
        download_url = calculate_download_url(root_url, current_version, studio)
        return download_url, current_version


def download_if_needed(version, login, password, studio):
    update_site_url, site_version = found_current_site_version(version, login, password, studio)
    # print(update_site_url)
    local_filename = calculate_local_filename(site_version, studio)
    # print(local_filename)
    download(update_site_url, local_filename)
    print('update done for version %s%s%s' % (COLOR_YELLOW, site_version, END_COLOR))
    return local_filename


def decompress(archive, destination_directory):
    print('decompress archive in directory %s' % destination_directory)
    with zipfile.ZipFile(archive, 'r') as zip_ref:
        zip_ref.extractall(destination_directory)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='download last Talend studio or update site (p2 repository) archives')
    parser.add_argument('versions', type=str, nargs='*',
                        help='list of versions to check for last update site archive')
    parser.add_argument('-l', '--login', type=str,
                        help='login to use to connect to build integration server')
    parser.add_argument('-p', '--password', type=str,
                        help='password associated with the login to use to connect to build integration server')
    parser.add_argument('-s', '--studio', action='store_true',
                        help='download the studio package instead of the update site.')
    parser.add_argument('-e', '--extract-directory', dest='directory', type=str,
                        help='where archives must be decompressed')
    args = parser.parse_args()
    versions = args.versions
    if len(versions) == 0:
        project_version = read_pom_version()
        print("current project version is : %s%s%s" % (COLOR_YELLOW, project_version, END_COLOR))
        versions = [project_version]
    for asked_version in versions:
        zip_file = download_if_needed(asked_version, args.login, args.password, args.studio)
        if args.directory is not None:
            decompress(zip_file, args.directory)

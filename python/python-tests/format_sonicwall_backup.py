import sys
from base64 import b64decode

file_path = sys.argv[1]

def read_content(file_path):
    with open(file_path, 'r') as in_file:
        content = in_file.read()
        content = str(b64decode(content))
        content = content.split('&')
        conf_kv = dict()
        for c in content:
            kv = c.split('=')
            if len(kv) == 2:
                conf_kv[kv[0]] = kv[1]
            else:
                conf_kv[kv[0]] = ''
    return conf_kv

with open('formatted_backup_file.txt', 'w') as out_file:
    conf_kv = read_content(file_path)

    for key, value in conf_kv.items():
        out_file.write('{}={}\n'.format(key, value))

import time
import warnings
from sys import argv
from os.path import join as path_join

from paramiko import AutoAddPolicy
from paramiko.client import SSHClient

warnings.filterwarnings(action='ignore', module='.*paramiko.*')

# Licenses to look for
licenses = {
    1: 'SSL VPN Nodes/Users',
    2: 'Virtual Assist Nodes/Users',
    3: 'Gateway Anti-Virus',
    4: 'Anti-Spyware',
    5: 'Intrusion Prevention',
    6: 'Application Firewall'
}


# Wait prompt to be ready after login
def wait_prompt(timeout):
    cont = 0
    while cont < timeout:
        buff = channel.recv(1024)
        cont += 1
        if cont >= timeout:
            raise TimeoutError
        for c in buff:
            if chr(c) == '>':
                cont = timeout
                break
        time.sleep(1)


# Searches licenses
def search_licenses(timeout):
    cont = 0
    remaining_licenses = len(licenses)
    licenses_status = dict()

    while cont < timeout and remaining_licenses > 0:
        buff = channel.recv(1024)
        line = buff.decode('utf-8')

        if credentials[host]['version'] == '5':
            for key, license in licenses.items():
                # Is it the license we are looking for?
                idx = line.find(license)
                if idx != -1:  # License was found
                    idx += len(license) + 1
                    while line[idx] != ':':
                        idx += 1
                    if line[idx + 1] == 'L':  # Is the license valid?
                        licenses_status[key] = 'Licensed'
                    else:
                        licenses_status[key] = 'Not Licensed'
                    remaining_licenses -= 1
        elif credentials[host]['version'] == '6':
            line = line[7:]  # Remove trash from line begining
            line = ''.join(line)
            for key, license in licenses.items():
                # Is it the license we are looking for?
                idx = line.find(license)
                if idx != -1:  # License was found
                    idx += len(license) + 1
                    while line[idx] == ' ':
                        idx += 1
                    if line[idx] == 'L':  # Is the license valid?
                        licenses_status[key] = 'Licensed'
                    else:
                        licenses_status[key] = 'Not Licensed'
                    remaining_licenses -= 1

        cont += 1
        if cont >= timeout:
            raise TimeoutError
        for c in buff:
            if chr(c) == '>':
                cont = timeout
                break
        if credentials[host]['version'] == '6':
            line = ''.join(line)
            if line.endswith('--MORE--'):
                channel.send('\n')
        time.sleep(1)

    return licenses_status


def read_credentials_file(file_path):
    credentials = dict()
    with open(file_path, 'r') as credentials_file:
        for line in credentials_file:
            line = line.split(',')
            credentials[line[0]] = {
                'ip': line[1],
                'port': line[2],
                'pwd': ''.join(line[3]),
                'version': line[4]
            }
    return credentials


# Load credentials in order to create SSH connections with devices
credentials = read_credentials_file(argv[1])

# Create paramiko SSH client
client = SSHClient()
client.load_system_host_keys()
client.set_missing_host_key_policy(AutoAddPolicy)

licenses_status = dict()

# Connecting to devices and retrieving licenses status
for host in credentials.keys():
    print('Host:', host)
    print('Firmware Version:', credentials[host]['version'])
    try:
        client.connect(
            credentials[host]['ip'],
            port=credentials[host]['port'],
            username='infomach',
            password=credentials[host]['pwd']
        )

        channel = client.invoke_shell()
        wait_prompt(30)

        channel.send('show status\n')
        licenses_status[host] = search_licenses(45)

        for key, license in licenses.items():
            print(license, '|', licenses_status.get(host).get(key))
        print('==============================')

        channel.send('exit\n')
        channel.close()

        client.close()
    except TimeoutError:
        licenses_status[host] = dict()
        for key in range(1, len(licenses) + 1):
            licenses_status[host][key] = 'Unreachable'

        for license in licenses.values():
            print(license, '| Unreachable')
        print('==============================')

# Writing output file
write_directory = argv[2]
with open(path_join(write_directory, 'sonicwall_licenses_status.txt'), 'w') as output_file:
    for host in licenses_status.keys():
        is_licensed = True
        status = ''
        for status in licenses_status[host].values():
            if status != 'Licensed':
                is_licensed = False
                break
        output_file.write('{},{}\n'.format(host, status))

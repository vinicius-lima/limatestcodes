import time
import warnings
from sys import argv
from os.path import join as path_join

from paramiko import AutoAddPolicy
from paramiko.client import SSHClient

warnings.filterwarnings(action='ignore', module='.*paramiko.*')

licenses = {
    # Licenses to look for on firmware version 5
    '5': {
        1: 'SSL VPN Nodes/Users',
        2: 'Virtual Assist Nodes/Users',
        3: 'Gateway Anti-Virus',
        4: 'Anti-Spyware',
        5: 'Intrusion Prevention',
        6: 'Application Firewall',
        7: 'Visualization Control'
    },
    # Licenses to look for on firmware version 6
    '6': {
        1: 'Gateway AV/Anti-Spyware/Intrusion Prevention/App Control/App Visualization',
        2: 'Premium Content Filter'
    }
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


# Searches licenses onto firmware version 5
def search_licenses_v5(timeout):
    cont = 0
    remaining_licenses = len(licenses['5'])
    licenses_status = dict()

    while cont < timeout and remaining_licenses > 0:
        buff = channel.recv(1024)
        line = buff.decode('utf-8')

        for key, license in licenses.get(credentials[host]['version'], {}).items():
            idx = line.find(license)  # Is it the license we are looking for?
            if idx != -1:  # License was found
                idx += len(license) + 1
                while line[idx] != ':':
                    idx += 1
                if line[idx + 1] == 'L':  # Is the license valid?
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
        time.sleep(1)

    return licenses_status


# Searches licenses onto firmware version 6
def search_licenses_v6(timeout):
    cont = 0
    remaining_licenses = len(licenses['6'])
    licenses_status = dict()

    while cont < timeout and remaining_licenses > 0:
        buff = channel.recv(1024)
        line = buff.decode('utf-8')
        line = line[7:]  # Remove trash from line begining
        line = ''.join(line)

        for key, license in licenses['6'].items():
            line = ''.join(line)
            if line.startswith(license):  # Is it the license we are looking for?
                line = line[len(license):]
                line = line.split(' ')
                for idx in range(len(line)):
                    if line[idx] == 'Licensed' and idx > 0:  # Is the license valid?
                        if line[idx - 1] == 'Not':
                            licenses_status[key] = 'Not Licensed'
                        else:
                            licenses_status[key] = 'Licensed'
                        break
                remaining_licenses -= 1

        cont += 1
        if cont >= timeout:
            raise TimeoutError
        for c in buff:
            if chr(c) == '>':
                cont = timeout
                break
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

        if credentials[host]['version'] == '5':
            channel.send('show status\n')
            licenses_status[host] = search_licenses_v5(30)
        elif credentials[host]['version'] == '6':
            channel.send('show license status\n')
            licenses_status[host] = search_licenses_v6(30)

        for key, license in licenses.get(credentials[host]['version'], {}).items():
            print(license, '|', licenses_status.get(host).get(key))
        print('==============================')

        channel.send('exit\n')
        channel.close()

        client.close()
    except TimeoutError:
        licenses_status[host] = dict()
        for key in range(1, len(licenses[credentials[host]['version']]) + 1):
            licenses_status[host][key] = 'Unreachable'

        for license in licenses[credentials[host]['version']].values():
            print(license, '| Unreachable')
        print('==============================')

# Writting output file
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

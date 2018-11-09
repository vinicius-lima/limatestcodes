import re
import sys

from glob import glob


if len(sys.argv) != 4:
    print('0.000')
    sys.exit(1)

backup_path = sys.argv[1]
host_name = sys.argv[2]

interface = sys.argv[3].split(' ')
interface = interface[0] + ' ' + interface[1]

try:
    file_path = glob('{}/{}_*.cli'.format(backup_path, host_name))[0]
except:
    print('[-] Could not find backup file.')
    sys.exit(1)

is_interface_property = False

with open(file_path, 'r', encoding='utf-8') as in_file:
    lines = in_file.readlines()

    for line in lines:
        line = ''.join(line[:-1])
        if re.match('^{}$'.format(interface), line):
            is_interface_property = True
            continue
        if is_interface_property:
            if re.match('^[\s]{4}exit', line)\
                or re.search('no egress-bandwidth-management', line)\
                or re.search('no bandwidth-management', line):

                print('0.000')
                sys.exit(0)
            elif re.search('.*bandwidth-management ([\d\.]*)', line):
                bandwidth = re.search('.*bandwidth-management ([\d\.]*)', line).groups()
                print(bandwidth[0])
                sys.exit(0)
    print('0.000')
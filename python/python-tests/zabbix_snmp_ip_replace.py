import requests
import json
import sys
from subprocess import DEVNULL, STDOUT, Popen
from pysnmp.hlapi import *

if len(sys.argv) < 2:
    sys.exit(1)


def interface_type(t):
    """
    Returns the interface protocol name according to the type number.

    :param t: Protocol number.
    :return: Protocol name.
    :rtype: string
    """
    switcher = {
        "1": "agent",
        "2": "SNMP",
        "3": "IPMI",
        "4": "JMX"
    }
    return switcher.get(t, "unknown")


def get_host_id(host_name, zabbix_url, user_auth):
    """
    Gets the host ID based on the host name.

    :param host_name: Host name.
    :param zabbix_url: Zabbix server URL.
    :param user_auth: User authorization access token.
    :return: Host ID
    :rtype: string
    """
    payload = {
        "jsonrpc": "2.0",
        "method": "host.get",
        "params": {
            "filter": {
                "host": host_name
            }
        },
        "auth": user_auth,
        "id": 1
    }

    r = requests.post(
        url=zabbix_url,
        data=json.dumps(payload),
        headers={"Content-Type": "application/json"}
    )

    return r.json()["result"][0]["hostid"]


def get_host_interfaces(host_id, zabbix_url, user_auth):
    """
    Lists the host interfaces.

    :param host_id: Host ID.
    :param zabbix_url: Zabbix server URL
    :param user_auth: User authorization token.
    :return: Array of host interfaces.
    :rtype: list
    """
    payload = {
        "jsonrpc": "2.0",
        "method": "hostinterface.get",
        "params": {
            "filter": {
                "hostid": host_id
            }
        },
        "auth": user_auth,
        "id": 2
    }

    r = requests.post(
        url=zabbix_url,
        data=json.dumps(payload),
        headers={"Content-Type": "application/json"}
    )

    return r.json()["result"]


def set_snmp_interface(params, zabbix_url, user_auth):
    """
    Set a host SNMP interface as default (main = 1).
    Other interfaces of the same type must be set to 0. (main = 0)

    The parameter `params` must be at the following format:
        [
            {
                'interfaceid': '1',
                'main': 1
            },
            {
                'interfaceid': '2',
                'main': 0
            }
            ...
        ]

    :param params: Array of host interfaces.
    :param zabbix_url: Zabbix server URL.
    :param user_auth: User authorization token.
    :return: Zabbix server response status.
    :rtype: int
    """
    payload = {
        "jsonrpc": "2.0",
        "method": "hostinterface.update",
        "params": params,
        "auth": user_auth,
        "id": 3
    }

    r = requests.post(
        url=zabbix_url,
        data=json.dumps(payload),
        headers={"Content-Type": "application/json"}
    )

    return r.status_code


zabbix_url = 'http://192.168.121.134/zabbix/api_jsonrpc.php'
zabbix_user_auth = "access_token"

security_user_name = "appliance_user"
snmp_authkey_sha = "appliance_authentication_key"
snmp_privkey_des = "appliance_private_key"

host_name = str(sys.argv[1])
search_oid = '1.3.6.1.2.1.1.3.0'  # sysUpTime.0
print("host_name =", host_name)

host_id = get_host_id(host_name, zabbix_url, zabbix_user_auth)
print("host_id =", host_id)

interfaces = get_host_interfaces(host_id, zabbix_url, zabbix_user_auth)

for idx in range(len(interfaces)):
    print("-------------------------")
    print("interfaceid: ", interfaces[idx]["interfaceid"])
    print("main: ", interfaces[idx]["main"])
    print("type:", interfaces[idx]["type"], "-", interface_type(interfaces[idx]["type"]))

    dst = 'null'
    if interfaces[idx]["useip"] == "1":
        print("IP:", interfaces[idx]["ip"])
        dst = interfaces[idx]["ip"]
    elif interfaces[idx]["useip"] == "0":
        print("DNS:", interfaces[idx]["dns"])
        dst = interfaces[idx]["dns"]

    print("port:", interfaces[idx]["port"])

    if interface_type(interfaces[idx]["type"]) == "SNMP":
        errorIndication, errorStatus, errorIndex, varBinds = next(
            getCmd(
                SnmpEngine(),
                CommunityData('public'),
                UdpTransportTarget((dst, interfaces[idx]["port"])),
                ContextData(),
                ObjectType(ObjectIdentity(search_oid))
            )
        )

        if errorIndication:
            snmp_up = False
            print(errorIndication)
        elif errorStatus:
            snmp_up = False
            print('%s at %s' % (errorStatus.prettyPrint(),
                    errorIndex and varBinds[int(errorIndex) - 1][0] or '?'))
        else:
            snmp_up = True
        print("SNMP V2 UP =", snmp_up)

        if not snmp_up:
            errorIndication, errorStatus, errorIndex, varBinds = next(
                getCmd(
                    SnmpEngine(),
                    UsmUserData(
                        security_user_name,
                        snmp_authkey_sha,
                        snmp_privkey_des,
                        authProtocol=usmHMACSHAAuthProtocol,
                        privProtocol=usmDESPrivProtocol),
                    UdpTransportTarget((dst, interfaces[idx]["port"])),
                    ContextData(),
                    ObjectType(ObjectIdentity(search_oid))
                )
            )

            if errorIndication:
                snmp_up = False
                print(errorIndication)
            elif errorStatus:
                snmp_up = False
                print('%s at %s' % (errorStatus.prettyPrint(),
                        errorIndex and varBinds[int(errorIndex) - 1][0] or '?'))
            else:
                snmp_up = True
            print("SNMP V3 UP =", snmp_up)

        if snmp_up and interfaces[idx]["main"] == "1":
            break  # Breaks -> for idx in range(len(interfaces))
        elif snmp_up and interfaces[idx]["main"] == "0":
            params = []
            interface = dict()
            for itf in interfaces:
                if itf['main'] == "1" and interface_type(itf["type"]) == "SNMP":
                    interface.clear()
                    interface['interfaceid'] = itf['interfaceid']
                    if itf["useip"] == "1":
                        interface['ip'] = itf['ip']
                    elif itf["useip"] == "0":
                        interface['dns'] = itf['dns']
                    interface['port'] = itf['port']
                    params.append(interface.copy())
                elif itf["interfaceid"] == interfaces[idx]["interfaceid"]\
                        and interface_type(interfaces[idx]["type"]) == "SNMP":
                    interface.clear()
                    interface['interfaceid'] = itf['interfaceid']
                    if itf["useip"] == "1":
                        interface['ip'] = itf['ip']
                    elif itf["useip"] == "0":
                        interface['dns'] = itf['dns']
                    interface['port'] = itf['port']
                    params.append(interface.copy())
            params[0]['interfaceid'], params[1]['interfaceid'] = params[1]['interfaceid'], params[0]['interfaceid']
            set_snmp_interface(params, zabbix_url, zabbix_user_auth)
            break  # Breaks -> for idx in range(len(interfaces))

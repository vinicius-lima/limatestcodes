import requests
import sys
from re import search
from json import dumps
from json.decoder import JSONDecodeError

if len(sys.argv) != 2:
    print('Usage:', sys.argv[0], '<original problem id>')
    sys.exit(1)

original_problem_id = sys.argv[1]

base_url = 'https://api.desk.ms'
operator_key = 'your operator key'
PublicKey = 'your public key'
access_token = ''


def get_access_token():
    endpoint = '/Login/autenticar'
    payload = {
        'PublicKey': PublicKey
    }

    response = requests.post(
        base_url + endpoint,
        data=dumps(payload),
        headers={"Authorization": operator_key}
    )

    access_token = response.json()
    return access_token


access_token = get_access_token()

endpoint = '/ChamadosSuporte/lista'
payload = {
    'Pesquisa': 'Original problem ID: ' + original_problem_id,
    'Tatutal': '',
    'Ordem': [
        {
            'Coluna': 'Chave',
            'Direcao': 'true'
        }
    ]
}

try:
    response = requests.post(
        base_url + endpoint,
        data=dumps(payload),
        headers={"Authorization": access_token}
    )
    response = response.json()
except JSONDecodeError:
    print('[-] Could not parse JSON')
    sys.exit(1)

if int(response.get('total', 0)) == 0:
    print('[-] No tickets were retrieved for the Original problem ID:',
          original_problem_id)
    print('[-] No ticket has been updated to solved.')
    sys.exit(1)
elif int(response.get('total', 0)) > 1:
    print('[-] More than one ticket was retrieved for the Original problem ID:',
          original_problem_id)
    print('[-] No ticket has been updated to solved.')
    sys.exit(1)

response = response['root'][0]

ticket_code = str(response['CodChamado'])
ticket_status = response.get('NomeStatus', '')
ticket_description = response.get('Descricao', '')

if search('reiniciado', ticket_description):
    print('[!] Ticket', ticket_code, 'is about an appliance reboot.')
    print('[-] No ticket has been updated to solved.')
    sys.exit(0)

if ticket_status.upper() == 'RESOLVIDO':
    print('[!] Ticket', ticket_code, 'has already been solved.')
    sys.exit(0)

endpoint = '/ChamadosSuporte/interagir'
payload = {
    'Chave': ticket_code,
    'TChamado': {
        'CodFormaAtendimento': '000004',
        'CodStatus': '000002',
        'Descricao': 'Ticket fechado automaticamente pelo sistema de monitoramento.',
        'CodCausa': '000020',
        'EnviarEmail': 'N'
    }
}

response = requests.put(
    base_url + endpoint,
    data=dumps(payload),
    headers={"Authorization": access_token}
)

if response.status_code == 200:
    print('[!] Ticket', ticket_code, 'has been updated to solved.')
else:
    print('[-] Could not update ticket', ticket_code,
          '| response status:', response.status_code)
    try:
        print(dumps(response.json(), indent=2))
    except JSONDecodeError:
        print('[-] Could not parse JSON')
    sys.exit(1)

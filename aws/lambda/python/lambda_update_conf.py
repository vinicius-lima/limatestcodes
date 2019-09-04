import json
import boto3

print('[+] Creating Lambda client...')
lambda_client = boto3.client('lambda', region_name='us-east-1')

print('[+] Sending new Lambda configuration...')
response = lambda_client.update_function_configuration(
    FunctionName='arn:aws:lambda:us-east-1:247172607968:function:ec2-management-EventTargetFunction-RKDE3R2D859F',
    Environment={
        'Variables': {
            'Holidays': '["01/01/2019-Ano Novo", "04/03/2019-Carnaval", "05/03/2019-Carnaval", "06/03/2019-Carnaval", "19/04/2019-Sexta-Feira Santa", "21/04/2019-Dia de Tiradentes", "01/05/2019-Dia do Trabalho", "07/09/2019-Independ\u00eancia do Brasil", "12/10/2019-Nossa Senhora Aparecida", "02/11/2019-Dia de Finados", "15/11/2019-Proclama\u00e7\u00e3o da Rep\u00fablica", "25/12/2019-Natal"]'
        }
    }
)

print('[!] Response:')
print(json.dumps(response, indent=2))

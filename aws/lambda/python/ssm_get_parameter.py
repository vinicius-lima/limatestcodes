import json
import boto3

print('[+] Creating Systems Manager client...')
ssm = boto3.client('ssm', region_name='us-east-1')

print('[+] Retrieving parameter value...')
response = ssm.get_parameter(
    Name='HolidayApiToken'
)

print('[!] Response:')
print(json.dumps(response, indent=2, default=lambda x: x.__str__()))

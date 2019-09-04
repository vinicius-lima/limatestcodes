import json
import boto3

print('[+] Creating CloudWatch logs client...')
cw_logs = boto3.client('logs', region_name='us-east-1')

print('[+] Getting log groups...')
response = cw_logs.describe_log_groups(
    logGroupNamePrefix='/aws/lambda/ec2-management',
    limit=50
)

# print(json.dumps(response, indent=2))

retention_in_days = 14
print('[+] Setting retention policy to {}'.format(retention_in_days))
for log in response['logGroups']:
    print(f"{log['logGroupName']} | retentionInDays: {log.get('retentionInDays', 'Never Expire')}")
    cw_logs.put_retention_policy(
        logGroupName=log['logGroupName'],
        retentionInDays=retention_in_days
    )

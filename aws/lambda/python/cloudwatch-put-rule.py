import json
import boto3

print('[+] Creating CloudWatch events client...')
cw_events = boto3.client('events', region_name='us-east-1')

print('[+] Creating Lambda client...')
lambda_client = boto3.client('lambda', region_name='us-east-1')

print('[+] Creating event rule...')
response = cw_events.put_rule(
    Name='cloudwatch-event-test',
    ScheduleExpression='cron(* * 20 * ? *)',
    State='ENABLED',
    Description='Created by a Python SDK call'
)
print(json.dumps(response, indent=2))

print('[+] Adding invoke permision on target Lambda function...')
rule_arn = response.get('RuleArn')
response = lambda_client.add_permission(
    FunctionName='cloudwatch-event-target',
    StatementId='ID-1',
    Action='lambda:InvokeFunction',
    Principal='events.amazonaws.com',
    SourceArn=rule_arn
)
print(json.dumps(response, indent=2))

print('[+] Binding event targets to rule...')
response = cw_events.put_targets(
    Rule='cloudwatch-event-test',
    Targets=[
        {
            'Id': 'cloudwatch-event-target',
            'Arn': 'arn:aws:lambda:us-east-1:aws_account_id:function:cloudwatch-event-target',
            'Input': '{"action":"start","email":"colaborador@infomach.com.br","envName":"Prod"}'
        }
    ]
)
print(json.dumps(response, indent=2))

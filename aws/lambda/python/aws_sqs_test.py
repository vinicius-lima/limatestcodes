import json
import boto3

sqs = boto3.client('sqs', region_name='sa-east-1')

response = sqs.receive_message(QueueUrl='https://sqs.sa-east-1.amazonaws.com/aws_account_id/ec2_state_change')

messages = response.get('Messages', [])
print('Amount of messages:', len(messages))
print('==============')
for message in messages:
    # print('MessageId:', message['MessageId'])
    # print('Body:', message['Body'])
    print(json.dumps(message, indent=2))
    sqs.delete_message(
        QueueUrl='https://sqs.sa-east-1.amazonaws.com/aws_account_id/ec2_state_change',
        ReceiptHandle=message['ReceiptHandle']
    )

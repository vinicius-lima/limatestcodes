import json
import boto3
from os import environ

sns = boto3.resource('sns', region_name=environ['AWS_REGION'])
topic = sns.Topic(environ['TopicArn'])

def lambda_handler(event, context):
    
    # Transforma a string em um objeto JSON
    message = json.loads(event['Records'][0]['Sns']['Message'])
    
    # Faz o parser apenas da propriedade 'Trigger'
    return_message = '\n'
    for key, value in message['Trigger'].items():
        return_message += '\t{}: {}\n'.format(key, value)
    message['Trigger'] = return_message
    
    # Faz o parser de toda a mensagem
    return_message = 'Que merrrrda hein\n'
    for key, value in message.items():
        return_message += '{}: {}\n'.format(key, value)
    
    response = topic.publish(
        Message=return_message,
        Subject='Teste'
    )
    
    return {
        'statusCode': 200,
        'body': json.dumps({
                'message': 'Message sent to SNS topic',
                'MessageId': response['MessageId'],
            })
    }

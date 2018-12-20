import json
import boto3

dynamodb = boto3.client(
    'dynamodb', region_name='sa-east-1', api_version='2012-08-10')


def lambda_handler(event, context):
    params = {
        'Item': {
            'Userid': {
                'S': event['userId']
            },
            'Age': {
                'N': event['age']
            },
            'Height': {
                'N': event['height']
            },
            'Income': {
                'N': event['income']
            }
        },
        'TableName': 'compare-yourself'
    }
    response = dynamodb.put_item(**params)
    print(response)
    return response

import json
import boto3

dynamodb = boto3.client(
    'dynamodb', region_name='sa-east-1', api_version='2012-08-10')


def lambda_handler(event, context):
    response = dynamodb.delete_item(
        Key={
            'Userid': {
                'S': event['userId']
            }
        },
        TableName='compare-yourself'
    )
    print(response)
    return response

import json
import boto3
from os import environ

dynamodb = boto3.client('dynamodb', region_name=environ['Region'], api_version='2012-08-10')

def lambda_handler(event, context):
    response = dynamodb.put_item(
        Item={
            'Category': {
                'S': 'devices'
            },
            'Id': {
                'N': event['id']
            },
            'Type': {
                'N': event['type']
            },
            'Width': {
                'N': event['width']
            },
            'Height': {
                'N': event['height']
            },
            'Battery': {
                'BOOL': event['battery']
            },
            'Keyboard': {
                'BOOL': event['keyboard']
            },
            'Location': {
                'N': event['location']
            }
        },
        TableName=environ['TableName']
    )
    print(response)
    return response

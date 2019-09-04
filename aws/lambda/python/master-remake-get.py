import json
import boto3
from os import environ

region_name = 'sa-east-1'
table_name = 'MasterRemake'

dynamodb = boto3.client('dynamodb', region_name=region_name, api_version='2012-08-10')

def lambda_handler(event, context):
    category = event['category']
    print('Category: ', category)
    
    response = dynamodb.scan(
        TableName=table_name,
        ExpressionAttributeValues={
            ':c': {
                'S': category
            }
        },
        FilterExpression='Category = :c'
    )
    if category == 'devices':
        items = [{
            'id': int(dataField['Id']['N']),
            'type': int(dataField['Type']['N']),
            'width': int(dataField['Width']['N']),
            'height': int(dataField['Height']['N']),
            'battery': bool(dataField['Battery']['BOOL']),
            'keyboard': bool(dataField['Keyboard']['BOOL']),
            'location': int(dataField['Location']['N'])
        } for dataField in response['Items']]
    else:
        items = [{
            'id': int(dataField['Id']['N']),
            'value': dataField['Value']['S']
        } for dataField in response['Items']]
        
    return items

response = lambda_handler({'category': 'devices'}, {})
print(response)

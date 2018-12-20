import json
import boto3

dynamodb = boto3.client(
    'dynamodb', region_name='sa-east-1', api_version='2012-08-10')
cisp = boto3.client('cognito-idp', region_name='us-east-2',
                    api_version='2016-04-18')


def lambda_handler(event, context):
    access_token = event['accessToken']

    type = event['type']
    if type == 'all':
        response = dynamodb.scan(TableName='compare-yourself')
        print(response)
        items = [{
            'age': int(dataField['Age']['N']),
            'height': int(dataField['Height']['N']),
            'income': int(dataField['Income']['N'])
        } for dataField in response['Items']]
        # TODO: handle exceptions
        return items
    elif type == 'single':
        result = cisp.get_user(AccessToken=access_token)
        print(result)
        userId = result['UserAttributes'][0]['Value']
        response = dynamodb.get_item(
            Key={
                'Userid': {
                    'S': userId
                }
            },
            TableName='compare-yourself'
        )
        print(response)
        if 'Item' in response:
            return [{
                'age': int(response['Item']['Age']['N']),
                'height': int(response['Item']['Height']['N']),
                'income': int(response['Item']['Income']['N'])
            }]
    return {'message': 'Something went wrong'}

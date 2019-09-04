import json
import boto3
from os import environ

apigateway = boto3.client('apigateway', region_name=environ['AWS_REGION'])

def lambda_handler(event, context):
    api_id = event.get('restApiId', '')
    name = event.get('authorizerName', 'cognito-authorizer')
    provider_arns = event.get('providerARNs', [])
    response = dict()

    try:
        response = apigateway.create_authorizer(
            restApiId=api_id,
            name=name,
            type='COGNITO_USER_POOLS',
            providerARNs=provider_arns,
            identitySource='method.request.header.Authorization'
        )
    except Exception as e:
        print(e)
        return {
            'statusCode': 400,
            'body': json.dumps({
                'errorMessage': 'Could not create authorizer',
                'requestBody': event
            })
        }

    authorizer_id = response['id']

    try:
        response = apigateway.get_resources(
            restApiId=api_id
        )
    except Exception as e:
        print(e)
        return {
            'statusCode': 400,
            'body': json.dumps({
                'errorMessage': 'Could not get a list of available resources',
                'requestBody': event
            })
        }

    try:
        for resource in response['items']:
            try:
                apigateway.update_method(
                    restApiId=api_id,
                    resourceId=resource['id'],
                    httpMethod='POST',
                    patchOperations=[
                        {
                            'op': 'replace',
                            'path': '/authorizationType',
                            'value': 'COGNITO_USER_POOLS'
                        },
                        {
                            'op': 'replace',
                            'path': '/authorizerId',
                            'value': authorizer_id
                        }
                    ]
                )
            except Exception:
                pass
    except Exception as e:
        print(e)
        return {
            'statusCode': 400,
            'body': json.dumps({
                'errorMessage': 'Could not associate authorizer',
                'requestBody': event
            })
        }
    
    return {
        'statusCode': 200,
        'body': json.dumps({
            'message': 'Authorizer has been created and POST endpoints have been configured'
        })
    }

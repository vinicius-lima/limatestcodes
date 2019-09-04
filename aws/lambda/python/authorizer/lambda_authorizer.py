import requests
import json
import boto3
import time
from os import environ
from jose import jwk, jwt
from jose.utils import base64url_decode
environ['AWS_REGION'] = 'us-east-1'
environ['TableName'] = 'EC2ManagementUsers'
environ['UserPoolId'] = 'us-east-1_qbgfwhVZs'
region = environ['AWS_REGION']
user_pool_id = environ['UserPoolId']

dynamodb = boto3.resource('dynamodb', region_name=environ['AWS_REGION'])
table = dynamodb.Table(environ['TableName'])

keys_url = f'https://cognito-idp.{region}.amazonaws.com/{user_pool_id}/.well-known/jwks.json'
# Downloading the public keys
response = requests.get(keys_url)
response = response.json()
keys = response['keys']


def decode_id_token(token):
    # Get the kid from the headers prior to verification
    headers = jwt.get_unverified_headers(token)
    kid = headers['kid']

    # Search for the kid in the downloaded public keys
    key_index = -1
    for idx in range(len(keys)):
        if kid == keys[idx]['kid']:
            key_index = idx
            break
    if key_index == -1:
        print('Public key not found in jwks.json')
        return {}

    # Construct the public key
    public_key = jwk.construct(keys[key_index])

    # Get the last two sections of the token,
    # message and signature (encoded in base64)
    message, encoded_signature = str(token).rsplit('.', 1)

    # Decode the signature
    decoded_signature = base64url_decode(encoded_signature.encode('utf-8'))

    # Verify the signature
    if not public_key.verify(message.encode("utf8"), decoded_signature):
        print('Signature verification failed')
        return {}

    # Use the unverified claims
    claims = jwt.get_unverified_claims(token)

    # Verifing the token expiration
    if time.time() > claims['exp']:
        print('Token is expired')
        return {}

    return claims


def generate_policy(principal_id, effect='Deny', resource=''):
    auth_response = dict()
    auth_response['principalId'] = principal_id

    policy_document = dict()
    policy_document['Version'] = '2012-10-17'
    policy_document['Statement'] = [
        {
            'Action': 'execute-api:Invoke',
            'Effect': effect,
            'Resource': resource
        }
    ]

    auth_response['policyDocument'] = policy_document
    return auth_response


def lambda_handler(event, context):
    principal_id = 'api-gateway'
    
    try:
        claims = decode_id_token(event['authorizationToken'])
        principal_id = claims['email']

        response = table.get_item(
            Key={
                'Email': principal_id
            },
            ProjectionExpression='Groups'
        )
        
        if 'Admin' not in response['Item']['Groups']:
            raise Exception()
    except Exception:
        return generate_policy(principal_id, 'Deny', event['methodArn'])

    return generate_policy(principal_id, 'Allow', event['methodArn'])
        
# the following is useful to make this script executable in both
# AWS Lambda and any other local environments
if __name__ == '__main__':
    # for testing locally you can enter the JWT ID Token here
    event = {
        'authorizationToken': 'eyJraWQiOiJFenpEN2tpWGcrbTlZSG5taHV6a0FoU2VGejRGa2hpVjRCZkl4aE4wRjd3PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJmMzQ0YTY3OS05ZWIxLTQ3MmMtOGFmYS01NDI1OGI1MmQ0YzIiLCJhdWQiOiIxdWcyaDBpYnN2dGswcWxqbzM5NzNpbWg3OSIsImV2ZW50X2lkIjoiYzExZDA2YWEtODk0ZS0xMWU5LTllNWEtZGI0OGJkZjEzMjQ4IiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE1NTk5MzA2OTUsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xX3FiZ2Z3aFZacyIsImNvZ25pdG86dXNlcm5hbWUiOiJmMzQ0YTY3OS05ZWIxLTQ3MmMtOGFmYS01NDI1OGI1MmQ0YzIiLCJleHAiOjE1NTk5MzQyOTUsImlhdCI6MTU1OTkzMDY5NSwiZW1haWwiOiJ2aW5pY2l1c2I4MEBnbWFpbC5jb20ifQ.HAFnetZwOr-vaJ53ikOcGC4QxFRMS2WKBbg5VyuzqWrWajzfItAdaLIG3owMxuFl1yDkdhXR0Dp4tDhQBTVZNQDmucKXScOyTPY5DHfJQN6wssF-gKXZhLWD20PjF2ixdrGOk1HIhK56J517YL-FqEnxfB3fiDuEvgu5cr2Q-2FDiL8Q7JL0DPaWY-RDJQNY_BhITOGdzyeT0Yhji5yHWp4eCIW5b3L4EEJs6RkF0q-09JKOSFaxHiNFiqibGHeEl5E1yrsjRMvfl0_Jy5R3wXkbwdwSTRgAi8i_uMLRg1DTJipXtiSbJVXym1rDKUdjJUooC5RZoe76CNXicojwqg',
        'methodArn': 'arn:aws:some-arn/some-resource'
    }
    iam_policy = lambda_handler(event, None)
    print(json.dumps(iam_policy, indent=2))
    
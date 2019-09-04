import json
import boto3
import yaml
from sys import argv


# Get URIs from YAML file
with open(argv[1], 'r') as stream:
    packaged = yaml.load(stream)

uris = list()
uris_keys = list()
for resource in packaged['Resources'].values():
    if resource['Properties'].get('CodeUri') is not None:
        uris.append(resource['Properties'].get('CodeUri'))

print('Packaged URIs:')
for idx in range(len(uris)):
    print(f"{idx + 1} - {uris[idx]} | ObjKey: {uris[idx].split('/')[-1]}")
    uris_keys.append(uris[idx].split('/')[-1])
print('=============================')

# List objects from artifacts bucket
s3 = boto3.resource('s3', region_name='us-east-1')
bucket = s3.Bucket('vbslima-pipeline-artifacts')
artifacts = list()

for obj in bucket.objects.all():
    if len(obj.key.split('/')) == 1:
        artifacts.append(obj.key)

print('Bucket artifacts:')
for idx in range(len(artifacts)):
    print(f"{idx + 1} - {artifacts[idx]}")
print('=============================')

# List objects that are not being used for deployment
unmatching = [obj_key for obj_key in artifacts if obj_key not in uris_keys]
print('Unmatching:')
for idx in range(len(unmatching)):
    print(f"{idx + 1} - {unmatching[idx]}")
print('=============================')

if len(unmatching) > 0:
    # Delete unused objects
    response = bucket.delete_objects(
        Delete={
            'Objects': [{'Key': key} for key in unmatching]
        }
    )

    print(f"RequestCharged: {resource.get('RequestCharged', 'No charges')}")
    print("Deleted:")
    for obj in response.get('Deleted', []):
        print(f"Key: {obj['Key']} | DeleteMarkerVersionId: {obj['DeleteMarkerVersionId']}")

    print("Errors:")
    for obj in response.get('Errors', []):
        print(f"Key: {obj['Key']} | Message: {obj['Message']}")
else:
    print('Nothing to delete!')

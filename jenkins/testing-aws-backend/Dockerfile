FROM python:3.6.8-slim

ENV AWS_ACCESS_KEY_ID access_key
ENV AWS_SECRET_ACCESS_KEY secret_key
ENV AWS_DEFAULT_REGION us-east-1
ENV AWS_DEFAULT_OUTPUT json

WORKDIR /usr/src/app

RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    python3-dev \
    && rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir awscli aws-sam-cli

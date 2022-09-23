#!/bin/bash
set -x

awslocal sqs create-queue --queue-name some-test-queue
awslocal sqs create-queue --queue-name another-test-queue
awslocal sqs create-queue --queue-name monday-test-queue
awslocal sqs create-queue --queue-name friday-test-queue

set +x
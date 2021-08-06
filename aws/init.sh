#!/bin/bash
set -x

awslocal sqs create-queue --queue-name some-test-queue

set +x
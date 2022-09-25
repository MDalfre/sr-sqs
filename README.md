![alt text](https://raw.githubusercontent.com/MDalfre/sr-sqs/main/.github/images/logo.png)

# Description

Simple generic Amazon SQS client and mock, designed to make asynchronous applications that uses SQS queues simple to test.

- List all queues;
- Produce (Send) messages;
- Consume (Receive) messages;
- Show message details;
- Create Queue;
- Create configuration file for AWS LocalStack;
- Multi-message send ( separator "//" );
- Reprocess DLQ queues;
- New Mock Mode ( Allow creation of mock response for queue);
- New theme color;
- MockMode: get request message field and use it at response message with placeholder ( %fieldName% )

SR-SQS is able to act as a generic consumer, producer and mock responses, to ensure your application workflow is running well.

## Json mock example
```json
{
  "mockList": [
    {
      "sourceQueue": "some-test-queue",
      "targetQueue": "another-test-queue",
      "messageToWait": "*",
      "mockResponse": "{\"status\":\"SUCCESS\"}"
    },
    {
      "sourceQueue": "monday-test-queue",
      "targetQueue": "friday-test-queue",
      "messageToWait": "*",
      "mockResponse": "{\"status\":\"SUCCESS\"}"
    }
  ]
}
```

# ScreenShot

![alt text](https://raw.githubusercontent.com/MDalfre/sr-sqs/main/.github/images/prtsc1.jpeg)
![alt text](https://raw.githubusercontent.com/MDalfre/sr-sqs/main/.github/images/prtsc2.jpeg)
![alt text](https://raw.githubusercontent.com/MDalfre/sr-sqs/main/.github/images/prtsc3.jpeg)

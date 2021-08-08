package service

import com.amazonaws.SdkClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder

class ConnectionService(
    serverUrl: String,
    accessKey: String,
    secretKey: String
) {
    var credentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)
    lateinit var sqs: AmazonSQS

    init {
        try {
            sqs = AmazonSQSClientBuilder
                .standard()
                .withCredentials(AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(serverUrl, Regions.US_EAST_1.name))
                .build()
        } catch (ex: SdkClientException) {
            println(ex.message)
        }
    }
}
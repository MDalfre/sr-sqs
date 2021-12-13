package service

import com.amazonaws.SdkClientException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import model.ConnectionSettings

class ConnectionService(
    connectionSettings: ConnectionSettings,
    private val communicationService: CommunicationService
) {
    private val selectedRegionName = connectionSettings.serverRegion.getName()
    lateinit var sqs: AmazonSQS

    init {
        try {
            communicationService.logInfo("Connecting SQS ...")
            sqs = AmazonSQSClientBuilder
                .standard()
                .withCredentials(
                    AWSStaticCredentialsProvider(
                        connectionSettings.credentialType.credential(connectionSettings)
                    )
                )
                .withEndpointConfiguration(
                    AwsClientBuilder.EndpointConfiguration(
                        connectionSettings.serverUrl,
                        selectedRegionName
                    )
                )
                .build()
            communicationService.logSuccess("AWS Connected")
        } catch (ex: SdkClientException) {
            communicationService.logError(ex.message)
        }
    }

    fun disconnect() {
        try {
            communicationService.logInfo("Disconnecting from server")
            sqs.shutdown()
        } catch (ex: SdkClientException) {
            communicationService.logError(ex.message)
            throw ex
        }
    }
}

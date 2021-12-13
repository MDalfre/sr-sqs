package service

import com.amazonaws.SdkClientException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import model.ConnectionSettings
import model.CredentialType

class ConnectionService(
    connectionSettings: ConnectionSettings,
    credentialType: CredentialType,
    private val logService: LogService
) {
    private val selectedRegionName = Regions.valueOf(connectionSettings.serverRegion).getName()
    lateinit var sqs: AmazonSQS

    init {
        try {
            logService.info("Connecting SQS ...")
            sqs = AmazonSQSClientBuilder
                .standard()
                .withCredentials(
                    AWSStaticCredentialsProvider(
                        credentialType.credential(connectionSettings)
                    )
                )
                .withEndpointConfiguration(
                    AwsClientBuilder.EndpointConfiguration(
                        connectionSettings.serverUrl,
                        selectedRegionName
                    )
                )
                .build()
        } catch (ex: SdkClientException) {
            logService.error(ex.message)
        }
    }

    fun disconnect() {
        try {
            logService.info("Disconnecting from server")
            sqs.shutdown()
        } catch (ex: SdkClientException) {
            logService.error(ex.message)
            throw ex
        }
    }
}

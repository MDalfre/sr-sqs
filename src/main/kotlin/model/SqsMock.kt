package model

data class SqsMock(
    val sourceQueue: String,
    val targetQueue: String,
    val messageToWait: String,
    val mockResponse: String
)

package com.example.gateway

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CaseStatus(
    @SerialName("receipt_number") val receiptNumber: String,
    @SerialName("status_code") val statusCode: String? = null,
    val status: String? = null,
    val message: String? = null,
    val raw: JsonElement? = null
)

@Serializable
data class FoiaResult(
    @SerialName("request_id") val requestId: String,
    val status: String? = null,
    val message: String? = null,
    val raw: JsonElement? = null
)

@Serializable
data class ProcessingTime(
    @SerialName("form_number") val formNumber: String,
    @SerialName("office_code") val officeCode: String,
    @SerialName("processing_time") val processingTime: String? = null,
    val message: String? = null,
    val raw: JsonElement? = null
)

@Serializable
data class CeacStatus(
    @SerialName("case_number") val caseNumber: String,
    val status: String,
    val message: String? = null
)

package com.zugaldia.speedofsound.core

import kotlinx.serialization.Serializable

@Serializable
enum class CredentialType {
    API_KEY
}

@Serializable
data class Credential(
    val id: String,
    val type: CredentialType,
    val name: String,
    val value: String
)

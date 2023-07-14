package com.simple.features.registration

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationReceivedRemoteModel(
    val login : String,
    val phoneNumber : String,
    val password : String
)

@Serializable
data class RegistrationResponseRemoteModel(
    val token : String
)
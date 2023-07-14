package com.simple.features.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginReceivedRemoteModel (
    val login : String,
    val password : String
)

@Serializable
data class LoginResponseRemoteModel(
    val token : String
)

package example.com.json

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterJson(val email: String, val password: String)
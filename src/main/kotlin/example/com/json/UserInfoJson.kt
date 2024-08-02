package example.com.json

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoJson(val id: Int, val email: String)

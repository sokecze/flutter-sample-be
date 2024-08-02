package example.com.model

import example.com.utils.getSalt

data class User(val id: Int, val email: String, val passwordHash: String) {
    val passwordSalt get() = passwordHash.getSalt()
}

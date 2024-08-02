package example.com.service

import Database
import example.com.json.UserInfoJson
import example.com.model.User
import example.com.utils.generatePasswordHash

class UserService(private val database: Database) {
    fun authenticateUser(email: String, password: String): User? {
        database.findUser(email)?.let {
            if (it.passwordHash == password.generatePasswordHash(it.passwordSalt)) {
                return it
            }
        }
        return null
    }

    fun registerUser(email: String, password: String) {
        database.addUser(email, password.generatePasswordHash())
    }

    fun getUser(id: Int): UserInfoJson? {
        return database.findUser(id)?.let { UserInfoJson(it.id, it.email) }
    }
}
import example.com.model.User

interface Database {
    fun addUser(email: String, passwordHash: String)

    fun findUser(id: Int): User?

    fun findUser(email: String): User?
}
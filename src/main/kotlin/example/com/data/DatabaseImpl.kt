package example.com.data

import Database
import example.com.DuplicateUserException
import example.com.EnvironmentVariables
import example.com.model.User
import java.sql.DriverManager
import java.sql.Statement

class DatabaseImpl : Database {

    companion object {
        private val connectionString =
            "jdbc:${EnvironmentVariables.DB_URL};" +
                    "database=${EnvironmentVariables.DB_NAME};" +
                    "user=${EnvironmentVariables.DB_USERNAME};" +
                    "password=${EnvironmentVariables.DB_PASSWORD};" +
                    "encrypt=true;" +
                    "trustServerCertificate=false;" +
                    "hostNameInCertificate=*.database.windows.net;" +
                    "loginTimeout=45;"
    }

    private fun <R> withStatement(block: Statement.() -> R): R =
        DriverManager.getConnection(connectionString).use {
            it.createStatement().use(block)
        }

    override fun addUser(email: String, passwordHash: String): Unit = withStatement {
        if (findUserStatement("email" to email) != null) throw DuplicateUserException(email)
        System.getenv()
        execute("INSERT INTO Users (email, password_hash) VALUES ('$email', '$passwordHash')")
    }

    override fun findUser(id: Int): User? = withStatement {
        return@withStatement findUserStatement("id" to "$id")
    }

    override fun findUser(email: String): User? = withStatement {
        return@withStatement findUserStatement("email" to email)
    }

    private fun Statement.findUserStatement(field: Pair<String, String>): User? {
        val result = executeQuery("SELECT * FROM Users WHERE ${field.first}='${field.second}'")
        return if (!result.next()) {
            null
        } else {
            User(
                id = result.getInt("id"),
                email = result.getString("email"),
                passwordHash = result.getString("password_hash"),
            )
        }
    }

}
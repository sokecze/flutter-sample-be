package example.com

enum class EnvironmentVariables {
    DB_URL,
    DB_NAME,
    DB_USERNAME,
    DB_PASSWORD,
    ;

    override fun toString() = requireNotNull(System.getenv(name)) { "$name environment variable is not set up." }
}
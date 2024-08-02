package example.com.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import example.com.DuplicateUserException
import example.com.json.UserRegisterJson
import example.com.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.logging.error
import org.koin.ktor.ext.inject
import java.sql.SQLException

fun Application.configureRouting() {
    val userService by inject<UserService>()

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()

    routing {
        post("/register") {
            try {
                val user = call.receive<UserRegisterJson>()
                userService.registerUser(user.email, user.password)
                call.respond(HttpStatusCode.Created)
            } catch (exception: Exception) {
                when (exception) {
                    is IllegalStateException, is JsonConvertException -> call.respondWithErrorLog(
                        HttpStatusCode.BadRequest,
                        exception,
                        "Incorrect request",
                    )

                    is SQLException -> call.respondWithErrorLog(
                        HttpStatusCode.ServiceUnavailable,
                        exception,
                        "Database error"
                    )

                    is DuplicateUserException -> call.respondWithErrorLog(
                        HttpStatusCode.Forbidden,
                        exception,
                        "User already exists"
                    )

                    else -> call.respondWithErrorLog(HttpStatusCode.InternalServerError, exception)
                }
            }
        }

        post("/login") {
            val credentials = call.receive<UserRegisterJson>()
            val user = userService.authenticateUser(credentials.email, credentials.password)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("userId", user.id)
                    .sign(Algorithm.HMAC256(secret))
                call.respond(hashMapOf("token" to token))
            }
        }

        authenticate("jwt") {
            get("/currentuser") {
                try {
                    val user = userService.getUser(call.currentUserId)
                    if (user != null) {
                        call.respond(user)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }
            }
        }
    }
}

private val ApplicationCall.currentUserId: Int
    get() = principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
        ?: throw IllegalStateException("Cannot get current user id")

suspend inline fun ApplicationCall.respondWithErrorLog(
    status: HttpStatusCode,
    exception: Throwable,
    message: String? = null,
) {
    application.environment.log.error(exception)
    if (message != null) {
        respond(status, message)
    } else {
        respond(status)
    }
}


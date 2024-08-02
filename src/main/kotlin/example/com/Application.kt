package example.com

import configureCors
import example.com.plugins.configureAuthentication
import example.com.plugins.configureRouting
import example.com.plugins.configureSerialization
import example.com.plugins.injectModules
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    EnvironmentVariables.entries.forEach { it.toString() }

    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureAuthentication()
    configureCors()
    configureRouting()
    injectModules()
}

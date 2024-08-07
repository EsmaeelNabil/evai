package de.thermondo

import de.thermondo.config.configureMonitoring
import de.thermondo.route.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

val logger = LoggerFactory.getLogger("Application")


fun Application.module() {
    configureMonitoring()
    configureRouting()
}

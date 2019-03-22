package org.atlanmod

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.util.concurrent.TimeUnit

fun main(args : Array<String>) {
    var monitor = buildMonitor()

    embeddedServer(Netty, 7070) {
        routing {
            post("/start") {
                println("start monitoring")
                monitor.run(Integer.valueOf(call.receiveParameters()["pid"]))
                call.respond(HttpStatusCode.Accepted)
            }

            post("/stop") {
                println("end monitoring")
                monitor.stop()
                call.respond(HttpStatusCode.Accepted)
                monitor = buildMonitor()
            }
        }
    }.start()
}

fun buildMonitor() : Monitor {
    return MonitorBuilder()
         .withChartDisplay()
         .withTdp(15.0)
         .withTdpFactor(0.7)
         .withRefreshFrequency(10, TimeUnit.NANOSECONDS)
         .build()
}

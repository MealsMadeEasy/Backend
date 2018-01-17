package com.mealsmadeeasy

import com.mealsmadeeasy.utils.firstBlocking
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing

private val port: Int
    get() = System.getenv("PORT")?.toIntOrNull() ?: 80

fun main(args: Array<String>) {
    println("Starting server on port $port...")
    embeddedServer(Netty, port) {
        routing {
            get("/") {
                call.respondText("This is not the endpoint you are looking for",
                        ContentType.Text.Plain, HttpStatusCode.NotFound)
            }

            get("/dbtest/") {
                call.respondText(FirebaseInstance.database.getReference("test")
                        .firstBlocking<String>().orEmpty())
            }
        }
    }.start(wait = true)
}

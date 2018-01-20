package com.mealsmadeeasy.endpoint

import com.mealsmadeeasy.utils.Json
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.response.respondText

data class Response(
        val body: String,
        val type: ContentType? = null,
        val code: HttpStatusCode? = null
) {

    companion object {
        inline fun <reified T> ofJson(body: T): Response {
            return Response(
                    body = Json.convertToJson(body),
                    type = ContentType.Application.Json,
                    code = HttpStatusCode.OK
            )
        }

        fun ofError(message: String, code: HttpStatusCode): Response {
            return Response(
                    body = message,
                    type = ContentType.Text.Plain,
                    code = code
            )
        }

        fun ofStatus(message: String): Response {
            return Response(
                    body = message,
                    type = ContentType.Text.Plain,
                    code = HttpStatusCode.OK
            )
        }
    }

}

suspend fun ApplicationCall.sendResponse(response: Response)
        = respondText(response.body, response.type, response.code)

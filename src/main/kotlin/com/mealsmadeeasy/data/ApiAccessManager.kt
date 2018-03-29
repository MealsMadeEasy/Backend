package com.mealsmadeeasy.data

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.endpoint.Response
import com.mealsmadeeasy.utils.firstBlocking
import com.mealsmadeeasy.utils.get
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.http.HttpStatusCode

object ApiAccessManager {

    private const val APP_ID_HEADER = "ApplicationID"
    private const val APP_KEY_HEADER = "ApiKey"

    private val db = FirebaseInstance.database

    fun requireApiAccess(call: ApplicationCall): Response? {
        return requireApiAccess(
                appId = call.request.headers[APP_ID_HEADER],
                apiKey = call.request.headers[APP_KEY_HEADER]
        )
    }

    fun requireApiAccess(appId: String?, apiKey: String?): Response? {
        if (appId == null) {
            return Response.ofError("No application ID specified", HttpStatusCode.BadRequest)
        } else if (apiKey == null) {
            return Response.ofError("No API key specified", HttpStatusCode.BadRequest)
        }

        return if (db["apiKeys/$appId"].firstBlocking<String>() == apiKey) {
            null
        } else {
            Response.ofError("Invalid API credentials", HttpStatusCode.Unauthorized)
        }
    }

}
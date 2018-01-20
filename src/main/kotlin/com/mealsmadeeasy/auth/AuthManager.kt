package com.mealsmadeeasy.auth

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.endpoint.Response
import com.mealsmadeeasy.utils.block
import org.jetbrains.ktor.http.HttpStatusCode

typealias UserId = String

object AuthManager {

    private val auth = FirebaseInstance.auth

    fun convertTokenToUserId(token: String): UserId? {
        return auth.verifyIdToken(token).block().uid
    }

    inline fun ensureValidUser(userToken: String?,
                               validTokenAction: (UserId) -> Response): Response {

        if (userToken == null) {
            return Response.ofError(
                    "Missing authentication token",
                    HttpStatusCode.BadRequest)
        }

        val userId = AuthManager.convertTokenToUserId(userToken)
                ?: return Response.ofError(
                        "Invalid authentication token",
                        HttpStatusCode.Unauthorized)

        return validTokenAction(userId)
    }

}
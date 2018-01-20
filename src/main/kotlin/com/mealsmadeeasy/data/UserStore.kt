package com.mealsmadeeasy.data

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.auth.AuthManager
import com.mealsmadeeasy.endpoint.Response
import com.mealsmadeeasy.model.UserProfile
import com.mealsmadeeasy.utils.firstBlocking
import org.jetbrains.ktor.http.HttpStatusCode

object UserStore {

    private val db = FirebaseInstance.database

    fun getPrivateUserProfile(userToken: String?): Response {
        return AuthManager.ensureValidUser(userToken) { userId ->
            Response.ofJson(db.getReference("privateProfiles").child(userId)
                    .firstBlocking<UserProfile>())
        }
    }

    fun updatePrivateUserProfile(userToken: String?,
                                 profile: UserProfile?): Response {
        if (profile == null) {
            return Response.ofError(
                    "Missing user profile parameter",
                    HttpStatusCode.BadRequest
            )
        }

        return AuthManager.ensureValidUser(userToken) { userId ->
            db.getReference("privateProfiles").child(userId).setValue(profile)
            Response.ofStatus("Ok")
        }
    }

}
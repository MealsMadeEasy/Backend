package com.mealsmadeeasy

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.database.FirebaseDatabase

object FirebaseInstance {

    private val serviceAccount = """
        {
          "type": "service_account",
          "project_id": "mealsmadeeasy-a4722",
          "private_key_id": "${envVar("MME_PRIVATE_KEY_ID")}",
          "private_key": "${envVar("MME_PRIVATE_KEY")}",
          "client_email": "${envVar("MME_CLIENT_EMAIL")}",
          "client_id": "${envVar("MME_CLIENT_ID")}",
          "auth_uri": "https://accounts.google.com/o/oauth2/auth",
          "token_uri": "https://accounts.google.com/o/oauth2/token",
          "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
          "client_x509_cert_url": "${envVar("MME_CLIENT_X509_CERT_URL")}"
        }
    """

    init {
        val credentials = FirebaseCredentials
                .fromCertificate(serviceAccount.byteInputStream())

        val options = FirebaseOptions.Builder()
                .setCredential(credentials)
                .setDatabaseUrl("https://mealsmadeeasy-a4722.firebaseio.com")
                .build()

        FirebaseApp.initializeApp(options)
    }

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private fun envVar(name: String): String {
        return System.getenv(name)
                ?: throw RuntimeException("$name environment variable not set")
    }

}
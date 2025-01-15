package com.ptboxchallenge

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
        // You can specify "*" to allow all hosts for now to see if CORS is the issue
        allowHost("ptbox-frontend.vercel.app", schemes = listOf("https"))
        allowHost("your-backend-server-url.com", schemes = listOf("https")) // Update with your backend URL
        allowHost("*", schemes = listOf("https")) // Allow any origin for testing
    }
}

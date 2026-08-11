package com.app.tastybuds.data.repo

import okhttp3.Interceptor
import okhttp3.Response

/** Attaches `Authorization: Bearer <jwt>` to every request except the login call. */
class AuthInterceptor(private val tokenStore: AuthTokenStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenStore.getToken()

        if (token.isNullOrBlank() || request.url.encodedPath.endsWith("/auth/login")) {
            return chain.proceed(request)
        }

        val authed = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authed)
    }
}

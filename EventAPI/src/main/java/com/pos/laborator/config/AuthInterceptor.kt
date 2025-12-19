package com.pos.laborator.config

import com.pos.laborator.grpc.AuthGrpcClient
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor(private val authGrpcClient: AuthGrpcClient) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Allow OPTIONS requests to pass through for CORS preflight
        if (request.method.equals("OPTIONS", ignoreCase = true)) {
            return true
        }

        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header")
            return false
        }

        val token = authHeader.substring(7)
        val isValid = runBlocking {
            authGrpcClient.validateToken(token)
        }

        if (!isValid) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token")
            return false
        }

        return true
    }
}
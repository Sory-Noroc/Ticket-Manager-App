package com.pos.laborator.grpc

import com.pos.laborator.protos.AuthServiceGrpcKt
import com.pos.laborator.protos.ValidateRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class AuthGrpcClient {

    @Value("\${AUTH_API_ADDRESS}")
    private lateinit var authApiAddress: String

    private val channel: ManagedChannel by lazy {
        ManagedChannelBuilder.forTarget(authApiAddress)
            .usePlaintext()
            .build()
    }

    private val stub: AuthServiceGrpcKt.AuthServiceCoroutineStub by lazy {
        AuthServiceGrpcKt.AuthServiceCoroutineStub(channel)
    }

    suspend fun validateToken(token: String): Boolean {
        if (token.isEmpty()) {
            return false
        }
        return try {
            val request = ValidateRequest.newBuilder().setToken(token).build()
            val response = stub.validateToken(request)
            response.valid
        } catch (e: Exception) {
            // Log the exception
            println("Error validating token: ${e.message}")
            false
        }
    }

    fun shutdown() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

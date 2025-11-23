package com.example.auth.grpc

import com.example.auth.model.Role
import com.example.auth.model.User
import com.example.auth.repository.UserRepository
import com.example.auth.service.JwtService
import io.grpc.Status
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.security.crypto.password.PasswordEncoder

@GrpcService
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) : AuthServiceGrpcKt.AuthServiceCoroutineImplBase() {

    override suspend fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw Status.NOT_FOUND.withDescription("User with email ${request.email} not found").asException()

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw Status.UNAUTHENTICATED.withDescription("Invalid credentials").asException()
        }

        val token = jwtService.generateToken(user)
        return LoginResponse.newBuilder().setToken(token).build()
    }

    override suspend fun validateToken(request: ValidateTokenRequest): ValidateTokenResponse {
        val claims = jwtService.validateTokenAndGetClaims(request.token) ?: return ValidateTokenResponse.newBuilder()
            .setIsValid(false).build()

        val userId = claims.get("userId", Integer::class.java)
        val email = claims.subject
        val role = claims.get("role", String::class.java)

        if (userId == null || email == null || role == null) {
            // Un token valid tre sa aiba toate claim urile
            return ValidateTokenResponse.newBuilder().setIsValid(false).build()
        }

        return ValidateTokenResponse.newBuilder()
            .setIsValid(true)
            .setUserId(userId.toInt()) // This is now safe
            .setEmail(email)
            .setRole(role)
            .build()
    }

    override suspend fun createUser(request: CreateUserRequest): UserResponse {
        // 1. Validate the admin token
        val adminClaims = jwtService.validateTokenAndGetClaims(request.adminToken)
        if (adminClaims == null || adminClaims["role", String::class.java] != Role.ADMIN.name) {
            throw Status.PERMISSION_DENIED.withDescription("Only admins can create users").asException()
        }

        // 2. Check if user already exists
        if (userRepository.findByEmail(request.email) != null) {
            throw Status.ALREADY_EXISTS.withDescription("User with email ${request.email} already exists").asException()
        }
        
        // 3. Validate the role string
        val userRole = try {
            Role.valueOf(request.role)
        } catch (e: IllegalArgumentException) {
            throw Status.INVALID_ARGUMENT.withDescription("Invalid role: ${request.role}").asException()
        }

        // 4. Create and save the new user
        val newUser = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = userRole
        )
        val savedUser = userRepository.save(newUser)

        // 5. Return the new user's data
        return UserResponse.newBuilder()
            .setUser(
                com.example.auth.grpc.User.newBuilder()
                    .setId(savedUser.id)
                    .setEmail(savedUser.email)
                    .setRole(savedUser.role.name)
            ).build()
    }
}

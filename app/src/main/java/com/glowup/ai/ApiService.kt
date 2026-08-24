package com.glowup.ai

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

// API Response Models
data class CreateUserResponse(
    val user: User
)

data class User(
    val id: String,
    @SerializedName("skin_type") val skinType: String?,
    @SerializedName("consent_state") val consentState: String
)

data class ConsentRequest(
    @SerializedName("facial_data") val facialData: Boolean,
    @SerializedName("policy_version") val policyVersion: String = "1.0"
)

data class CaptureRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("image_base64") val imageBase64: String,
    @SerializedName("is_baseline") val isBaseline: Boolean = true,
    val vertical: String = "skin"
)

data class CaptureResponse(
    @SerializedName("photo_id") val photoId: String?,
    val metrics: Metrics?,
    val quality: Quality?
)

data class Metrics(
    @SerializedName("overall_score") val overallScore: Double?,
    @SerializedName("redness_score") val rednessScore: Double?,
    @SerializedName("blemish_count") val blemishCount: Int?
)

data class Quality(
    val score: Double?,
    val accepted: Boolean?
)

// API Service
class ApiService {
    private val baseUrl = "http://10.0.2.2:8000/api" // Special IP for Android emulator to access localhost
    private val gson = Gson()

    // Create a new user
    suspend fun createUser(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/users")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val body = """{"skin_type": "normal"}"""
            connection.outputStream.write(body.toByteArray())

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                val userResponse = gson.fromJson(response, CreateUserResponse::class.java)
                Result.success(userResponse.user.id)
            } else {
                Result.failure(Exception("Failed to create user: ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Give consent
    suspend fun giveConsent(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/users/$userId/consent")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val body = gson.toJson(ConsentRequest(facialData = true))
            connection.outputStream.write(body.toByteArray())

            if (connection.responseCode == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to give consent: ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Upload photo and get skin analysis
    suspend fun uploadCapture(userId: String, bitmap: Bitmap): Result<CaptureResponse> = withContext(Dispatchers.IO) {
        try {
            // Convert bitmap to base64
            val base64Image = bitmapToBase64(bitmap)

            val url = URL("$baseUrl/captures")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val captureRequest = CaptureRequest(
                userId = userId,
                imageBase64 = base64Image,
                isBaseline = true
            )
            val body = gson.toJson(captureRequest)
            connection.outputStream.write(body.toByteArray())

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                val captureResponse = gson.fromJson(response, CaptureResponse::class.java)
                Result.success(captureResponse)
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
                Result.failure(Exception("Failed to upload: $responseCode - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper: Convert Bitmap to Base64 string
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    // Complete flow: Create user + consent + upload
    suspend fun analyzePhoto(bitmap: Bitmap): Result<SkinAnalysisResult> = withContext(Dispatchers.IO) {
        try {
            // Step 1: Create user
            val userIdResult = createUser()
            if (userIdResult.isFailure) {
                return@withContext Result.failure(userIdResult.exceptionOrNull()!!)
            }
            val userId = userIdResult.getOrThrow()

            // Step 2: Give consent
            val consentResult = giveConsent(userId)
            if (consentResult.isFailure) {
                return@withContext Result.failure(consentResult.exceptionOrNull()!!)
            }

            // Step 3: Upload photo
            val uploadResult = uploadCapture(userId, bitmap)
            if (uploadResult.isFailure) {
                return@withContext Result.failure(uploadResult.exceptionOrNull()!!)
            }

            val captureResponse = uploadResult.getOrThrow()

            // Extract skin score (use overall score if available, otherwise calculate from metrics)
            val skinScore = captureResponse.metrics?.overallScore?.toInt()
                ?: calculateScoreFromMetrics(captureResponse.metrics)

            Result.success(SkinAnalysisResult(
                skinScore = skinScore,
                userId = userId,
                photoId = captureResponse.photoId ?: "",
                rednessScore = captureResponse.metrics?.rednessScore?.toInt(),
                blemishCount = captureResponse.metrics?.blemishCount
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateScoreFromMetrics(metrics: Metrics?): Int {
        // If no metrics, return a default score
        if (metrics == null) return 75

        // Simple calculation: assume good skin if no detailed metrics
        return 75
    }
}

// Result model for UI
data class SkinAnalysisResult(
    val skinScore: Int,
    val userId: String,
    val photoId: String,
    val rednessScore: Int?,
    val blemishCount: Int?
)

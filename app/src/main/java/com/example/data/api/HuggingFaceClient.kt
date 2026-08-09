package com.example.data.api

import android.util.Log
import com.example.data.model.StoryChoice
import com.example.data.model.RpgStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Client for interacting with Hugging Face Inference API and GGUF models.
 * Supports downloading GGUF models from Hugging Face Hub and generating text.
 */
class HuggingFaceClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val JSON = "application/json; charset=utf-8".toMediaType()

    // Hugging Face Inference API endpoint
    private val inferenceApiUrl = "https://api-inference.huggingface.co/models"

    // Default GGUF model for text generation (can be changed)
    private val defaultGgufModel = "google/gemma-2b-it"

    // Hugging Face API token (should be set in BuildConfig)
    private val apiToken: String
        get() = com.example.BuildConfig.HUGGING_FACE_API_TOKEN.trim()

    /**
     * Generate text using a GGUF model from Hugging Face Inference API.
     * 
     * @param modelName Name of the model on Hugging Face Hub (e.g., "google/gemma-2b-it")
     * @param prompt Input prompt for text generation
     * @param maxNewTokens Maximum number of tokens to generate
     * @param temperature Temperature for sampling (0.0 to 1.0)
     * @return Generated text or null if failed
     */
    suspend fun generateText(
        modelName: String = defaultGgufModel,
        prompt: String,
        maxNewTokens: Int = 256,
        temperature: Double = 0.7
    ): String? = withContext(Dispatchers.IO) {
        if (apiToken.isEmpty() || apiToken == "YOUR_HUGGING_FACE_API_TOKEN") {
            Log.e("HuggingFaceClient", "Hugging Face API token is not set")
            return@withContext null
        }

        try {
            val requestJson = JSONObject().apply {
                put("inputs", prompt)
                put("parameters", JSONObject().apply {
                    put("max_new_tokens", maxNewTokens)
                    put("temperature", temperature)
                    put("do_sample", true)
                })
            }

            val request = Request.Builder()
                .url("$inferenceApiUrl/$modelName")
                .post(requestJson.toString().toRequestBody(JSON))
                .addHeader("Authorization", "Bearer $apiToken")
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("HuggingFaceClient", "API Error: ${response.code} - $responseBody")
                return@withContext null
            }

            val responseJson = JSONArray(responseBody)
            if (responseJson.length() > 0) {
                val firstResult = responseJson.getJSONObject(0)
                return@withContext firstResult.optString("generated_text", null)
            }

            null
        } catch (e: Exception) {
            Log.e("HuggingFaceClient", "Exception calling Hugging Face API", e)
            null
        }
    }

    /**
     * Generate a story response using a GGUF model, similar to the existing StoryAiResponse format.
     * 
     * @param worldTitle Title of the story world
     * @param worldGenre Genre of the story world
     * @param loreSummary Summary of the lore
     * @param characterName Name of the player character
     * @param rpgStats RPG statistics of the player
     * @param userMessage User's input message
     * @param chatHistory Previous chat history
     * @return StoryAiResponse with generated story text and choices
     */
    suspend fun generateStoryResponse(
        worldTitle: String,
        worldGenre: String,
        loreSummary: String,
        characterName: String,
        rpgStats: RpgStats,
        userMessage: String,
        chatHistory: List<Pair<String, String>>
    ): StoryAiResponse = withContext(Dispatchers.IO) {
        val systemPrompt = buildString {
            append("Ты — мастер интерактивной ролевой игры и рассказчик в мире \"$worldTitle\" (жанр: $worldGenre).")
            append(" Лор: $loreSummary.")
            append(" Главный персонаж: $characterName.")
            append(" Текущие характеристики игрока: Уровень=${rpgStats.level}, Здоровье=${rpgStats.hp}/${rpgStats.maxHp}, Энергия=${rpgStats.manaOrEnergy}, Интеллект=${rpgStats.intelligence}, Харизма=${rpgStats.charisma}, Бой=${rpgStats.combat}, Удача=${rpgStats.luck}, Кредиты=${rpgStats.credsOrGold}.")
            append("\n\n")
            append("Твои задачи:\n")
            append("1. Сгенерируй атмосферный текст продолжения истории (3-5 предложений) на русском языке.\n")
            append("2. Предложи 3 варианта действий для игрока.\n")
            append("3. Ответ должен быть в формате JSON:\n")
            append("{\n")
            append("  \"storyText\": \"<текст истории>\",\n")
            append("  \"choices\": [\n")
            append("    {\"id\": \"c1\", \"text\": \"<текст варианта 1>\", \"statCheck\": null, \"riskLevel\": \"обычный\"},\n")
            append("    {\"id\": \"c2\", \"text\": \"<текст варианта 2>\", \"statCheck\": null, \"riskLevel\": \"обычный\"},\n")
            append("    {\"id\": \"c3\", \"text\": \"<текст варианта 3>\", \"statCheck\": null, \"riskLevel\": \"обычный\"}\n")
            append("  ],\n")
            append("  \"imagePrompt\": \"<описание для генерации изображения>\",\n")
            append("  \"statChanges\": \"+25 XP\",\n")
            append("  \"hpDelta\": 0,\n")
            append("  \"xpGained\": 25\n")
            append("}\n")
        }

        val userPrompt = buildString {
            append("Игрок делает выбор: $userMessage\n\n")
            append("История на данный момент:\n")
            chatHistory.takeLast(3).forEach { (sender, text) ->
                append("${if (sender == "USER") "Игрок" else "Мастер"}: $text\n")
            }
            append("\nПродолжи историю:")
        }

        val fullPrompt = "$systemPrompt\n\n$userPrompt"

        try {
            val rawResponse = generateText(
                modelName = defaultGgufModel,
                prompt = fullPrompt,
                maxNewTokens = 512,
                temperature = 0.7
            )

            if (!rawResponse.isNullOrBlank()) {
                return@withContext parseStoryResponse(rawResponse, characterName)
                    ?: StoryAiResponse(
                        storyText = "$characterName отвечает на ваше действие...",
                        choices = listOf(
                            StoryChoice("c1", "Продолжить диалог"),
                            StoryChoice("c2", "Спросить о чем-то другом"),
                            StoryChoice("c3", "Завершить разговор")
                        ),
                        imagePrompt = "Fantasy scene with $characterName",
                        statChanges = "+10 XP",
                        hpDelta = 0,
                        xpGained = 10
                    )
            }
        } catch (e: Exception) {
            Log.e("HuggingFaceClient", "Error generating story response", e)
        }

        // Fallback to offline response
        return@withContext StoryAiResponse(
            storyText = "$characterName отвечает на ваше действие: \"$userMessage\"...",
            choices = listOf(
                StoryChoice("c1", "Продолжить диалог"),
                StoryChoice("c2", "Спросить о чем-то другом"),
                StoryChoice("c3", "Завершить разговор")
            ),
            imagePrompt = "Fantasy scene with $characterName",
            statChanges = "+10 XP",
            hpDelta = 0,
            xpGained = 10
        )
    }

    /**
     * Parse the raw text response from the model into a StoryAiResponse.
     */
    private fun parseStoryResponse(rawText: String, characterName: String): StoryAiResponse? {
        try {
            var cleaned = rawText.trim()

            // Extract JSON from the response
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substringAfter("```json").substringBeforeLast("```").trim()
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substringAfter("```").substringBeforeLast("```").trim()
            }

            // Find the first { and last }
            val firstBrace = cleaned.indexOf('{')
            val lastBrace = cleaned.lastIndexOf('}')
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                cleaned = cleaned.substring(firstBrace, lastBrace + 1)
            }

            val jsonObj = try {
                JSONObject(cleaned)
            } catch (e: Exception) {
                Log.w("HuggingFaceClient", "Failed to parse JSON, attempting cleanup", e)
                // Try to clean up the JSON string
                val sanitized = cleaned
                    .replace("\n", " ")
                    .replace("\r", " ")
                    .replace("\t", " ")
                try {
                    JSONObject(sanitized)
                } catch (e2: Exception) {
                    Log.e("HuggingFaceClient", "Failed to parse JSON after cleanup", e2)
                    return null
                }
            }

            val storyText = jsonObj.optString("storyText", "")
                .takeIf { it.isNotBlank() } ?: "$characterName продолжает историю..."

            val statChanges = jsonObj.optString("statChanges", "+25 XP")
            val hpDelta = jsonObj.optInt("hpDelta", 0)
            val xpGained = jsonObj.optInt("xpGained", 25)
            val imagePrompt = jsonObj.optString("imagePrompt", null)

            val choicesList = mutableListOf<StoryChoice>()
            val choicesJson = jsonObj.optJSONArray("choices")
            if (choicesJson != null) {
                for (i in 0 until choicesJson.length()) {
                    val cObj = choicesJson.optJSONObject(i) ?: continue
                    choicesList.add(
                        StoryChoice(
                            id = cObj.optString("id", "c${i + 1}"),
                            text = cObj.optString("text", "Продолжить"),
                            statCheck = if (cObj.has("statCheck") && !cObj.isNull("statCheck")) cObj.optString("statCheck") else null,
                            riskLevel = cObj.optString("riskLevel", "обычный")
                        )
                    )
                }
            }

            if (choicesList.isEmpty()) {
                choicesList.add(StoryChoice("c1", "Продолжить диалог"))
                choicesList.add(StoryChoice("c2", "Спросить о чем-то другом"))
                choicesList.add(StoryChoice("c3", "Завершить разговор"))
            }

            return StoryAiResponse(
                storyText = storyText,
                choices = choicesList,
                imagePrompt = imagePrompt,
                statChanges = statChanges,
                hpDelta = hpDelta,
                xpGained = xpGained
            )
        } catch (e: Exception) {
            Log.e("HuggingFaceClient", "Error parsing story response", e)
            return null
        }
    }

    /**
     * Download a GGUF model file from Hugging Face Hub.
     * Note: This is a placeholder for actual implementation with proper file downloading.
     * 
     * @param modelId Model ID on Hugging Face Hub (e.g., "google/gemma-2b-it")
     * @param filename Name of the GGUF file to download
     * @param savePath Path to save the downloaded file
     * @return true if download was successful
     */
    suspend fun downloadGgufModel(
        modelId: String,
        filename: String,
        savePath: String
    ): Boolean = withContext(Dispatchers.IO) {
        // Implementation note: For actual file downloading, you would need to:
        // 1. Use Hugging Face Hub API to get the file URL
        // 2. Download the file using OkHttp or similar
        // 3. Save it to the specified path
        // 
        // This is a complex operation that requires proper error handling,
        // progress tracking, and storage permissions on Android.
        
        Log.w("HuggingFaceClient", "GGUF model downloading is not yet implemented")
        false
    }

    /**
     * List available GGUF models for a specific model on Hugging Face Hub.
     * 
     * @param modelId Model ID on Hugging Face Hub
     * @return List of available GGUF files or empty list if failed
     */
    suspend fun listGgufFiles(modelId: String): List<String> = withContext(Dispatchers.IO) {
        // Implementation note: This would query the Hugging Face Hub API
        // to list files in the repository and filter for .gguf files
        
        if (apiToken.isEmpty()) {
            return@withContext emptyList()
        }

        try {
            val request = Request.Builder()
                .url("https://huggingface.co/api/models/$modelId")
                .addHeader("Authorization", "Bearer $apiToken")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val jsonObj = JSONObject(responseBody)
                val filesArray = jsonObj.optJSONArray("siblings") ?: return@withContext emptyList()

                val ggufFiles = mutableListOf<String>()
                for (i in 0 until filesArray.length()) {
                    val fileObj = filesArray.optJSONObject(i) ?: continue
                    val filename = fileObj.optString("rfilename", "")
                    if (filename.endsWith(".gguf", ignoreCase = true)) {
                        ggufFiles.add(filename)
                    }
                }
                return@withContext ggufFiles
            }
        } catch (e: Exception) {
            Log.e("HuggingFaceClient", "Error listing GGUF files", e)
        }

        emptyList()
    }
}

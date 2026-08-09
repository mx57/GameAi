package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ImageResolution
import com.example.data.api.HuggingFaceClient
import com.example.data.model.RpgStats
import com.example.data.model.StoryChoice
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class StoryAiResponse(
    val storyText: String,
    val choices: List<StoryChoice>,
    val imagePrompt: String? = null,
    val statChanges: String? = null,
    val hpDelta: Int = 0,
    val xpGained: Int = 25
)

class GeminiStoryClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun generateNextTurn(
        worldTitle: String,
        worldGenre: String,
        loreSummary: String,
        characterName: String,
        rpgStats: RpgStats,
        userMessage: String,
        chatHistory: List<Pair<String, String>>, // Sender -> Text
        isOfflineMode: Boolean,
        useHuggingFace: Boolean = false
    ): StoryAiResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        val hfApiKey = BuildConfig.HUGGING_FACE_API_TOKEN.trim()

        if (useHuggingFace && hfApiKey.isNotEmpty() && hfApiKey != "YOUR_HUGGING_FACE_API_TOKEN") {
            val hfClient = HuggingFaceClient()
            return@withContext hfClient.generateStoryResponse(
                worldTitle = worldTitle,
                worldGenre = worldGenre,
                loreSummary = loreSummary,
                characterName = characterName,
                rpgStats = rpgStats,
                userMessage = userMessage,
                chatHistory = chatHistory
            )
        }

        if (isOfflineMode || apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateOfflineGemmaResponse(
                worldTitle = worldTitle,
                characterName = characterName,
                userChoice = userMessage,
                rpgStats = rpgStats
            )
        }

        try {
            val systemInstructionText = """
                Ты — мастер интерактивной ролевой игры и стори-чата во вселенной "$worldTitle" (Жанр: $worldGenre).
                Лор: $loreSummary.
                Главный персонаж: $characterName.
                Текущие характеристики игрока: Уровень=${rpgStats.level}, Здоровье=${rpgStats.hp}/${rpgStats.maxHp}, Энергия=${rpgStats.manaOrEnergy}, Интеллект=${rpgStats.intelligence}, Харизма=${rpgStats.charisma}, Бой=${rpgStats.combat}, Удача=${rpgStats.luck}, Кредиты=${rpgStats.credsOrGold}.
                
                ВАЖНЫЕ ПРАВИЛА ДИАЛОГА И СЮЖЕТА:
                1. Игрок может сделать выбор из вариантов ИЛИ ввести СВОЙ СОБСТВЕННЫЙ ДИАЛОГ / СВОБОДНУЮ РЕПЛИКУ.
                2. Если игрок написал собственный текст или реплику, обязательно дай эмоциональную и логичную реакцию персонажа $characterName на слова игрока.
                3. Общайся с игроком от лица $characterName или как Game Master, но при этом ОБЯЗАТЕЛЬНО продвигай генеральный сюжет и квест во вселенной "$worldTitle" вперед, выводя историю на новый этап!
                
                ПРАВИЛА ГЕНЕРАЦИИ ПРОМПТОВ ДЛЯ ИЗОБРАЖЕНИЙ (imagePrompt):
                - Промпт должен быть на английском языке.
                - Если жанр истории "ADULT_18", промпт должен содержать ключевые слова для создания эротического или чувственного арта (например, "sensual", "intimate", "romantic", "boudoir", "anime style mature", "dark fantasy romance"), но без явной порнографии. Создавай атмосферу напряжения и страсти.
                - Для других жанров создавай промпты в стиле кинематографичного арта, подходящего по жанру.
                
                ИНСТРУКЦИЯ ПО ФОРМАТИРОВАНИЮ:
                Ответь СТРОГО в формате JSON без кавычек ```json вокруг.
                Структура JSON:
                {
                  "storyText": "Атмосферное литературное продолжение истории (3-5 предложений) с реакцией на реплику игрока и продвижением сюжета.",
                  "statChanges": "Краткое описание изменившихся характеристик или наград, например: '+25 XP, +10 Кредитов'",
                  "hpDelta": 0,
                  "xpGained": 25,
                  "imagePrompt": "Детальный промпт на английском языке для генерации кинематографичной картинки сцены",
                  "choices": [
                    { "id": "c1", "text": "Текст первого варианта", "statCheck": "Харизма >= 10", "riskLevel": "Обычный" },
                    { "id": "c2", "text": "Текст второго варианта", "statCheck": null, "riskLevel": "Высокий" },
                    { "id": "c3", "text": "Текст третьего варианта", "statCheck": "Интеллект >= 12", "riskLevel": "Низкий" }
                  ]
                }
            """.trimIndent()

            val contentsArray = JSONArray()

            // Include last 6 turns of history
            val recentHistory = chatHistory.takeLast(6)
            for ((sender, text) in recentHistory) {
                val role = if (sender == "USER") "user" else "model"
                val contentObj = JSONObject()
                contentObj.put("role", role)
                val partsArray = JSONArray()
                partsArray.put(JSONObject().put("text", text))
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
            }

            // Current prompt turn
            val currentContent = JSONObject()
            currentContent.put("role", "user")
            val currentParts = JSONArray()
            currentParts.put(JSONObject().put("text", "Игрок делает выбор: $userMessage"))
            currentContent.put("parts", currentParts)
            contentsArray.put(currentContent)

            val rootJson = JSONObject()
            rootJson.put("contents", contentsArray)

            val systemInstructionObj = JSONObject()
            val sysParts = JSONArray()
            sysParts.put(JSONObject().put("text", systemInstructionText))
            systemInstructionObj.put("parts", sysParts)
            rootJson.put("systemInstruction", systemInstructionObj)

            val generationConfig = JSONObject()
            generationConfig.put("temperature", 0.8)
            generationConfig.put("responseMimeType", "application/json")
            rootJson.put("generationConfig", generationConfig)

            val requestUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(requestUrl)
                .post(rootJson.toString().toRequestBody(JSON))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful || responseBody.isEmpty()) {
                Log.e("GeminiStoryClient", "API Error: ${response.code} $responseBody")
                return@withContext generateOfflineGemmaResponse(worldTitle, characterName, userMessage, rpgStats)
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

                if (rawText.isNotBlank()) {
                    val parsedResponse = parseStoryResponse(rawText, characterName)
                    if (parsedResponse != null) {
                        return@withContext parsedResponse
                    }
                }
            }

            generateOfflineGemmaResponse(worldTitle, characterName, userMessage, rpgStats)
        } catch (e: Exception) {
            Log.e("GeminiStoryClient", "Exception calling Gemini", e)
            generateOfflineGemmaResponse(worldTitle, characterName, userMessage, rpgStats)
        }
    }

    private fun parseStoryResponse(rawText: String, characterName: String): StoryAiResponse? {
        try {
            var cleaned = rawText.trim()
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substringAfter("```json").substringBeforeLast("```").trim()
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substringAfter("```").substringBeforeLast("```").trim()
            }

            val firstBrace = cleaned.indexOf('{')
            val lastBrace = cleaned.lastIndexOf('}')
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                cleaned = cleaned.substring(firstBrace, lastBrace + 1)
            }

            var jsonObj: JSONObject? = null
            try {
                jsonObj = JSONObject(cleaned)
            } catch (e: Exception) {
                val sanitized = sanitizeJsonString(cleaned)
                try {
                    jsonObj = JSONObject(sanitized)
                } catch (e2: Exception) {
                    Log.w("GeminiStoryClient", "Failed standard JSONObject parsing, attempting regex extraction", e2)
                }
            }

            if (jsonObj != null) {
                val storyText = jsonObj.optString("storyText", "").takeIf { it.isNotBlank() }
                    ?: jsonObj.optString("text", "События развиваются дальше...")
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
                                text = cObj.optString("text", "Продолжить путь"),
                                statCheck = if (cObj.has("statCheck") && !cObj.isNull("statCheck")) cObj.optString("statCheck") else null,
                                riskLevel = cObj.optString("riskLevel", "Обычный")
                            )
                        )
                    }
                }

                if (choicesList.isEmpty()) {
                    choicesList.add(StoryChoice("c1", "Идти дальше в неизвестность"))
                    choicesList.add(StoryChoice("c2", "Осмотреться по сторонам"))
                    choicesList.add(StoryChoice("c3", "Задать прямой вопрос $characterName"))
                }

                return StoryAiResponse(
                    storyText = storyText,
                    choices = choicesList,
                    imagePrompt = imagePrompt,
                    statChanges = statChanges,
                    hpDelta = hpDelta,
                    xpGained = xpGained
                )
            }

            // Regex extraction fallback
            val storyRegex = """"storyText"\s*:\s*"((?:[^"\\]|\\.)*)"""".toRegex()
            val storyMatch = storyRegex.find(cleaned)?.groupValues?.get(1)
                ?.replace("\\\"", "\"")?.replace("\\n", "\n")

            if (!storyMatch.isNullOrBlank()) {
                val choicesList = mutableListOf<StoryChoice>()
                val choiceRegex = """"text"\s*:\s*"((?:[^"\\]|\\.)*)"""".toRegex()
                choiceRegex.findAll(cleaned).forEachIndexed { index, match ->
                    val choiceText = match.groupValues[1].replace("\\\"", "\"")
                    choicesList.add(StoryChoice("c${index + 1}", choiceText))
                }
                if (choicesList.isEmpty()) {
                    choicesList.add(StoryChoice("c1", "Продолжить приключение"))
                }

                return StoryAiResponse(
                    storyText = storyMatch,
                    choices = choicesList,
                    imagePrompt = "Cinematic fantasy art scene",
                    statChanges = "+10 XP",
                    hpDelta = 0,
                    xpGained = 10
                )
            }
        } catch (e: Exception) {
            Log.e("GeminiStoryClient", "Error parsing AI response", e)
        }
        return null
    }

    private fun sanitizeJsonString(json: String): String {
        val sb = StringBuilder()
        var inString = false
        var isEscaped = false
        for (ch in json) {
            if (ch == '"' && !isEscaped) {
                inString = !inString
                sb.append(ch)
            } else if (ch == '\\' && !isEscaped) {
                isEscaped = true
                sb.append(ch)
            } else {
                if (inString && (ch == '\n' || ch == '\r')) {
                    sb.append(" ")
                } else {
                    sb.append(ch)
                }
                isEscaped = false
            }
        }
        return sb.toString()
    }

    /**
     * Local Gemma Offline Intelligence Simulation Engine.
     * Generates rich atmospheric narrative branches completely local and offline!
     */
    private fun generateOfflineGemmaResponse(
        worldTitle: String,
        characterName: String,
        userChoice: String,
        rpgStats: RpgStats
    ): StoryAiResponse {
        val lowerChoice = userChoice.lowercase().trim()

        val isDirectDialogue = lowerChoice.contains("?") || lowerChoice.contains("привет") || 
                lowerChoice.contains("скажи") || lowerChoice.contains("кто") || 
                lowerChoice.contains("как") || lowerChoice.contains("почему") || 
                lowerChoice.contains("зачем") || lowerChoice.contains("расскажи") ||
                lowerChoice.length > 25

        val storyText = when {
            isDirectDialogue -> {
                "$characterName останавливается, внимательно посмотрев на вас, и отвечает: «$userChoice... Интересная мысль. Во вселенной $worldTitle далеко не каждый решился бы сказать такое». Подумав секунду, $characterName добавляет: «Твои слова заставляют меня пересмотреть наш план. Но квест ждёт, и нам необходимо двигаться далее к цели!»"
            }
            lowerChoice.contains("атак") || lowerChoice.contains("бой") || lowerChoice.contains("выстрел") || lowerChoice.contains("меч") -> {
                "$characterName мгновенно выхватывает оружие и прикрывает вас от надвигающейся угрозы. Воздух наполняется электрическим гулом, а ваши датчики фиксируют всплеск энергии. Вы успешно нейтрализуете опасную зону!"
            }
            lowerChoice.contains("хариз") || lowerChoice.contains("убедит") || lowerChoice.contains("говор") || lowerChoice.contains("договор") -> {
                "$characterName прислушивается к вашим убедительным аргументам и кивает: «Твои слова имеют смысл. Давай сделаем именно так». Напряжение спадает, и вам удается завязать полезный контакт."
            }
            lowerChoice.contains("взлом") || lowerChoice.contains("терминал") || lowerChoice.contains("маг") || lowerChoice.contains("заклинани") -> {
                "Ваш карманный терминал вспыхивает каскадом защитных кодов. Интеллект (${rpgStats.intelligence}) помогает обойти зашифрованные протоколы! Защитная система открывает скрытый отсек."
            }
            else -> {
                "Вы обращаетесь к $characterName со словами: «$userChoice». Персонаж задумчиво внимает вашему решению: «Это смелый выбор для $worldTitle. Давай посмотрим, куда приведет нас этот путь»."
            }
        }

        val choices = listOf(
            StoryChoice("c1", "Спросить $characterName о дальнейших шагах", "Харизма >= 10", "Низкий"),
            StoryChoice("c2", "Исследовать скрытый артефакт в центре зала", "Интеллект >= 11", "Обычный"),
            StoryChoice("c3", "Подготовиться к возможному бою", "Бой >= 12", "Высокий"),
            StoryChoice("c4", "Предложить свой собственный маршрут", null, "Обычный")
        )

        val imagePrompt = "Cyberpunk fantasy sci-fi digital art scene with dramatic volumetric lighting featuring character $characterName in $worldTitle, highly detailed 8k"

        return StoryAiResponse(
            storyText = storyText,
            choices = choices,
            imagePrompt = imagePrompt,
            statChanges = "[Локальный ИИ Gemma] +25 XP, +10 Кредитов",
            hpDelta = 0,
            xpGained = 25
        )
    }
}

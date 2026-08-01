package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.StoryMessage
import com.example.data.model.World
import java.io.File
import java.io.FileOutputStream

object StoryExporter {

    fun exportStoryToTextPdf(context: Context, world: World, messages: List<StoryMessage>): File? {
        return try {
            val exportDir = File(context.cacheDir, "story_exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val file = File(exportDir, "${world.title.replace(" ", "_")}_Story.txt")
            val writer = FileOutputStream(file).bufferedWriter()

            writer.write("=========================================\n")
            writer.write("  ${world.title.uppercase()}\n")
            writer.write("  Жанр: ${world.genre.titleRu}\n")
            writer.write("  Автор: ${world.authorName}\n")
            writer.write("  Персонаж: ${world.primaryCharacterName}\n")
            writer.write("=========================================\n\n")

            writer.write("--- ЛОР И ПРЕДЫСТОРИЯ ---\n")
            writer.write("${world.loreSummary}\n\n")

            writer.write("--- ГЛАВА I: НАЧАЛО ИСТОРИИ ---\n\n")

            for (msg in messages) {
                val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(msg.timestamp))
                if (msg.sender == "USER") {
                    writer.write("[$time] ВЫБОР ИГРОКА:\n")
                    writer.write("> ${msg.text}\n\n")
                } else {
                    writer.write("[$time] ${msg.senderName}:\n")
                    writer.write("${msg.text}\n")
                    if (!msg.statChanges.isNull_or_Empty()) {
                        writer.write("  [Изменения: ${msg.statChanges}]\n")
                    }
                    writer.write("\n")
                }
            }

            writer.write("=========================================\n")
            writer.write("Сгенерировано в приложении Realm Story Chat\n")
            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportStoryToEpubHtml(context: Context, world: World, messages: List<StoryMessage>): File? {
        return try {
            val exportDir = File(context.cacheDir, "story_exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val file = File(exportDir, "${world.title.replace(" ", "_")}_EpubBook.html")
            val writer = FileOutputStream(file).bufferedWriter()

            writer.write("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n")
            writer.write("<title>${world.title}</title>\n")
            writer.write("<style>\n")
            writer.write("body { font-family: 'Georgia', serif; background-color: #120e24; color: #e2e8f0; padding: 20px; line-height: 1.6; }\n")
            writer.write("h1 { color: #a855f7; text-align: center; border-bottom: 2px solid #06b6d4; padding-bottom: 10px; }\n")
            writer.write(".subtitle { text-align: center; color: #94a3b8; font-style: italic; margin-bottom: 30px; }\n")
            writer.write(".character-msg { background: #1a1532; border-left: 4px solid #a855f7; padding: 12px 16px; margin: 15px 0; border-radius: 8px; }\n")
            writer.write(".user-msg { background: #0f172a; border-right: 4px solid #06b6d4; padding: 12px 16px; margin: 15px 0; text-align: right; border-radius: 8px; font-weight: bold; }\n")
            writer.write(".author { color: #fde047; font-weight: bold; }\n")
            writer.write("</style>\n</head>\n<body>\n")

            writer.write("<h1>${world.title}</h1>\n")
            writer.write("<div class=\"subtitle\">Жанр: ${world.genre.titleRu} | Автор: ${world.authorName}</div>\n")

            writer.write("<div class=\"character-msg\"><span class=\"author\">Лор Вселенной:</span><p>${world.loreSummary}</p></div>\n")

            for (msg in messages) {
                if (msg.sender == "USER") {
                    writer.write("<div class=\"user-msg\"><span style=\"color:#06b6d4;\">Ваш выбор:</span> ${msg.text}</div>\n")
                } else {
                    writer.write("<div class=\"character-msg\"><span class=\"author\">${msg.senderName}:</span><p>${msg.text}</p></div>\n")
                }
            }

            writer.write("</body>\n</html>")
            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun String?.isNull_or_Empty(): Boolean = this == null || this.isEmpty()
}

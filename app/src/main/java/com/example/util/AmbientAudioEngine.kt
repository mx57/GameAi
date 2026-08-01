package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.WorldGenre
import kotlinx.coroutines.*
import kotlin.math.sin
import kotlin.random.Random

enum class AmbientPreset(val titleRu: String, val description: String) {
    CYBERPUNK("Киберпанк Дождь и Дрон", "Гудящий неоновый басс с шумом дождя"),
    DARK_FANTASY("Фэнтези Подземелье", "Эфирный ветер и гипнотические колокола"),
    SCI_FI("Космический Корабль", "Глубокий резонансный гул реактора"),
    DETECTIVE("Нуарный Дождь", "Теплый ночной дождь и бархатные тона"),
    POST_APOCALYPSE("Пустошь", "Сухой ветер и зернистый эмбиент")
}

object AmbientAudioEngine {

    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var isPlaying = false
        private set

    var currentPreset: AmbientPreset = AmbientPreset.CYBERPUNK
        private set

    var volume: Float = 0.35f
        set(value) {
            field = value.coerceIn(0f, 1f)
            try {
                audioTrack?.setVolume(field)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun startAmbient(preset: AmbientPreset = currentPreset) {
        currentPreset = preset
        if (isPlaying) {
            stopAmbient()
        }

        isPlaying = true
        synthJob = scope.launch {
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ) * 2

            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.setVolume(volume)
                audioTrack?.play()

                val buffer = ShortArray(1024)
                var phase1 = 0.0
                var phase2 = 0.0
                var lfoPhase = 0.0

                while (isPlaying && isActive) {
                    val freq1 = when (currentPreset) {
                        AmbientPreset.CYBERPUNK -> 110.0 // Low A synth drone
                        AmbientPreset.DARK_FANTASY -> 264.0 // Ethereal Middle C
                        AmbientPreset.SCI_FI -> 55.0 // Deep sub-bass reactor rumble
                        AmbientPreset.DETECTIVE -> 146.83 // Low D jazz tone
                        AmbientPreset.POST_APOCALYPSE -> 82.41 // Low E wasteland tone
                    }

                    val freq2 = when (currentPreset) {
                        AmbientPreset.CYBERPUNK -> 165.0 // Perfect fifth
                        AmbientPreset.DARK_FANTASY -> 528.0 // Solfeggio chime pitch
                        AmbientPreset.SCI_FI -> 110.0 // Octave harmonic
                        AmbientPreset.DETECTIVE -> 220.0
                        AmbientPreset.POST_APOCALYPSE -> 123.47
                    }

                    for (i in buffer.indices) {
                        lfoPhase += 0.0003
                        val lfo = (sin(lfoPhase) + 1.0) * 0.5 // 0.0 to 1.0 modulation

                        // Waveform synthesis
                        val tone1 = sin(phase1) * 0.3
                        val tone2 = sin(phase2) * (0.15 * lfo)
                        val noise = (Random.nextFloat() * 2f - 1f) * 0.08 * (0.5 + 0.5 * lfo)

                        val sampleValue = ((tone1 + tone2 + noise) * 32767 * 0.4).toInt()
                            .coerceIn(-32768, 32767)

                        buffer[i] = sampleValue.toShort()

                        phase1 += 2.0 * Math.PI * freq1 / sampleRate
                        phase2 += 2.0 * Math.PI * freq2 / sampleRate

                        if (phase1 > 2.0 * Math.PI) phase1 -= 2.0 * Math.PI
                        if (phase2 > 2.0 * Math.PI) phase2 -= 2.0 * Math.PI
                    }

                    audioTrack?.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopAmbient() {
        isPlaying = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
    }

    fun toggleAmbient() {
        if (isPlaying) stopAmbient() else startAmbient()
    }

    fun setGenrePreset(genre: WorldGenre) {
        val newPreset = when (genre) {
            WorldGenre.CYBERPUNK -> AmbientPreset.CYBERPUNK
            WorldGenre.DARK_FANTASY -> AmbientPreset.DARK_FANTASY
            WorldGenre.SCI_FI -> AmbientPreset.SCI_FI
            WorldGenre.DETECTIVE -> AmbientPreset.DETECTIVE
            WorldGenre.POST_APOCALYPSE -> AmbientPreset.POST_APOCALYPSE
        }
        if (isPlaying && newPreset != currentPreset) {
            startAmbient(newPreset)
        } else {
            currentPreset = newPreset
        }
    }
}

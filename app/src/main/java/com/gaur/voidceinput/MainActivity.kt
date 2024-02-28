package com.gaur.voidceinput

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var outputTV: TextView
    private lateinit var micIV: ImageView
    private lateinit var tts: TextToSpeech
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private val REQUEST_CODE_RECORD_AUDIO = 2
    private var recordedSpeech: String? = null
    var childVoice:Voice? = null
    var maleVoice:Voice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        outputTV = findViewById(R.id.idTVOutput)
        micIV = findViewById(R.id.idIVMic)
        val play = findViewById<Button>(R.id.play)
        val male = findViewById<Button>(R.id.male)
        val female = findViewById<Button>(R.id.female)



        tts = TextToSpeech(this, this)

        micIV.setOnClickListener {
            // Start speech recognition
            startRecording()

        }



        female.setOnClickListener {
            val text = outputTV.text.toString()
            speak(text, VOICE_TYPE_FEMALE)
            Log.e("VoiceDebug2", "male_voice")
        }
        male.setOnClickListener {
            val text = outputTV.text.toString()
            speak(text, VOICE_TYPE_MALE)
            Log.e("VoiceDebug2", "male_voice")
        }

        play.setOnClickListener {
            val text = outputTV.text.toString()
            speak(text, VOICE_TYPE_CHILD)
            Log.e("VoiceDebug3", "child_voice")
        }


    }
    private fun speak(text: String, voiceType: Int) {
        val voiceName = getVoiceName(voiceType)
        val voice = getVoiceForName(voiceName)

        if (voice != null) {
            tts.voice = voice
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            val errorMessage = "Voice not found for voice name: $voiceName"
            Log.e("VoiceDebug", errorMessage)
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }



//    private fun getVoiceForType(voiceType: Int, language: Locale): Voice? {
//        val voices = tts.voices
//        for (voice in voices) {
//            // Log voice names for debugging
//            Log.d("VoiceDebug", "Available Voice: ${voice.name}")
//            Log.d("VoiceDebug1", "Available Voice: $language")
//
//            // Check voice type and language
//            if (voice.locale == language && voice.name.contains(getVoiceName(voiceType), ignoreCase = true)) {
//                return voice
//            }
//        }
//        return null
//    }
private fun getVoiceForName(voiceName: String): Voice? {
    val voices = tts.voices
    for (voice in voices) {
        // Log voice names for debugging
        Log.d("VoiceDebug", "Available Voice: ${voice.name}")

        if (voice.name.equals(voiceName, ignoreCase = true)) {
            return voice
        }
    }
    return null
}

    private fun getVoiceName(voiceType: Int): String {
        return when (voiceType) {
            VOICE_TYPE_MALE -> {
                "hi-in-x-hia-local" // Example voice name for English (United States)
            }
            VOICE_TYPE_CHILD -> {
                "nl-nl-x-dma-local" // Adjust this according to your actual voice names
            }
            VOICE_TYPE_FEMALE -> {
                "es-US-language" // Adjust this according to your actual voice names
            }
            else -> {
                "en-us-x-iol-local" // Default to male voice for unrecognized types
            }
        }
    }




    private fun startRecording() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to record")

        try {
            startActivityForResult(intent, REQUEST_CODE_RECORD_AUDIO)
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_RECORD_AUDIO) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                recordedSpeech = res?.get(0)
                if (recordedSpeech != null) {
                    outputTV.text = recordedSpeech
                }
            }
        }
    }

    private fun playRecordedSpeech(voiceType: Int) {
        recordedSpeech?.let { text ->
            val voiceName = getVoiceName(voiceType)
            speak(text, voiceType)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set up voices and language here
            setupVoices()
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupVoices() {
        tts.language = Locale.US

        // Load custom voices
        val maleVoiceName = "voice"
        val childVoiceName = "child"

        val maleVoiceResId = resources.getIdentifier(maleVoiceName, "raw", packageName)
        val childVoiceResId = resources.getIdentifier(childVoiceName, "raw", packageName)

        val maleVoicePath = "android.resource://$packageName/$maleVoiceResId"
        val childVoicePath = "android.resource://$packageName/$childVoiceResId"
        Log.d("pathsala", maleVoiceResId.toString())
        Log.d("pathsala", childVoiceResId.toString())


        tts.addSpeech(maleVoiceName, maleVoicePath)
        tts.addSpeech(childVoiceName, childVoicePath)

        // Set the loaded custom voices
        maleVoice = Voice(maleVoiceName, Locale.US, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_LOW, false, null)
        childVoice = Voice(childVoiceName, Locale.US, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_LOW, false, null)
    }






    companion object {
        private const val VOICE_TYPE_MALE = 1
        private const val VOICE_TYPE_CHILD = 2
        private const val VOICE_TYPE_FEMALE=3
    }
}

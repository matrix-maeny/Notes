package com.matrix_maeny.mynotes;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

// ======================== voices =========================//
/*
format:
    Set<String> a = new HashSet<String>();
    a.add("male");
    Voice v=new Voice("en-us-x-iol-local",new Locale("en","US"),400,200,false,a);
    textToSpeech.setVoice(v);

    male: en-us-x-iol-local
    male: en-us-x-iom-local
    male: es-us-x-esd-local *
    male: es-us-x-esf-local
    male: en-in-x-ene-local **
    male: en-gb-x-rjs-local **
    male: en-gb-x-gbd-local **



    female: en-us-x-sfg-local
    female: es-ES-language
    female: es-us-x-esc-local
    female: en-in-x-cxx-local
    female: es-US-language *
    female: en-in-x-ahp-local *
    female: en-gb-x-gba-local *
    female: en-gb-x-gbg-local *
    female: en-gb-x-gbc-local *
    female: en-us-x-tpf-local ***
    female: en-IN-language ***

 */
// ==================== voice ended

public class ProcessHelper implements TextToSpeech.OnUtteranceCompletedListener {

    TextToSpeech textToSpeech;              // for speech service
    Context context;                        // for getting the context
    boolean checkSpeechEngineStatus;                 // for checking the speech engine status
    Set<String> a = new HashSet<String>();          // to store voice


    // constructor


    public ProcessHelper(Context context) {
        this.context = context;
    }

    public TextToSpeech getTextToSpeech(String speechVoice, String speechCode, String speechCountry) {

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int checkLanguageStatus = textToSpeech.setLanguage(Locale.US);

                    Voice voice = new Voice(speechVoice, new Locale(speechCode, speechCountry), 400, 200, false, a);
                    if (textToSpeech.setVoice(voice) == TextToSpeech.SUCCESS) {
//                        tempToast("voice seated into engine : processHelper: line 73");
                    }

                    if (checkLanguageStatus == TextToSpeech.LANG_MISSING_DATA || checkLanguageStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tempToast("Language Not supported: processHelper: line 77");
                        checkSpeechEngineStatus = false;

                    } else {
                        checkSpeechEngineStatus = true;
                        //textToSpeech.speak("Hello",TextToSpeech.QUEUE_FLUSH,null,null);
                    }
                } else {
                    tempToast("Text To Speed Engine Initialization Failed: processHelper: line 85");
                    checkSpeechEngineStatus = false;
                }
            }
        });

        return textToSpeech;
    }

    public void speakTheWords(String speechVoice, String speechCode, String speechCountry, String words, float pitch, float speed) {

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int checkLanguageStatus = textToSpeech.setLanguage(Locale.US);

                    Voice voice = new Voice(speechVoice, new Locale(speechCode, speechCountry), 400, 200, false, a);
                    if (textToSpeech.setVoice(voice) == TextToSpeech.SUCCESS) {
//                        tempToast("voice seated into engine : processHelper: line 73");
                    }

                    if (checkLanguageStatus == TextToSpeech.LANG_MISSING_DATA || checkLanguageStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tempToast("Language Not supported: processHelper: line 77");
                        checkSpeechEngineStatus = false;

                    } else {
                        checkSpeechEngineStatus = true;
                        //textToSpeech.speak("Hello",TextToSpeech.QUEUE_FLUSH,null,null);
                    }
                } else {
                    tempToast("Text To Speed Engine Initialization Failed: processHelper");
                    checkSpeechEngineStatus = false;
                }

                textToSpeech.setPitch(pitch);
                textToSpeech.setSpeechRate(speed);

//                Toast.makeText(context, "wait few seconds to initialize speech engine", Toast.LENGTH_LONG).show();

                textToSpeech.speak(words, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });


    }

    public boolean itIsSpeaking(){
        return textToSpeech.isSpeaking();
    }
    public void stopSpeaking(){
        //if(textToSpeech.isSpeaking()){
            textToSpeech.stop();


        //}
    }
    void tempToast(String m) {
        Toast.makeText(context, m, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        stopSpeaking();
    }
}

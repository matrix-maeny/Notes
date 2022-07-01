package com.matrix_maeny.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.matrix_maeny.mynotes.databinding.ActivitySettingsBinding;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/*

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

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;                // to avoid excess code (you should enable in the build.gradle(Module: app);

// Variable declarations ===========================================//

    boolean isCheckingSpeech = true;                        // flag for checking  voices (Male of Female, initializing with false
    boolean checkSpeechState = false;                      // this is for checking the initial state When the settings activity is opened
    boolean isSettingsSaved = false;                        // to check whether settings saved or not


    SeekBar voicePitch, voiceSpeed;                   // reference variables for setting pitch and speed of the voice
    ProcessHelper processHelper;                       // all process are inside it.. getting help (to check speech engine speech engine status)
    TextToSpeech textToSpeech;                      // reference variable for Text to speech engine
    float pitch = 1, speed = 1;          // for setting the pitch and speed of the speech engine
    // to store the state of the progress bar
    int speedOfProgressBar = 50;
    int pitchOfProgressBar = 50;


    MyWorkDBHelper dbHelper;            // this is for accessing settings where you've saved... (database)


    Set<String> a = new HashSet<String>();          // to store voice

    private int maleVoiceNum = 0;                   // voice num to access the maleVoice array
    private int femaleVoiceNum = 0;                 // female voice num to access the femaleVoice
    // array for voices

    private final String[][] femaleVoices = {               // female voice codes
            {"en-GB-language", "en", "GB"},//1
            {"en-us-x-tpf-local", "en", "US"},//1
            {"es-ES-language", "es", "ES"},//2
            {"es-us-x-esc-local", "es", "US"},//3
            {"en-in-x-cxx-local", "es", "US"},//4
            {"es-US-language", "in", "US"},//5
            {"en-in-x-ahp-local", "en", "IN"},//6
            {"en-gb-x-gba-local", "en", "GB"},//7
            {"en-gb-x-gbg-local", "en", "GB"},//8
            {"en-gb-x-gbc-local", "en", "GB"},//9
            {"en-us-x-sfg-local", "en", "US"},//10
            {"en-IN-language", "en", "US"}//11
    };
    String speechVoice = femaleVoices[0][0];
    String speechCode = femaleVoices[0][1];
    String speechCountry = femaleVoices[0][2];
    String maleOrFemale = "female";

    // setting default values


//============ Variable declarations complete ====================//

    @Override
    protected void onCreate(Bundle savedInstanceState) { // onCreate Method
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");


// ======================== finding id's and assigning to the variable ============================//



        voicePitch = findViewById(R.id.voicePitch);     // id for voice pitch
        voiceSpeed = findViewById(R.id.voiceSpeed);     // id for voice speed

        // initializing database for Speech settings
        dbHelper = new MyWorkDBHelper(SettingsActivity.this, "SpeechSettings.db", 1);
        processHelper = new ProcessHelper(SettingsActivity.this); // creating a helper object to get speech engine


        // =================== initializing values =================
        int n = setTheChanges();

        if (n == 0) {

            textToSpeech = processHelper.getTextToSpeech(speechVoice, speechCode, speechCountry);                 // getting speech engine
            binding.f0.setChecked(true);

            setPitchAndSpeed();
//            setVisibilityOfVoices();
            setTheEngineVoice();


        }



// ======================== finding id's and assigning to the variable completed ============================//

        // ============================= OnClick Listeners for  variables  =============================//

        voicePitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setPitchAndSpeed();
                setTheEngineVoice();
                testSpeech("Hello, My name is Matrix");
            }
        });
        voiceSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null, null);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null, null);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                setPitchAndSpeed();
                setTheEngineVoice();

                testSpeech("Hello, My name is Matrix");     // calling speak function

            }
        });


        // onclick listeners for voices(male of female)
//        voiceMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    isCheckingSpeech = true;
//                    checkSpeechState = false;
//                    Toast.makeText(SettingsActivity.this, "Selected Male", Toast.LENGTH_SHORT).show();
//                    voiceFemale.setChecked(false); // unCheck the male version
//                    setVisibilityOfVoices();    // enabling the buttons
//
//                    setTheEngineVoice();        // set the speech
//                    setTheButtonsState();
//                }
//            }
//        });
//        voiceFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    isCheckingSpeech = true;
//                    checkSpeechState = false;
//
//                    Toast.makeText(SettingsActivity.this, "Selected Female", Toast.LENGTH_SHORT).show();
//                    setVisibilityOfVoices();
//                    setTheEngineVoice();
//                    setTheButtonsState();
//
//
//
//                }
//            }
//        });


        // onclick listeners for voices(female)
        binding.f0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 0;   // setting the voice num to zero
                    setTheEngineVoice();

                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);


                }

            }
        });
        binding.f1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 1;   // setting the voice num to zero
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);


                }

            }
        });
        binding.f2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 2;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);
                }

            }
        });
        binding.f3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 3;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);
                }

            }
        });
        binding.f4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 4;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);
                }

            }
        });
        binding.f5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 5;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);
                }

            }
        });
        binding.f6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 6;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);
                }

            }
        });
        binding.f7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 7;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);
                }

            }
        });
        binding.f8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 8;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);
                }

            }
        });
        binding.f9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 9;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f10.setChecked(false);
                    binding.f11.setChecked(false);
                }

            }
        });
        binding.f10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 10;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f11.setChecked(false);
                }

            }
        });
        binding.f11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    femaleVoiceNum = 11;
                    setTheEngineVoice();

                    binding.f0.setChecked(false);
                    binding.f1.setChecked(false);
                    binding.f2.setChecked(false);
                    binding.f3.setChecked(false);
                    binding.f4.setChecked(false);
                    binding.f5.setChecked(false);
                    binding.f6.setChecked(false);
                    binding.f7.setChecked(false);
                    binding.f8.setChecked(false);
                    binding.f9.setChecked(false);
                    binding.f10.setChecked(false);
                }
            }
        });



    }//onCreate Method


    // Common toast method to reduce the code
    final void tempToast(String m) {
        Toast.makeText(SettingsActivity.this, m, Toast.LENGTH_SHORT).show();
    }

    // common method for speech
    final void testSpeech(String m) {

        if (!checkSpeechState) {        // if the engine isn't started yet..show message,, else no need (because the engine is started already..);
            tempToast("Wait few seconds to initialize speech engine");
        }
        textToSpeech.speak(m, TextToSpeech.QUEUE_FLUSH, null, null);
        checkSpeechState = true; // The speech engine is initialized... so we're setting the state to true;

    }

    // common method for setting the voice
    final void setTheEngineVoice() {
        Voice voice;

            voice = new Voice(femaleVoices[femaleVoiceNum][0], new Locale(femaleVoices[femaleVoiceNum][1], femaleVoices[femaleVoiceNum][2]), 400, 200, false, a);

            speechVoice = femaleVoices[femaleVoiceNum][0];
            speechCode = femaleVoices[femaleVoiceNum][1];
            speechCountry = femaleVoices[femaleVoiceNum][2];
            maleOrFemale = "female";



        textToSpeech.setVoice(voice);
        if (isCheckingSpeech) {
            testSpeech("Hello, My name is Matrix");

        }

    }


    // this method is for setting the speed and pitch of the speech engine
    final void setPitchAndSpeed() {
        speedOfProgressBar = voiceSpeed.getProgress();
        pitchOfProgressBar = voicePitch.getProgress(); //setting the pitch and speed for recollection

        pitch = (float) pitchOfProgressBar / 50;        // getting the values from seekbar
        speed = (float) speedOfProgressBar / 50;
        if (pitch < 0.1f) pitch = 0.1f; // in case if user sets to zero
        if (speed < 0.1f) speed = 0.1f;

        textToSpeech.setSpeechRate(speed); // setting the speed and pitch
        textToSpeech.setPitch(pitch);
    }


    @Override
    public void onBackPressed() {
        changeTheSettings();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() { // called when app closed
        textToSpeech.stop();  // stop the engine
        textToSpeech.shutdown(); // shutdown the engine
        changeTheSettings();

        dbHelper.close();
        super.onDestroy();
    }


    //    // method for inserting data settings in the database
    // once created in the database we need not to create another time
    final void changeTheSettings() {

        if (!isSettingsSaved) {
            boolean check = dbHelper.insertSpeechData("speed", speedOfProgressBar, "", "", "");
            boolean check1 = dbHelper.insertSpeechData("pitch", pitchOfProgressBar, "", "", "");
            boolean check2 = dbHelper.insertSpeechData("voice", 0, speechVoice, speechCode, speechCountry);
            if (check && check1 && check2) {
                tempToast("Settings saved");
            } else {
                check = dbHelper.updateSpeechData("speed", speedOfProgressBar, maleOrFemale, "0", String.valueOf(femaleVoiceNum));
                check1 = dbHelper.updateSpeechData("pitch", pitchOfProgressBar, "", "", "");
                check2 = dbHelper.updateSpeechData("voice", 0, speechVoice, speechCode, speechCountry);

                if (check && check1 && check2) {
                    tempToast("Settings saved");
                } else {
                    tempToast("Some error occurred.. Contact Matrix");
                }
            }
        }
        isSettingsSaved = true; // the settings are saved.. so we need not to set once again
    }


    final int setTheChanges() {
        Cursor cursor = dbHelper.getData();
        if (cursor.getCount() == 0) return 0;


        if (cursor.moveToNext()) {

            speedOfProgressBar = cursor.getInt(1);
            maleOrFemale = cursor.getString(2);
            maleVoiceNum = cursor.getInt(3);
            femaleVoiceNum = cursor.getInt(4);
        } else tempToast("Some error occurred in moveToNext: SettingsActivity: line 820");

        if (cursor.moveToNext()) {
            pitchOfProgressBar = cursor.getInt(1);
        } else {
            tempToast("Some error occurred in moveToNext: SettingsActivity: line 820");
        }

        if (cursor.moveToNext()) {
            speechVoice = cursor.getString(2);
            speechCode = cursor.getString(3);
            speechCountry = cursor.getString(4);
        } else tempToast("Some error occurred in moveToNext: SettingsActivity: line 820");


        voicePitch.setProgress(pitchOfProgressBar);
        voiceSpeed.setProgress(speedOfProgressBar);


        setTheButtonsState();

//        setVisibilityOfVoices();

        textToSpeech = processHelper.getTextToSpeech(speechVoice, speechCode, speechCountry);                 // getting speech engine
        setPitchAndSpeed();



        return 1;
    }

    final void setTheButtonsState(){

            switch (femaleVoiceNum) {
                case 1:
                    binding.f1.setChecked(true);
                    break;
                case 2:
                    binding.f2.setChecked(true);
                    break;
                case 3:
                    binding.f3.setChecked(true);
                    break;
                case 4:
                    binding.f4.setChecked(true);
                    break;
                case 5:
                    binding.f5.setChecked(true);
                    break;
                case 6:
                    binding.f6.setChecked(true);
                    break;
                case 7:
                    binding.f7.setChecked(true);
                    break;
                case 8:
                    binding.f8.setChecked(true);
                    break;
                case 9:
                    binding.f9.setChecked(true);
                    break;
                case 10:
                    binding.f10.setChecked(true);
                    break;
                case 11:
                    binding.f11.setChecked(true);
                    break;
                default:
                    binding.f0.setChecked(true);

            }

    }




}
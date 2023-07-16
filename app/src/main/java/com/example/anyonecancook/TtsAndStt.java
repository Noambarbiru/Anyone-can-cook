package com.example.anyonecancook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import ai.picovoice.porcupine.PorcupineActivationException;
import ai.picovoice.porcupine.PorcupineActivationLimitException;
import ai.picovoice.porcupine.PorcupineActivationRefusedException;
import ai.picovoice.porcupine.PorcupineActivationThrottledException;
import ai.picovoice.porcupine.PorcupineException;
import ai.picovoice.porcupine.PorcupineInvalidArgumentException;
import ai.picovoice.porcupine.PorcupineManager;
import ai.picovoice.porcupine.PorcupineManagerCallback;

import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.example.anyonecancook.databinding.EnterRecipeLinkBinding;
import com.example.anyonecancook.databinding.MakingRecipePhaseBinding;
import com.example.anyonecancook.databinding.ShowCheckListBinding;
import com.example.anyonecancook.databinding.ShowRecipeBinding;

import java.util.Set;

public class TtsAndStt {
    enum AppState {
        STOPPED,
        WAKEWORD,
        STT
    }

    enum ClassItBelongsTo {
        MAKING_RECIPE_PHASE,
        ENTER_RECIPE_LINK,
        SHOW_RECIPE,
        SHOW_CHECKLIST

    }

    TextToSpeech tts = null;
    private PorcupineManager porcupineManager = null;

    private static final String ACCESS_KEY = "${YOUR_ACCESS_KEY_HERE}";

    private final String defaultKeywordPath = "Chef-Remy_en_android_v2_1_0.ppn";
    private final String defaultKeyword = "Chef Remy";

    private TextView intentTextView;
    private ToggleButton recordButton;

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    private AppState currentState;
    private HashMap<String, String> params;
    private Context context;
    private FragmentActivity activity;
    public PorcupineManagerCallback porcupineManagerCallback;
    private ClassItBelongsTo classItBelongsTo;
    private ViewBinding binding;


    public TtsAndStt(TextView intentTextView, ToggleButton recordButton, Context context,
                     FragmentActivity activity, ClassItBelongsTo classItBelongsTo,
                     ViewBinding binding) {
        this.intentTextView = intentTextView;
        this.recordButton = recordButton;
        this.context = context;
        this.activity = activity;

        this.porcupineManagerCallback = new PorcupineManagerCallback() {
            @Override
            public void invoke(int keywordIndex) {
                ((AppCompatActivity) activity).runOnUiThread(() -> {
                    if (intentTextView != null) {
                        intentTextView.setText("");
                        intentTextView.setVisibility(View.INVISIBLE);
                    }
                    try {
                        // need to stop porcupine manager before speechRecognizer can start listening.
                        porcupineManager.stop();
                    } catch (PorcupineException e) {
                        displayError("Failed to stop Porcupine.");
                        return;
                    }
                    stopAnimation();
                    speechRecognizer.stopListening();
                    speechRecognizer.startListening(speechRecognizerIntent);
                    currentState = AppState.STT;
                });
            }
        };
        this.classItBelongsTo = classItBelongsTo;
        this.binding = binding;


    }

    public void onCreateView() {

        // on android 11, RecognitionService has to be specifically added to android manifest.
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            displayError("Speech Recognition not available.");
        }

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());

        try {
            porcupineManager = new PorcupineManager.Builder()
                    .setAccessKey(ACCESS_KEY)
                    .setKeywordPath(defaultKeywordPath)
                    .setSensitivity(0.7f)
                    .build(((AppCompatActivity) this.activity).getApplicationContext(), porcupineManagerCallback);

        } catch (PorcupineInvalidArgumentException e) {
            onPorcupineInitError(
                    String.format("%s\nEnsure your accessKey '%s' is a valid access key.", e.getMessage(), ACCESS_KEY)
            );
        } catch (PorcupineActivationException e) {
            onPorcupineInitError("AccessKey activation error");
        } catch (PorcupineActivationLimitException e) {
            onPorcupineInitError("AccessKey reached its device limit");
        } catch (PorcupineActivationRefusedException e) {
            onPorcupineInitError("AccessKey refused");
        } catch (PorcupineActivationThrottledException e) {
            onPorcupineInitError("AccessKey has been throttled");
        } catch (PorcupineException e) {
            onPorcupineInitError("Failed to initialize Porcupine " + e.getMessage());
        }

        currentState = AppState.STOPPED;
        createTTS();

    }


    public void createTTS() {
        TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR && tts != null) {
                    try {
                        Set<String> a = new HashSet<String>();
                        //number 118 in tts.getVoices()
                        Voice voice = new Voice("en-us-x-iom-local", new Locale("en_US"), 400, 200, false, a);
                        tts.setVoice(voice);
                    } catch (Exception e) {
                        tts.setVoice(tts.getDefaultVoice());
                    }
                    tts.setSpeechRate((float) 0.9);


                }
                if (tts != null) {
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {
                            Handler mainHandler = new Handler(context.getMainLooper());
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    stopService();
                                }
                            };
                            mainHandler.post(r);
                        }

                        @Override
                        public void onDone(String s) {
                            Handler mainHandler = new Handler(context.getMainLooper());
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {

                                    if (MainActivity.is_not_first_time) {
                                        playback(0);
                                    }

                                }
                            };
                            mainHandler.post(r);

                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                }
            }

        };

        tts = new TextToSpeech(context, listener, "com.google.android.tts");
        params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, classItBelongsTo.name());
    }

    public void resume() {
        playback(0);
    }

    public void ttsSpeak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, params);
        }
    }

    public void ttsSpeakFlushQueue(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);
        }
    }

    public void onDestroyView() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        onStop();
    }


    private void displayError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    private void onPorcupineInitError(final String errorMessage) {
        ((AppCompatActivity) activity).runOnUiThread(() -> {
            if (intentTextView != null) {
                TextView errorText = intentTextView;
                errorText.setText(errorMessage);
                errorText.setVisibility(View.VISIBLE);
            }
            if (recordButton != null) {
                recordButton.setChecked(false);
                recordButton.setEnabled(false);
            }
        });
    }

    public void onStop() {
        if (recordButton != null && recordButton.isChecked()) {
            recordButton.toggle();
        }
        if (speechRecognizer != null) {
            stopService();
            if (tts != null) {
                ttsSpeakFlushQueue("");
                tts.stop();
                tts.shutdown();
                tts = null;
            }
            stopAnimation();
            speechRecognizer.destroy();
        }
    }

    public boolean hasRecordPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static void requestRecordPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
//        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
    }

    private void playback(int milliSeconds) {
        if (speechRecognizer != null) {
            stopAnimation();
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(new SpeechListener());
        if (classItBelongsTo == ClassItBelongsTo.SHOW_CHECKLIST && ShowCheckList.checking) {
            speechRecognizer.startListening(speechRecognizerIntent);
            currentState = AppState.STT;
        } else {
            currentState = AppState.WAKEWORD;
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentState == AppState.WAKEWORD) {
                    porcupineManager.start();
                    if (intentTextView != null) {
                        intentTextView.setTextColor(Color.RED);
                        intentTextView.setVisibility(View.VISIBLE);
                        intentTextView.setText("Listening for " + defaultKeyword + " ...");
                    }
                }
            }
        }, milliSeconds);
    }

    private void stopService() {
        if (porcupineManager != null) {
            try {
                porcupineManager.stop();
            } catch (PorcupineException e) {
                displayError("Failed to stop porcupine.");
            }
        }
        if (intentTextView != null) {
            intentTextView.setText("");
            intentTextView.setVisibility(View.INVISIBLE);
        }
        if (speechRecognizer != null) {
            stopAnimation();
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
        }
        currentState = AppState.STOPPED;
    }

    public void onRequestPermissionsResult(@NonNull int[] grantResults) {
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            onPorcupineInitError("Microphone permission is required for this demo");
        } else {
            playback(0);
        }
    }

    public boolean process() {
        if (recordButton == null || recordButton.isChecked()) {
            if (hasRecordPermission()) {
                playback(0);
                return true;
            } else {
                requestRecordPermission(activity);
                if (recordButton != null) {
                    recordButton.setChecked(false);
                }
            }
            return false;
        } else {
            stopService();
        }
        return true;
    }

    private void stopAnimation() {
        ImageView imageView = null;
        switch (classItBelongsTo) {
            case SHOW_CHECKLIST:
                imageView = ((ShowCheckListBinding) binding).animation;
                break;
            case SHOW_RECIPE:
                imageView = ((ShowRecipeBinding) binding).animation;
                break;
            case ENTER_RECIPE_LINK:
                imageView = ((EnterRecipeLinkBinding) binding).animation;
                break;
            case MAKING_RECIPE_PHASE:
                imageView = ((MakingRecipePhaseBinding) binding).animation;
                break;
        }
        if (imageView != null) {
            AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
            imageView.setVisibility(View.INVISIBLE);
            animationDrawable.stop();
        }
    }

    private class SpeechListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            ImageView imageView = null;
            switch (classItBelongsTo) {
                case SHOW_CHECKLIST:
                    imageView = ((ShowCheckListBinding) binding).animation;
                    break;
                case SHOW_RECIPE:
                    imageView = ((ShowRecipeBinding) binding).animation;
                    break;
                case ENTER_RECIPE_LINK:
                    imageView = ((EnterRecipeLinkBinding) binding).animation;
                    break;
                case MAKING_RECIPE_PHASE:
                    imageView = ((MakingRecipePhaseBinding) binding).animation;
                    break;
            }
            if (imageView != null) {
                AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                imageView.setVisibility(View.VISIBLE);
                animationDrawable.start();
            }
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
            stopAnimation();
        }

        @Override
        public void onError(int error) {
            stopAnimation();
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    if (recordButton == null || recordButton.isChecked()) {
                        displayError("Error recording audio.");
                        process();
                    }
                    return;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    displayError("Insufficient permissions.");
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                case SpeechRecognizer.ERROR_NETWORK:
                    if (recordButton == null || recordButton.isChecked()) {
                        displayError("Network Error - Make sure you have internet connection.");
                        playback(500);
                    }
                    return;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    if (recordButton == null || recordButton.isChecked()) {
//                        displayError("No recognition result matched.");
                        displayError("Try again. Couldn't recognition what was said.");
                        playback(500);
                    }
                    return;
                case SpeechRecognizer.ERROR_CLIENT:
                    return;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    if (recordButton == null || recordButton.isChecked()) {
//                        displayError("Recognition service is busy.");
                        playback(500);
                    }
                    return;
                case SpeechRecognizer.ERROR_SERVER:
                    if (recordButton == null || recordButton.isChecked()) {
                        displayError("Server Error.");
                        playback(500);
                    }
                    return;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    if (recordButton == null || recordButton.isChecked()) {
                        displayError("No speech input.");
                        playback(500);
                    }
                    return;

                default:
                    displayError("Something wrong occurred.");
            }

            stopService();
            if (recordButton != null) {
                recordButton.toggle();
            }
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (intentTextView != null) {
                intentTextView.setVisibility(View.INVISIBLE);
            }
            if (classItBelongsTo == ClassItBelongsTo.MAKING_RECIPE_PHASE) {
                String request = data.get(0);
                String category = inputSimilarity(MakingRecipePhase.sentences, request);

                switch (category) {
                    case "next instruction":
                        if (MakingRecipePhase.started) {
                            if (MakingRecipePhase.in_bon_appetit) {
                                ((MakingRecipePhaseBinding) binding).returnToAppOpeningButton.callOnClick();
                            } else {
                                ((MakingRecipePhaseBinding) binding).NextInstructionButton.callOnClick();
                            }
                        }
                        break;
                    case "previous instruction":
                        if (MakingRecipePhase.started) {
                            if (MakingRecipePhase.instruction_index == 0 && !((MakingRecipePhaseBinding) binding).instructionTextview.getText().toString().equals("\n")) {
                                ttsSpeakFlushQueue("There is no previous instruction.");
                            } else {
                                ((MakingRecipePhaseBinding) binding).previousInstructionButton.callOnClick();
                            }
                        }
                        break;
                    case "repeat instruction":
                        if (MakingRecipePhase.started) {
                            ((MakingRecipePhaseBinding) binding).RepeatInstructionButton.callOnClick();
                        }
                        break;
                    case "I have a question":
                        if (MakingRecipePhase.started) {
                            try {
                                MakingRecipePhase.questionHandler(request, tts, params, intentTextView);
                            } catch (Exception e) {
                                ttsSpeakFlushQueue("Couldn't find an answer. Please try again in a few seconds.");
                            }
                        }
                        break;
                    case "start":
                        if (!MakingRecipePhase.started) {
                            ((MakingRecipePhaseBinding) binding).StartButton.callOnClick();
                        }
                        break;
                    case "previous page":
                        if (MainActivity.is_vision_impaired) {
                            ((MakingRecipePhaseBinding) binding).returnToChecklistButton.callOnClick();
                        }
                        break;
                    case "next page":
                        if (MainActivity.is_vision_impaired) {
                            if (((MakingRecipePhaseBinding) binding).returnToAppOpeningButton.getVisibility() == View.VISIBLE) {
                                ((MakingRecipePhaseBinding) binding).returnToAppOpeningButton.callOnClick();
                            }
                        }
                        break;
                    case "finish":
                        if (((MakingRecipePhaseBinding) binding).returnToAppOpeningButton.getVisibility() == View.VISIBLE) {
                            ((MakingRecipePhaseBinding) binding).returnToAppOpeningButton.callOnClick();
                        }
                        break;
                }
            } else if (classItBelongsTo == ClassItBelongsTo.ENTER_RECIPE_LINK) {
                String text = data.get(0);
                String category = inputSimilarity(EnterRecipeLink.sentences, text);
                switch (category) {
                    case "next page":
                        ((EnterRecipeLinkBinding) binding).enterRecipeLinkButton.callOnClick();
                        break;
                    case "previous page":
                        ((EnterRecipeLinkBinding) binding).enterRecipeToOpeningButton.callOnClick();
                        break;
                    case "":
                        ((EnterRecipeLinkBinding) binding).enterRecipeLinkInputText.setText(text);
                        ttsSpeak("I recognized: " + text + ". You can try again or move to next page.");
//                        ((EnterRecipeLinkBinding) binding).enterRecipeLinkButton.callOnClick();
                        break;
                }
            } else if (classItBelongsTo == ClassItBelongsTo.SHOW_RECIPE) {
                String request = data.get(0);
                String category = inputSimilarity(ShowRecipe.sentences, request);
                boolean flag_got_to_steps = false;
                switch (category) {
                    case "read the ingredients":
                        ttsSpeak("The ingredients are:");
                        for (String ingredient : EnterRecipeLink.ingrAndInstArray) {
                            if (ingredient.equals("I HAVE EVERYTHING!\n")) {
                                flag_got_to_steps = true;
                            } else if (ingredient.equals(EnterRecipeLink.separator)) {
                                flag_got_to_steps = false;
                                break;
                            } else if (flag_got_to_steps) {
                                ingredient = ingredient.replaceAll("\\(", ",(");
                                ingredient = ingredient.replaceAll("\\)", "),");
                                ttsSpeak(ingredient);
                                if (tts != null) {
                                    tts.playSilence(500, TextToSpeech.QUEUE_ADD, params);
                                }
                            }

                        }
                        break;
                    case "read the instructions":
                        int i = 1;
                        ttsSpeak("The instructions are:");
                        for (String step : EnterRecipeLink.ingrAndInstArray) {
                            if (step.equals(EnterRecipeLink.separator)) {
                                flag_got_to_steps = true;
                            } else if (flag_got_to_steps) {
                                ttsSpeak("instruction " + Integer.toString(i) + ": " + step);
                                if (tts != null) {
                                    tts.playSilence(500, TextToSpeech.QUEUE_ADD, params);
                                }
                                i += 1;
                            }
                        }
                        break;
                    case "next page":
                        ((ShowRecipeBinding) binding).goToCheckListButton.callOnClick();
                        break;
                    case "previous page":
                        ((ShowRecipeBinding) binding).returnToEnterLinkButton.callOnClick();
                        break;
                    case "change recipe":
                        if (((ShowRecipeBinding) binding).changeRecipeButton.getVisibility() == View.VISIBLE) {
                            ((ShowRecipeBinding) binding).changeRecipeButton.callOnClick();
                        }
                        break;
                }
            } else if (classItBelongsTo == ClassItBelongsTo.SHOW_CHECKLIST) {
                String text = data.get(0);
                String category = inputSimilarity(ShowCheckList.sentences, text);
                switch (category) {
                    case "next page":
                        if (((ShowCheckListBinding) binding).buttonShowCheckListGoToMaking.getVisibility() == View.VISIBLE) {
                            ((ShowCheckListBinding) binding).buttonShowCheckListGoToMaking.callOnClick();
                        }
                        break;
                    case "previous page":
                        ((ShowCheckListBinding) binding).buttonShowCheckListGoToShowRecipe.callOnClick();
                        break;
                    case "yes":
                        if (ShowCheckList.checking) {
                            if (!(((ShowCheckListBinding) binding).ingredientsList.isItemChecked(ShowCheckList.ingredient_index))) {
                                ((ShowCheckListBinding) binding).ingredientsList.performItemClick(null, ShowCheckList.ingredient_index, 0);
                            }
                            if (tts != null) {
                                tts.playSilence(1000, TextToSpeech.QUEUE_ADD, params);
                            }

                        }
                        break;
                    case "no":
                        if (ShowCheckList.checking) {
                            ShowCheckList.ingredient_index += 1;
                            ((ShowCheckListBinding) binding).invisible.callOnClick();
                        }
                        break;
                    case "stop checking":
                        ShowCheckList.checking = false;
                        ((ShowCheckListBinding) binding).invisible.callOnClick();
                        break;
                    case "continue checking":
                        if (tts == null) {
                            createTTS();
                        }
                        ShowCheckList.checking = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ((ShowCheckListBinding) binding).invisible.callOnClick();

                                } catch (Exception e) {
                                }
                            }
                        }, 500);
                        break;
                    case "":
                        if (tts != null) {
                            ttsSpeak("Please retry, couldn't recognize what was said");
                        }

                }

            }

            playback(500);
        }

        private String inputSimilarity(String[] options, String text) {
            {
                int min_dist = Integer.MAX_VALUE;
                String most_similar = "";
                for (String option : options) {
                    int curr = MakingRecipePhase.computeEditDistance(option, text);
                    if (curr < min_dist) {
                        if ((!option.equals("previous page") && !option.equals("next page") && !option.equals("yes") && !option.equals("no") && !option.equals("stop checking") && !option.equals("continue checking")) || curr <= 2) {
                            min_dist = curr;
                            most_similar = option;
                        }
                    }
                }
                return most_similar;
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    }


}
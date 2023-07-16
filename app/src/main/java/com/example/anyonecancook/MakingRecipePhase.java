package com.example.anyonecancook;


import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;

import android.widget.TextView;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.anyonecancook.databinding.MakingRecipePhaseBinding;


public class MakingRecipePhase extends Fragment {
    public static ArrayList<String> instructionsArray;
    private MakingRecipePhaseBinding binding;
    public static int instruction_index = 0;
    public static boolean in_bon_appetit = false;

    private static ChatGPT chatGPT;

    public static String[] sentences = {"next instruction", "previous instruction", "repeat instruction", "I have a question", "finish", "start", "next page", "previous page"};
    public static boolean started = false;
    private TtsAndStt ttsAndStt;
    private static final int[] fontSizeMSmallDevice = {42};
    private static final int[] fontSizeXlLargeDevice = {100};

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        editTextFontSize();
        binding.animation.setVisibility(View.INVISIBLE);
        if (MainActivity.POPUP_WINDOW_SCORE == null || !MainActivity.POPUP_WINDOW_SCORE.isShowing()) {
            if (ttsAndStt.tts == null) {
                ttsAndStt.createTTS();
            }
            if (started) {
                binding.instructionTextview.setText(instructionsArray.get(instruction_index));
                if (in_bon_appetit) {
                    int orientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                    if (MainActivity.large_device && (orientation == Surface.ROTATION_90
                            || orientation == Surface.ROTATION_270)) {
                        binding.instructionTextview.setText("\n");
                        binding.instructionTextview.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    } else {
                        binding.instructionTextview.setText("\n");
                        binding.instructionTextview.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                    }
                    binding.bonAppetitImage.setVisibility(View.VISIBLE);
                    binding.roundCardView.setVisibility(View.VISIBLE);
                    binding.returnToAppOpeningButton.setVisibility(View.VISIBLE);
                }
                binding.StartButton.setVisibility(View.INVISIBLE);
                if (!in_bon_appetit) {
                    binding.RepeatInstructionButton.setVisibility(View.VISIBLE);
                    binding.NextInstructionButton.setVisibility(View.VISIBLE);
                    binding.instructionTextview.setGravity(Gravity.CENTER);
                    binding.bonAppetitImage.setVisibility(View.INVISIBLE);
                    binding.roundCardView.setVisibility(View.INVISIBLE);
                    binding.returnToAppOpeningButton.setVisibility(View.INVISIBLE);
                }
                if (instruction_index > 0 || binding.instructionTextview.getText().toString().equals("\n")) {
                    binding.previousInstructionButton.setVisibility(View.VISIBLE);
                }
            }
            if (binding.StartButton.getVisibility() == View.INVISIBLE) {
                ttsAndStt.resume();
            }
            if (binding.StartButton.getVisibility() == View.VISIBLE) {

                if (MainActivity.is_vision_impaired) {
                    if (ttsAndStt.process()) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (ttsAndStt.tts != null) {
                                        ttsAndStt.ttsSpeakFlushQueue("When you're ready, call me and say start");
                                    }

                                } catch (Exception e) {
                                }
                            }
                        }, 500);
                    }
                }
            }
        } else {
            onStop();
        }
    }

    public static void questionHandler(String request, TextToSpeech tts,
                                       HashMap<String, String> params,
                                       TextView intentTextView) throws Exception {
        int index = Math.min(instruction_index, instructionsArray.size());
        request = "I am in step " + Integer.toString(index + 1) + ". " + request;
        String answer = chatGPT.answer(request);
        tts.speak(answer, TextToSpeech.QUEUE_FLUSH, params);
//        intentTextView.setText("answer" + " - " + answer);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
//        super.onCreateView(inflater, container, savedInstanceState);

        binding = MakingRecipePhaseBinding.inflate(inflater, container, false);
        chatGPT = new ChatGPT();
        ttsAndStt = new TtsAndStt(null, null, getContext(),
                getActivity(), TtsAndStt.ClassItBelongsTo.MAKING_RECIPE_PHASE, binding);
        if (MainActivity.large_device) {
            binding.returnToAppOpeningButton.setTextSize(60);
            binding.returnToChecklistButton.getLayoutParams().width = 400;
            binding.returnToChecklistButton.requestLayout();
            binding.returnToChecklistButton.setTextSize(40);
            binding.returnToChecklistButton.getLayoutParams().width = 500;
            binding.returnToChecklistButton.requestLayout();
            binding.previousInstructionButton.setTextSize(70);
            binding.NextInstructionButton.setTextSize(70);
            binding.RepeatInstructionButton.setTextSize(70);
            binding.StartButton.setTextSize(70);
            binding.bonAppetitImage.getLayoutParams().height = 800;
            binding.bonAppetitImage.getLayoutParams().width = 800;
            binding.bonAppetitImage.requestLayout();

            binding.roundCardView.getLayoutParams().height = 650;
            binding.roundCardView.getLayoutParams().width = 650;
            binding.roundCardView.setRadius(550);
            binding.roundCardView.requestLayout();

            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) binding.instructionTextview.getLayoutParams();
            p.setMargins(15, 20, 15, 5);
            binding.instructionTextview.setLayoutParams(p);

            binding.animation.getLayoutParams().height = 260;
            binding.animation.getLayoutParams().width = 260;
            FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) binding.animation.getLayoutParams();
//            param.setMargins(300, 0, 0, 0);
            param.setMarginStart(300);
            binding.animation.setLayoutParams(param);
            binding.animation.requestLayout();
            binding.RemyMenuImage.getLayoutParams().height = 330;
            binding.RemyMenuImage.getLayoutParams().width = 330;
            binding.RemyMenuImage.requestLayout();

            binding.RemyMenuRoundCardView.getLayoutParams().height = 260;
            binding.RemyMenuRoundCardView.getLayoutParams().width = 260;
            binding.RemyMenuRoundCardView.setRadius(130);
            binding.RemyMenuRoundCardView.requestLayout();

            int orientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            if (orientation == Surface.ROTATION_90
                    || orientation == Surface.ROTATION_270) {
                binding.animation.setImageResource(R.drawable.listening_animation);
                binding.animation.getLayoutParams().height = 300;
                binding.animation.getLayoutParams().width = 500;
                param = (FrameLayout.LayoutParams) binding.animation.getLayoutParams();
//            param.setMargins(300, 0, 0, 0);
                param.setMarginStart(600);
                binding.animation.setLayoutParams(param);
                binding.animation.requestLayout();

                binding.bonAppetitImage.getLayoutParams().height = 600;
                binding.bonAppetitImage.getLayoutParams().width = 600;
                binding.bonAppetitImage.requestLayout();

                binding.roundCardView.getLayoutParams().height = 500;
                binding.roundCardView.getLayoutParams().width = 500;
                binding.roundCardView.setRadius(300);
                binding.roundCardView.requestLayout();
            }
        }
        editTextFontSize();

        ArrayList<String> ingrAndInstArray = EnterRecipeLink.ingrAndInstArray;
        int index_middle = ingrAndInstArray.indexOf("$?");
        instructionsArray = new ArrayList<String>();
        for (int i = index_middle + 1; i < ingrAndInstArray.size(); i++) {
            instructionsArray.add(ingrAndInstArray.get(i));
        }
        // for initialization of tts
        binding.previousInstructionButton.setVisibility(View.INVISIBLE);
        binding.RepeatInstructionButton.setVisibility(View.INVISIBLE);
        binding.NextInstructionButton.setVisibility(View.INVISIBLE);
        binding.returnToAppOpeningButton.setVisibility(View.INVISIBLE);
        binding.bonAppetitImage.setVisibility(View.INVISIBLE);
        binding.roundCardView.setVisibility(View.INVISIBLE);
        ttsAndStt.onCreateView();
        return binding.getRoot();

    }

    private void editTextFontSize() {

        int addToFontSize = 0;
        if (MainActivity.large_device) {
            switch (MainActivity.fontSize) {
                case S:
                    addToFontSize = -60;
                    break;
                case M:
                    addToFontSize = -40;
                    break;
                case L:
                    addToFontSize = -20;
                    break;
                case XL:
                    addToFontSize = 0;
                    break;
            }
            binding.instructionTextview.setTextSize(fontSizeXlLargeDevice[0] + addToFontSize);

        } else {
            switch (MainActivity.fontSize) {
                case S:
                    addToFontSize = -7;
                    break;
                case M:
                    addToFontSize = 0;
                    break;
                case L:
                    addToFontSize = 7;
                    break;
                case XL:
                    addToFontSize = 14;
                    break;
            }
            binding.instructionTextview.setTextSize(fontSizeMSmallDevice[0] + addToFontSize);

        }
    }

    public static int computeEditDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        binding.instructionTextview.setMovementMethod(new ScrollingMovementMethod());
        binding.returnToChecklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCheckList.ingredient_index = -1;
                NavHostFragment.findNavController(MakingRecipePhase.this)
                        .navigate(R.id.action_MakingRecipePhase_to_ShowCheckList);
            }
        });
        binding.returnToAppOpeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MakingRecipePhase.this)
                        .navigate(R.id.action_MakingRecipePhase_to_AppOpening);
            }
        });
        binding.StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ttsAndStt.process()) {
                    String instruction = instructionsArray.get(instruction_index);
                    binding.instructionTextview.setText(instruction);
                    binding.StartButton.setVisibility(View.INVISIBLE);
                    binding.RepeatInstructionButton.setVisibility(View.VISIBLE);
                    binding.NextInstructionButton.setVisibility(View.VISIBLE);
                    started = true;

                    ttsAndStt.ttsSpeakFlushQueue(instruction);
                }
            }
        });
        binding.NextInstructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instruction_index == instructionsArray.size() - 1) {
                    int orientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                    if (MainActivity.large_device && (orientation == Surface.ROTATION_90
                            || orientation == Surface.ROTATION_270)) {
                        binding.instructionTextview.setText("\n");
                        binding.instructionTextview.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    } else {
                        binding.instructionTextview.setText("\n");
                        binding.instructionTextview.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                    }
                    in_bon_appetit = true;
                    binding.bonAppetitImage.setVisibility(View.VISIBLE);
                    binding.roundCardView.setVisibility(View.VISIBLE);
                    binding.NextInstructionButton.setVisibility(View.INVISIBLE);
                    binding.RepeatInstructionButton.setVisibility(View.INVISIBLE);
                    binding.returnToAppOpeningButton.setVisibility(View.VISIBLE);
//                    binding.returnToChecklistButton.setVisibility(View.INVISIBLE);
                    ttsAndStt.ttsSpeakFlushQueue("Bon Appetit!");

                } else {
                    instruction_index = Math.min(instruction_index + 1, instructionsArray.size() - 1);
                    binding.instructionTextview.setText(instructionsArray.get(instruction_index));
                    ttsAndStt.ttsSpeakFlushQueue(instructionsArray.get(instruction_index));
                }

                if (instruction_index > 0 || binding.instructionTextview.getText().toString().equals("\n")) {
                    binding.previousInstructionButton.setVisibility(View.VISIBLE);
                }

            }
        });
        binding.previousInstructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.instructionTextview.getText().toString().equals("\n")) {
                    binding.instructionTextview.setText(instructionsArray.get(instruction_index));
                    in_bon_appetit = false;
                    binding.instructionTextview.setGravity(Gravity.CENTER);
                    binding.bonAppetitImage.setVisibility(View.INVISIBLE);
                    binding.roundCardView.setVisibility(View.INVISIBLE);
                    binding.NextInstructionButton.setVisibility(View.VISIBLE);
                    binding.RepeatInstructionButton.setVisibility(View.VISIBLE);
                    binding.returnToAppOpeningButton.setVisibility(View.INVISIBLE);
//                    binding.returnToChecklistButton.setVisibility(View.VISIBLE);
                } else {
                    instruction_index = Math.max(instruction_index - 1, 0);
                    binding.instructionTextview.setText(instructionsArray.get(instruction_index));
                }
                ttsAndStt.ttsSpeakFlushQueue(instructionsArray.get(instruction_index));
                if (instruction_index == 0 && !binding.instructionTextview.getText().toString().equals("\n")) {
                    binding.previousInstructionButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.RepeatInstructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.instructionTextview.getText().toString().equals("\n")) {
                    ttsAndStt.ttsSpeakFlushQueue("Bon Appetit!");
                } else {
                    ttsAndStt.ttsSpeakFlushQueue(binding.instructionTextview.getText().toString());
                }
            }
        });
        binding.RemyMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.ShowMenuPopup(getContext(), MakingRecipePhase.this, MainActivity.ClassItBelongsTo.MAKING_RECIPE_PHASE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        ttsAndStt.onDestroyView();
        onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        ttsAndStt.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[]
                    permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ttsAndStt.onRequestPermissionsResult(grantResults);
    }


}







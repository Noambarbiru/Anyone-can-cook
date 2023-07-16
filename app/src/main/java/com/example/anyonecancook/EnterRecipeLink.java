package com.example.anyonecancook;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.example.anyonecancook.databinding.EnterRecipeLinkBinding;

import java.util.ArrayList;

import java.util.Locale;

public class EnterRecipeLink extends Fragment {

    public static String recipe_url;
    private EnterRecipeLinkBinding binding;
    public static ArrayList<String> recipeArray = new ArrayList<String>();
    public static ArrayList<String> ingrAndInstArray = new ArrayList<String>();
    public static String recipeTitle;
    public static String recipeText = "";
    private TtsAndStt ttsAndStt;
    public static final String separator = "$?";
    private static final String msg_no_input = "no input was given";
    private static final String msg_no_valid = "The Link is not a valid url";
    private static final String msg_no_in_allrecipes = "The Link is not for a recipe in allrecipes.com";
    private static final String msg_could_not_find = "couldn't find a recipe for that recipe name in allrecipes.com";
    private final String ttsOrder = "Call me and then say a recipe name";
    private final String welcomeMsg = "Hello, I'm excited to cook with you today. lets start by choosing a recipe.";
    private final String aboutMenuMsg = "Welcome! I'm Chef Remy, the friendly mouse in the upper left corner. " +
            "I'm thrilled that you've chosen to cook with me. " +
            "Just click on my picture to explore the menu, where you can access settings and learn about the voice commands relevant to the page from which you enter the menu. For someone who is not visually impaired, voice commands will only appear on the recipe preparation page." +
            " Wishing you good luck and happy cooking!\n";
    public static String[] sentences = {"next page", "previous page"};
    public static boolean came_from_app_opening = true;
    private LayoutInflater layoutInflater;
    private View layout;
    public static RecipeCrawler recipeCrawler;
    private static final int[] fontSizeMSmallDevice = {45, 35};
    private static final int[] fontSizeXlLargeDevice = {95, 65};

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = EnterRecipeLinkBinding.inflate(inflater, container, false);

        ttsAndStt = new TtsAndStt(null, null, getContext(),
                getActivity(), TtsAndStt.ClassItBelongsTo.ENTER_RECIPE_LINK, binding);
        ttsAndStt.onCreateView();
        binding.errorMessage.setVisibility(View.INVISIBLE);
        editTextFontSize();
        if (MainActivity.large_device) {
            binding.enterRecipeLinkButton.setTextSize(60);
            binding.enterRecipeToOpeningButton.setTextSize(60);

            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) binding.enterRecipeLinkTextview.getLayoutParams();
            p.setMargins(10, 340, 10, 50);
            if (MainActivity.fontSize == MainActivity.FontSize.M || MainActivity.fontSize == MainActivity.FontSize.S) {
                p.setMargins(10, 340, 10, 100);
            }
            binding.enterRecipeLinkTextview.setLayoutParams(p);
            int orientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            if (orientation == Surface.ROTATION_90
                    || orientation == Surface.ROTATION_270) {
                p.setMargins(10, 240, 10, 10);
                binding.enterRecipeLinkTextview.setLayoutParams(p);
                LinearLayout.LayoutParams parm = (LinearLayout.LayoutParams) binding.enterRecipeLinkInputText.getLayoutParams();
                parm.setMargins(160, 20, 160, 20);
                parm.setMarginStart(160);
                parm.setMarginEnd(160);
                binding.enterRecipeLinkInputText.setLayoutParams(parm);
            } else {
                LinearLayout.LayoutParams parm = (LinearLayout.LayoutParams) binding.errorMessage.getLayoutParams();
                parm.setMargins(20, 0, 20, 200);
                parm.setMarginStart(20);
                parm.setMarginEnd(20);
                binding.errorMessage.setLayoutParams(parm);
            }
            binding.animation.getLayoutParams().height = 210;
            binding.animation.getLayoutParams().width = 440;
            binding.animation.requestLayout();

            binding.RemyMenuImage.getLayoutParams().height = 330;
            binding.RemyMenuImage.getLayoutParams().width = 330;
            binding.RemyMenuImage.requestLayout();

            binding.RemyMenuRoundCardView.getLayoutParams().height = 260;
            binding.RemyMenuRoundCardView.getLayoutParams().width = 260;
            binding.RemyMenuRoundCardView.setRadius(130);
            binding.RemyMenuRoundCardView.requestLayout();


        } else {

            if (MainActivity.fontSize == MainActivity.FontSize.M || MainActivity.fontSize == MainActivity.FontSize.S) {
                LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) binding.enterRecipeLinkTextview.getLayoutParams();
                p.setMargins(10, 500, 10, 10);
                binding.enterRecipeLinkTextview.setLayoutParams(p);

            } else {
                LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) binding.enterRecipeLinkTextview.getLayoutParams();
                p.setMargins(10, 300, 10, 10);
                binding.enterRecipeLinkTextview.setLayoutParams(p);
            }
            if (MainActivity.fontSize == MainActivity.FontSize.M || MainActivity.fontSize == MainActivity.FontSize.S) {
                LinearLayout.LayoutParams parm = (LinearLayout.LayoutParams) binding.enterRecipeLinkInputText.getLayoutParams();
                parm.setMargins(20, 200, 20, 170);
                parm.setMarginStart(20);
                parm.setMarginEnd(20);
                binding.enterRecipeLinkInputText.setLayoutParams(parm);
            } else {
                LinearLayout.LayoutParams parm = (LinearLayout.LayoutParams) binding.enterRecipeLinkInputText.getLayoutParams();
                parm.setMargins(20, 40, 20, 145);
                parm.setMarginStart(20);
                parm.setMarginEnd(20);
                binding.enterRecipeLinkInputText.setLayoutParams(parm);
            }
            LinearLayout.LayoutParams parm = (LinearLayout.LayoutParams) binding.errorMessage.getLayoutParams();
            parm.setMargins(20, 0, 20, 200);
            parm.setMarginStart(20);
            parm.setMarginEnd(20);
            binding.errorMessage.setLayoutParams(parm);


        }

        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = layoutInflater.inflate(R.layout.layout_popup, null);


        return binding.getRoot();

    }


    private void editTextFontSize() {

        int addToFontSize = 0;
        if (MainActivity.large_device) {
            switch (MainActivity.fontSize) {
                case S:
                    addToFontSize = -35;
                    break;
                case M:
                    addToFontSize = -25;
                    break;
                case L:
                    addToFontSize = -15;
                    break;
                case XL:
                    addToFontSize = 0;
                    break;
            }
            binding.enterRecipeLinkTextview.setTextSize(fontSizeXlLargeDevice[0] + addToFontSize);
            binding.enterRecipeLinkInputText.setTextSize(fontSizeXlLargeDevice[1] + addToFontSize);
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) binding.enterRecipeLinkTextview.getLayoutParams();
            p.setMargins(10, 340, 10, 100);
            if (MainActivity.fontSize != MainActivity.FontSize.XL) {
                p.setMargins(10, 340, 10, 350);
            }
            binding.enterRecipeLinkTextview.setLayoutParams(p);
            int orientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            if (orientation == Surface.ROTATION_90
                    || orientation == Surface.ROTATION_270) {
                p.setMargins(10, 230, 10, 10);
                if (MainActivity.fontSize != MainActivity.FontSize.XL) {
                    p.setMargins(10, 230, 10, 90);
                }
                if (MainActivity.fontSize == MainActivity.FontSize.L) {
                    p.setMargins(10, 230, 10, 60);
                }
                if (MainActivity.fontSize == MainActivity.FontSize.S) {
                    p.setMargins(10, 230, 10, 120);
                }
                binding.enterRecipeLinkTextview.setLayoutParams(p);
            } else {
                LinearLayout.LayoutParams parm = (LinearLayout.LayoutParams) binding.errorMessage.getLayoutParams();
                parm.setMargins(20, 0, 20, 200);
                parm.setMarginStart(20);
                parm.setMarginEnd(20);
                binding.errorMessage.setLayoutParams(parm);
            }
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
            binding.enterRecipeLinkTextview.setTextSize(fontSizeMSmallDevice[0] + addToFontSize);
            binding.enterRecipeLinkInputText.setTextSize(fontSizeMSmallDevice[1] + addToFontSize);
            if (MainActivity.fontSize == MainActivity.FontSize.M || MainActivity.fontSize == MainActivity.FontSize.S) {
                LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) binding.enterRecipeLinkTextview.getLayoutParams();
                p.setMargins(10, 500, 10, 10);
                binding.enterRecipeLinkTextview.setLayoutParams(p);

            } else {
                LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) binding.enterRecipeLinkTextview.getLayoutParams();
                p.setMargins(10, 300, 10, 10);
                binding.enterRecipeLinkTextview.setLayoutParams(p);
            }
            if (MainActivity.fontSize == MainActivity.FontSize.M || MainActivity.fontSize == MainActivity.FontSize.S) {
                LinearLayout.LayoutParams parm = (LinearLayout.LayoutParams) binding.enterRecipeLinkInputText.getLayoutParams();
                parm.setMargins(20, 200, 20, 170);
                parm.setMarginStart(20);
                parm.setMarginEnd(20);
                binding.enterRecipeLinkInputText.setLayoutParams(parm);
            } else {
                LinearLayout.LayoutParams parm = (LinearLayout.LayoutParams) binding.enterRecipeLinkInputText.getLayoutParams();
                parm.setMargins(20, 40, 20, 145);
                parm.setMarginStart(20);
                parm.setMarginEnd(20);
                binding.enterRecipeLinkInputText.setLayoutParams(parm);
            }
            LinearLayout.LayoutParams parm = (LinearLayout.LayoutParams) binding.errorMessage.getLayoutParams();
            parm.setMargins(20, 0, 20, 200);
            parm.setMarginStart(20);
            parm.setMarginEnd(20);
            binding.errorMessage.setLayoutParams(parm);

        }
    }

    public static boolean changeRecipe(String url) {
        try {
            Crawler crawler = new Crawler(url);
            recipeArray.clear();
            ingrAndInstArray.clear();
            recipeText = "";
            ingrAndInstArray.add("I HAVE EVERYTHING!\n");

            recipeText = recipeText + "Ingredients:\n";

            int ingr_number = 1;
            for (String ingredient : crawler.ingredients) {
                ingrAndInstArray.add(ingredient);
                recipeText = recipeText + (ingredient) + "\n";
            }

            recipeArray.add("Ingredients:");
            for (String ingredient : crawler.ingredients) {
                recipeArray.add("\u2022 ".concat(ingredient));
            }

            recipeText = recipeText + "\nInstructions:\n";

            ingr_number = 1;
            recipeArray.add("");
            recipeArray.add("Instructions:");
            ingrAndInstArray.add(separator);
            for (String instruction : crawler.instructions) {
                String step = Integer.toString(ingr_number).concat(")  ").concat(instruction);
                recipeArray.add(step);
                ingrAndInstArray.add(instruction);
                recipeText = recipeText + "(" + Integer.toString(ingr_number) + ") " + instruction + "\n";
                ingr_number++;
            }
            recipeTitle = crawler.title;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkValidationOfUrl(String url) {
        {
            recipeCrawler = null;
            try {
                if (url.trim().equals("")) {
                    binding.errorMessage.setText(msg_no_input);
                    return false;
                } else if (!url.contains("/")) {
                    recipeCrawler = new RecipeCrawler(url);
                    url = recipeCrawler.get_url();
                    if (url.equals("")) {
                        binding.errorMessage.setText(msg_could_not_find);
                        recipeCrawler = null;
                        return false;
                    }
                }
                if (!url.toLowerCase(Locale.ROOT).contains("allrecipes.com/recipe/")) {
                    binding.errorMessage.setText(msg_no_in_allrecipes);
                    return false;
                }
                return changeRecipe(url);
            }

            // If there was an Exception
            // while creating URL object
            catch (Exception e) {
                binding.errorMessage.setText(msg_no_valid);
                return false;
            }
        }
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        binding.enterRecipeLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipe_url = binding.enterRecipeLinkInputText.getText().toString();
                ttsAndStt.onStop();
                ttsAndStt.createTTS();
                ttsAndStt.resume();
                boolean legal_url = checkValidationOfUrl(recipe_url);
                if (legal_url) {

                    binding.errorMessage.setVisibility(View.INVISIBLE);
                    NavHostFragment.findNavController(EnterRecipeLink.this)
                            .navigate(R.id.action_EnterRecipeLink_to_ShowRecipe);
                } else {

                    if (MainActivity.is_vision_impaired && ttsAndStt.hasRecordPermission()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (ttsAndStt.tts != null) {
                                        ttsAndStt.ttsSpeak(binding.errorMessage.getText().toString());
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }, 500);
                    }
                    binding.errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.enterRecipeToOpeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(EnterRecipeLink.this)
                        .navigate(R.id.action_EnterRecipeLink_to_AppOpening);
            }
        });
        binding.RemyMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.ShowMenuPopup(getContext(), EnterRecipeLink.this, MainActivity.ClassItBelongsTo.ENTER_RECIPE_LINK);
            }
        });
        if (MainActivity.got_url_from_browser) {
            binding.enterRecipeLinkInputText.setText(MainActivity.url);
            MainActivity.got_url_from_browser = false;
            MainActivity.url = "";
//            binding.enterRecipeLinkButton.callOnClick();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ttsAndStt.onDestroyView();
        binding = null;
        onStop();
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

    @Override
    public void onResume() {
        super.onResume();
        if (ttsAndStt.tts == null) {
            ttsAndStt.createTTS();
        }
        editTextFontSize();

        binding.animation.setVisibility(View.INVISIBLE);

        if (MainActivity.got_url_from_browser) {
            binding.enterRecipeLinkInputText.setText(MainActivity.url);
            MainActivity.got_url_from_browser = false;
//            binding.enterRecipeLinkButton.callOnClick();
        }
        if (MainActivity.POPUP_WINDOW_SCORE == null || !MainActivity.POPUP_WINDOW_SCORE.isShowing()) {
            if (!MainActivity.is_not_first_time) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (ttsAndStt.tts != null) {
                                ttsAndStt.ttsSpeak(aboutMenuMsg);
                                SharedPreferences.Editor editor = MainActivity.settings.edit();
                                editor.putBoolean(MainActivity.prefFirstTimeKey, true);
                                editor.commit();
                                MainActivity.is_not_first_time = true;
                            }

                        } catch (Exception e) {
                        }
                    }
                }, 1000);
            } else {

                if (MainActivity.is_vision_impaired && ttsAndStt.process()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (ttsAndStt.tts != null && binding.enterRecipeLinkInputText.getText().length() == 0) {
                                    if (came_from_app_opening) {

                                        ttsAndStt.ttsSpeak(welcomeMsg);

                                    }
                                    ttsAndStt.ttsSpeak(ttsOrder);
                                }
                            } catch (Exception e) {
                            }
                        }
                    }, 1000);

                }
            }
        } else {
            onStop();
        }
    }


}



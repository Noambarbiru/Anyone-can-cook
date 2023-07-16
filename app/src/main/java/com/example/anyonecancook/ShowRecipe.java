package com.example.anyonecancook;


import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.anyonecancook.databinding.ShowRecipeBinding;

import java.util.ArrayList;


public class ShowRecipe extends Fragment {

    private ShowRecipeBinding binding;
    private ListView recipeList;
    private CustomAdapter adapter;
    private ArrayList<String> recipeArray = EnterRecipeLink.recipeArray;
    private String recipeTitle;
    private TtsAndStt ttsAndStt;
    private static final String ttsOrder = "Call me and then tell me what to read, the ingredients or the instructions.";
    public static String[] sentences = {"read the ingredients", "read the instructions", "next page", "previous page", "change recipe"};
    private static final int[] fontSizeMSmallDevice = {35};
    private static final int[] fontSizeXlLargeDevice = {70};

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = ShowRecipeBinding.inflate(inflater, container, false);
        ttsAndStt = new TtsAndStt(null, null, getContext(),
                getActivity(), TtsAndStt.ClassItBelongsTo.SHOW_RECIPE, binding);
        ttsAndStt.onCreateView();
        recipeList = binding.recipeList;
        updateRecipeViewed();
        if (EnterRecipeLink.recipeCrawler == null) {
            binding.changeRecipeButton.setVisibility(View.INVISIBLE);
        }
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.goToCheckListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCheckList.ingredient_index = -1;
                try {
                    NavHostFragment.findNavController(ShowRecipe.this)
                            .navigate(R.id.action_ShowRecipe_to_ShowCheckList);
                } catch (Exception e) {
                    System.out.println();
                }
            }
        });
        binding.returnToEnterLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnterRecipeLink.came_from_app_opening = false;
                NavHostFragment.findNavController(ShowRecipe.this)
                        .navigate(R.id.action_ShowRecipe_to_EnterRecipeLink);
            }
        });
        binding.changeRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ttsAndStt.onStop();
                ttsAndStt.createTTS();
                ttsAndStt.resume();
                EnterRecipeLink.changeRecipe(EnterRecipeLink.recipeCrawler.get_url());
                updateRecipeViewed();
            }
        });
        binding.RemyMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.ShowMenuPopup(getContext(), ShowRecipe.this, MainActivity.ClassItBelongsTo.SHOW_RECIPE);
            }
        });
    }

    private void editTextFontSize() {
        int addToFontSize = 0;
        if (MainActivity.large_device) {
            switch (MainActivity.fontSize) {
                case S:
                    adapter = new CustomAdapter(getActivity(), recipeArray, R.layout.list_view_layout_large_device_s, true);
                    addToFontSize = -35;
                    break;
                case M:
                    adapter = new CustomAdapter(getActivity(), recipeArray, R.layout.list_view_layout_large_device_m, true);
                    addToFontSize = -25;
                    break;
                case L:
                    adapter = new CustomAdapter(getActivity(), recipeArray, R.layout.list_view_layout_large_device_l, true);
                    addToFontSize = -15;
                    break;
                case XL:
                    adapter = new CustomAdapter(getActivity(), recipeArray, R.layout.list_view_layout_large_device_xl, true);
                    addToFontSize = 0;
                    break;
            }

            binding.showRecipeTextviewRecipeTitle.setTextSize(fontSizeXlLargeDevice[0] + addToFontSize);


        } else {
            switch (MainActivity.fontSize) {
                case S:
                    adapter = new CustomAdapter(getActivity(), recipeArray, R.layout.list_view_layout_s, true);
                    addToFontSize = -7;
                    break;
                case M:
                    adapter = new CustomAdapter(getActivity(), recipeArray, R.layout.list_view_layout_m, true);
                    addToFontSize = 0;
                    break;
                case L:
                    adapter = new CustomAdapter(getActivity(), recipeArray, R.layout.list_view_layout_l, true);
                    addToFontSize = 7;
                    break;
                case XL:
                    adapter = new CustomAdapter(getActivity(), recipeArray, R.layout.list_view_layout_xl, true);
                    addToFontSize = 14;
                    break;
            }
            binding.showRecipeTextviewRecipeTitle.setTextSize(fontSizeMSmallDevice[0] + addToFontSize);
        }
        recipeList.setAdapter(adapter);
        recipeList.setDivider(null);
    }

    private void updateRecipeViewed() {
        recipeTitle = EnterRecipeLink.recipeTitle;
        binding.showRecipeTextviewRecipeTitle.setText(recipeTitle);
        recipeArray = EnterRecipeLink.recipeArray;
        String recipeTitle = EnterRecipeLink.recipeTitle;
        editTextFontSize();
        if (MainActivity.large_device) {
            binding.returnToEnterLinkButton.setTextSize(60);
            binding.goToCheckListButton.setTextSize(60);

            binding.changeRecipeButton.setCustomSize(200);
            binding.changeRecipeButton.setMaxImageSize(180);
            binding.changeRecipeButton.requestLayout();

            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) binding.showRecipeTextviewRecipeTitle.getLayoutParams();
            p.setMargins(10, 275, 10, 0);
            binding.showRecipeTextviewRecipeTitle.setLayoutParams(p);

            binding.animation.getLayoutParams().height = 300;
            binding.animation.getLayoutParams().width = 500;
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
                LinearLayout.LayoutParams pram = (LinearLayout.LayoutParams) binding.showRecipeTextviewRecipeTitle.getLayoutParams();
                pram.setMargins(330, 60, 330, 0);
                pram.setMarginStart(330);
                pram.setMarginEnd(330);
                binding.showRecipeTextviewRecipeTitle.setLayoutParams(pram);
                pram = (LinearLayout.LayoutParams) binding.recipeList.getLayoutParams();
                pram.setMargins(260, 20, 260, 10);
                pram.setMarginStart(260);
                pram.setMarginEnd(260);
                binding.recipeList.setLayoutParams(pram);
//                binding.animation.setImageResource(R.drawable.listening_only_mic_animation);
                binding.animation.getLayoutParams().height = 220;
//                binding.animation.getLayoutParams().width = 200;
                binding.animation.getLayoutParams().width = 350;
                FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) binding.animation.getLayoutParams();
                param.setMargins(0, 0, 0, -20);
                param.setMarginStart(0);
                param.setMarginEnd(0);
                param.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                binding.animation.setLayoutParams(param);
                binding.animation.requestLayout();
            }
        }
        recipeList.setAdapter(adapter);
        recipeList.setDivider(null);
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

        if (MainActivity.POPUP_WINDOW_SCORE == null || !MainActivity.POPUP_WINDOW_SCORE.isShowing()) {
            if (MainActivity.is_vision_impaired) {
                if (ttsAndStt.hasRecordPermission()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (ttsAndStt.tts != null) {
                                    ttsAndStt.resume();
                                    ttsAndStt.ttsSpeak(ttsOrder);
                                }
                            } catch (Exception e) {
                            }
                        }
                    }, 1500);
                } else if (ttsAndStt.process()) {
                    ttsAndStt.ttsSpeak(ttsOrder);
                }
            }
        } else {
            onStop();
        }
    }


}
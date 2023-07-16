package com.example.anyonecancook;


import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.anyonecancook.databinding.*;

import java.util.ArrayList;


public class ShowCheckList extends Fragment {
    private ArrayList<String> ingredientsArray;
    private ShowCheckListBinding binding;
    private ListView ingredientsList;
    private CustomAdapter adapter;
    private boolean checkAllWasChecked;
    private TtsAndStt ttsAndStt;
    public static String[] sentences = {"yes", "no", "next page", "previous page", "stop checking", "continue checking"};
    public static boolean checking = false;
    public static int ingredient_index = -1;
    private static final int[] fontSizeMSmallDevice = {35};
    private static final int[] fontSizeXlLargeDevice = {70};


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        ingredientsArray = new ArrayList<String>();
        checkAllWasChecked = false;
        binding = ShowCheckListBinding.inflate(inflater, container, false);
        // handle your code here.
        ttsAndStt = new TtsAndStt(null, null, getContext(),
                getActivity(), TtsAndStt.ClassItBelongsTo.SHOW_CHECKLIST, binding);
        ttsAndStt.onCreateView();
        ArrayList<String> ingrAndInstArray = EnterRecipeLink.ingrAndInstArray;
        int index_middle = ingrAndInstArray.indexOf("$?");
        ingredientsArray = new ArrayList<String>();
        for (int i = 0; i < index_middle; i++) {
            ingredientsArray.add(ingrAndInstArray.get(i));
        }


        ingredientsList = binding.ingredientsList;
        if (MainActivity.large_device) {
            binding.buttonShowCheckListGoToShowRecipe.setTextSize(60);
            binding.buttonShowCheckListGoToMaking.setTextSize(60);

            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) binding.textviewShowCheckListRecipeTitle.getLayoutParams();
            p.setMargins(10, 275, 10, 0);
            binding.textviewShowCheckListRecipeTitle.setLayoutParams(p);

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
                LinearLayout.LayoutParams pram = (LinearLayout.LayoutParams) binding.textviewShowCheckListRecipeTitle.getLayoutParams();
                pram.setMargins(330, 60, 330, 0);
                pram.setMarginStart(330);
                pram.setMarginEnd(330);
                binding.textviewShowCheckListRecipeTitle.setLayoutParams(pram);
                pram = (LinearLayout.LayoutParams) binding.ingredientsList.getLayoutParams();
                pram.setMargins(260, 20, 260, 10);
                pram.setMarginStart(260);
                pram.setMarginEnd(260);
                binding.ingredientsList.setLayoutParams(pram);
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
        editTextFontSize();

        int number_of_selected_items = 0;

        for (int i = 1; i < ingredientsList.getCount(); i++) {
            if (ingredientsList.isItemChecked(i)) {
                number_of_selected_items++;
            }
        }
        if (number_of_selected_items == (ingredientsList.getCount() - 1)) {
            binding.buttonShowCheckListGoToMaking.setVisibility(View.VISIBLE);
        } else {
            binding.buttonShowCheckListGoToMaking.setVisibility(View.INVISIBLE);
        }
        return binding.getRoot();

    }

    private void editTextFontSize() {
        ArrayList<Integer> was_checked = new ArrayList<>();
        for (int i = 1; i < ingredientsList.getCount(); i++) {
            if (ingredientsList.isItemChecked(i)) {
                was_checked.add(i);
            }
        }
        int addToFontSize = 0;
        if (MainActivity.large_device) {
            switch (MainActivity.fontSize) {
                case S:
                    adapter = new CustomAdapter(getActivity(), ingredientsArray, R.layout.check_list_layout_large_device_s, false);
                    addToFontSize = -35;
                    break;
                case M:
                    adapter = new CustomAdapter(getActivity(), ingredientsArray, R.layout.check_list_layout_large_device_m, false);
                    addToFontSize = -25;
                    break;
                case L:
                    adapter = new CustomAdapter(getActivity(), ingredientsArray, R.layout.check_list_layout_large_device_l, false);
                    addToFontSize = -15;
                    break;
                case XL:
                    adapter = new CustomAdapter(getActivity(), ingredientsArray, R.layout.check_list_layout_large_device_xl, false);
                    addToFontSize = 0;
                    break;
            }

            binding.textviewShowCheckListRecipeTitle.setTextSize(fontSizeXlLargeDevice[0] + addToFontSize);


        } else {
            switch (MainActivity.fontSize) {
                case S:
                    adapter = new CustomAdapter(getActivity(), ingredientsArray, R.layout.check_list_layout_s, false);
                    addToFontSize = -7;
                    break;
                case M:
                    adapter = new CustomAdapter(getActivity(), ingredientsArray, R.layout.check_list_layout_m, false);
                    addToFontSize = 0;
                    break;
                case L:
                    adapter = new CustomAdapter(getActivity(), ingredientsArray, R.layout.check_list_layout_l, false);
                    addToFontSize = 7;
                    break;
                case XL:
                    adapter = new CustomAdapter(getActivity(), ingredientsArray, R.layout.check_list_layout_xl, false);
                    addToFontSize = 14;
                    break;
            }
            binding.textviewShowCheckListRecipeTitle.setTextSize(fontSizeMSmallDevice[0] + addToFontSize);
        }
        ingredientsList.setAdapter(adapter);
        ingredientsList.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ingredientsList.setDivider(null);
        for (int i : was_checked) {
            ingredientsList.setItemChecked(i, true);
        }
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonShowCheckListGoToMaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number_of_selected_items = 0;
                for (int i = 0; i < ingredientsList.getCount(); i++) {
                    if (ingredientsList.isItemChecked(i)) {
                        number_of_selected_items++;
                    }
                }
                if (number_of_selected_items == ingredientsList.getCount()) {
                    MakingRecipePhase.started = false;
                    MakingRecipePhase.instruction_index = 0;
                    MakingRecipePhase.in_bon_appetit = false;
                    NavHostFragment.findNavController(ShowCheckList.this)
                            .navigate(R.id.action_ShowCheckList_to_MakingRecipePhase);
                }
            }
        });
        binding.buttonShowCheckListGoToShowRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(ShowCheckList.this)
                        .navigate(R.id.action_ShowCheckList_to_ShowRecipe);
            }
        });
        binding.RemyMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.ShowMenuPopup(getContext(), ShowCheckList.this, MainActivity.ClassItBelongsTo.SHOW_CHECKLIST);
            }
        });

        ingredientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (!checkAllWasChecked && ingredientsList.isItemChecked(0)) {
                        for (int i = 1; i < ingredientsList.getCount(); i++) {
                            ingredientsList.setItemChecked(i, true);
                        }
                        checkAllWasChecked = !checkAllWasChecked;
                    } else if (checkAllWasChecked && !ingredientsList.isItemChecked(0)) {
                        for (int i = 1; i < ingredientsList.getCount(); i++) {
                            ingredientsList.setItemChecked(i, false);
                        }
                        checkAllWasChecked = !checkAllWasChecked;
                    }
                }
                check_all_was_checked();
                try {
                    if (MainActivity.is_vision_impaired) {
                        ttsAndStt.process();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (ttsAndStt.tts != null) {
                                        binding.invisible.callOnClick();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }, 500);
                    }
                } catch (Exception e) {
                }
            }
        });
        binding.invisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (MainActivity.is_vision_impaired) {
                        if (checking && !check_all_was_checked()) {
                            run_on_next_check();
                        } else {
                            checking = false;
                            ttsAndStt.onStop();
                            ttsAndStt.resume();
                        }
                    } else {
                        checking = false;
                        ttsAndStt.onStop();
                    }
                } catch (Exception e) {
                }
            }
        });


    }

    private boolean check_all_was_checked() {
        int number_of_selected_items = 0;
        for (int i = 1; i < ingredientsList.getCount(); i++) {
            if (ingredientsList.isItemChecked(i)) {
                number_of_selected_items++;
            }
        }
        if (number_of_selected_items == (ingredientsList.getCount() - 1)) {
            ingredientsList.setItemChecked(0, true);
            binding.buttonShowCheckListGoToMaking.setVisibility(View.VISIBLE);
            if (MainActivity.is_vision_impaired) {
                binding.buttonShowCheckListGoToMaking.callOnClick();
            }
            return true;
        } else {
            binding.buttonShowCheckListGoToMaking.setVisibility(View.INVISIBLE);
            return false;
        }
    }

    public void run_on_next_check() {

        ttsAndStt.resume();
        checking = true;
        if (ingredient_index == -1) {
            ingredient_index = 0;
        }
        while (true) {
            if (!ingredientsList.isItemChecked(ingredient_index)) {
                if (ingredient_index == 0) {
                    ttsAndStt.ttsSpeakFlushQueue("Do you have all the ingredients?");
                } else {
                    ttsAndStt.ttsSpeakFlushQueue("Do you have " + ingredientsArray.get(ingredient_index) + "?");
                }
                return;
            }
            ingredient_index = (ingredient_index + 1) % ingredientsArray.size();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (ttsAndStt.tts == null) {
            ttsAndStt.createTTS();
        }
        editTextFontSize();
        binding.animation.setVisibility(View.INVISIBLE);

        if (ingredientsList.isItemChecked(0)) {
            checkAllWasChecked = true;
        }
        check_all_was_checked();
        if (MainActivity.POPUP_WINDOW_SCORE == null || !MainActivity.POPUP_WINDOW_SCORE.isShowing()) {
            if (MainActivity.is_vision_impaired) {
                if (ttsAndStt.process()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (ttsAndStt.tts != null) {

                                    if (!checking && ingredient_index == -1) {
                                        ttsAndStt.ttsSpeakFlushQueue("let's make sure you have all the necessary ingredients. Please answer with yes or no.");
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    if (ttsAndStt.tts != null) {
                                                        checking = true;
                                                        binding.invisible.callOnClick();
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        }, 7500);
                                    } else {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    if (ttsAndStt.tts != null) {
                                                        binding.invisible.callOnClick();
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        }, 1000);

                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }, 500);

                }
            }
        } else {
            onStop();
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

}
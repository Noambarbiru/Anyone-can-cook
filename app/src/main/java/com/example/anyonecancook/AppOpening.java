package com.example.anyonecancook;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.anyonecancook.databinding.AppOpeningBinding;

public class AppOpening extends Fragment {

    private AppOpeningBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = AppOpeningBinding.inflate(inflater, container, false);
        if (MainActivity.large_device) {
            binding.appOpeningTextview.setTextSize(115);
            binding.appOpeningButton.setTextSize(80);
            binding.appOpeningButton.getLayoutParams().width = 550;
            binding.appOpeningButton.requestLayout();
            int orientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            if (orientation == Surface.ROTATION_90
                    || orientation == Surface.ROTATION_270) {

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                params.gravity = Gravity.START | Gravity.CENTER_HORIZONTAL;
                params.leftMargin = (int) MainActivity.x_px / 12;
                params.topMargin = 30;
                binding.appOpeningTextview.setLayoutParams(params);
                binding.appOpeningTextview.setGravity(Gravity.CENTER);
//
                binding.appOpeningImageView.setImageResource(R.drawable.opening_img_large);
            }

        }
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.appOpeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EnterRecipeLink.came_from_app_opening = true;

                NavHostFragment.findNavController(AppOpening.this)
                        .navigate(R.id.action_AppOpening_to_EnterRecipeLink);
            }
        });
        if (MainActivity.got_url_from_browser) {
            binding.appOpeningButton.callOnClick();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.got_url_from_browser) {
            binding.appOpeningButton.callOnClick();
        }
    }

}
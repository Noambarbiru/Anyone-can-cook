package com.example.anyonecancook;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.anyonecancook.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    //    static public Client client;
    private AppBarConfiguration appBarConfiguration;
    private static ActivityMainBinding binding;
    public static boolean got_url_from_browser = false;
    public static String url;
    public static boolean large_device;
    public static float x_px;
    public static float y_px;
    public static boolean is_vision_impaired;
    public static boolean is_not_first_time = false;
    public static SharedPreferences settings;
    public static SharedPreferences settingsFontSize;
    public static final String prefVisionImpaired = "VisionImpaired";
    public static final String prefFontSize = "VisionImpaired";
    public static final String prefFontSizeS = "isFontSizeS";
    public static final String prefFontSizeM = "isFontSizeM";
    public static final String prefFontSizeL = "isFontSizeL";
    public static final String prefFontSizeXL = "isFontSizeXL";
    public static final String prefVisionImpairedKey = "isVisionImpaired";
    public static final String prefFirstTimeKey = "isNotFirstTime";
    public static PopupWindow POPUP_WINDOW_SCORE;
    public static FontSize fontSize;
    public static Fragment fragment;
    public static ClassItBelongsTo classItBelongsTo;

    enum FontSize {
        S,
        M,
        L,
        XL
    }

    enum ClassItBelongsTo {
        MAKING_RECIPE_PHASE,
        ENTER_RECIPE_LINK,
        SHOW_RECIPE,
        SHOW_CHECKLIST

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);


        run_on_resume();
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (is_vision_impaired) {
            binding.mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background));
            binding.toolbar.setBackgroundColor(getResources().getColor(R.color.light_background));
        } else {
            binding.mainLayout.setBackgroundColor(getResources().getColor(R.color.background));
            binding.toolbar.setBackgroundColor(getResources().getColor(R.color.background));

        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }

    protected void run_on_resume() {
        settings = getSharedPreferences(prefVisionImpaired, MODE_PRIVATE);
        settingsFontSize = getSharedPreferences(prefFontSize, MODE_PRIVATE);
        is_vision_impaired = settings.getBoolean(prefVisionImpairedKey, false);
        if (settings.getBoolean(prefFontSizeS, false)) {
            MainActivity.fontSize = FontSize.S;
        } else if (settings.getBoolean(prefFontSizeM, false)) {
            MainActivity.fontSize = FontSize.M;
        } else if (settings.getBoolean(prefFontSizeL, false)) {
            MainActivity.fontSize = FontSize.L;
        } else if (settings.getBoolean(prefFontSizeXL, false)) {
            MainActivity.fontSize = FontSize.XL;
        } else if (is_vision_impaired) {
            MainActivity.fontSize = FontSize.XL;
        } else {
            MainActivity.fontSize = null;
        }
        if (binding != null) {
            if (is_vision_impaired) {
                binding.mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background));
                binding.toolbar.setBackgroundColor(getResources().getColor(R.color.light_background));
            } else {
                binding.mainLayout.setBackgroundColor(getResources().getColor(R.color.background));
                binding.toolbar.setBackgroundColor(getResources().getColor(R.color.background));

            }
        }

        is_not_first_time = MainActivity.settings.getBoolean(MainActivity.prefFirstTimeKey, false);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        x_px = metrics.widthPixels;
        y_px = metrics.heightPixels;
        float yInches = y_px / metrics.ydpi;
        float xInches = x_px / metrics.xdpi;

        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches < 7) { // small device don't rotate
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (MainActivity.fontSize == null) {
                MainActivity.fontSize = FontSize.M;
            }
            large_device = false;
        } else {// large device
            if (MainActivity.fontSize == null) {
                MainActivity.fontSize = FontSize.M;
            }
            large_device = true;
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if ((Intent.ACTION_SEND.equals(action)) && type != null && "text/plain".equals(type)) {
            url = (String) intent.getExtras().get(Intent.EXTRA_TEXT);
            got_url_from_browser = true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        String type = intent.getType();
        if ((Intent.ACTION_SEND.equals(action)) && type != null && "text/plain".equals(type)) {
            url = (String) intent.getExtras().get(Intent.EXTRA_TEXT);
            got_url_from_browser = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        run_on_resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static void update_keyWords(View layout) {
        if (MainActivity.is_vision_impaired) {
            switch (MainActivity.classItBelongsTo) {
                case ENTER_RECIPE_LINK:
                    ((TextView) layout.findViewById(R.id.keyWordsTextView)).setText(R.string.keywords_vision_impiared);
                    break;
                case SHOW_RECIPE:
                    if (EnterRecipeLink.recipeCrawler == null) {
                        ((TextView) layout.findViewById(R.id.keyWordsTextView)).setText(R.string.keywords_vision_impiared_show_recipe);
                    } else {
                        ((TextView) layout.findViewById(R.id.keyWordsTextView)).setText(R.string.keywords_vision_impiared_show_recipe_change_recipe);
                    }
                    break;
                case SHOW_CHECKLIST:
                    ((TextView) layout.findViewById(R.id.keyWordsTextView)).setText(R.string.keywords_vision_impiared_checkList);
                    break;
                case MAKING_RECIPE_PHASE:
                    ((TextView) layout.findViewById(R.id.keyWordsTextView)).setText(R.string.keywords_vision_impiared_making_recipe);
                    break;
            }
            ((LinearLayout) layout.findViewById(R.id.keyWordLayout)).getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            ((LinearLayout) layout.findViewById(R.id.keyWordLayout)).getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            ((LinearLayout) layout.findViewById(R.id.keyWordLayout)).requestLayout();
        } else {
            if (MainActivity.classItBelongsTo == ClassItBelongsTo.MAKING_RECIPE_PHASE) {
                ((TextView) layout.findViewById(R.id.keyWordsTextView)).setText(R.string.keywords_not_vision_impiared);
                ((LinearLayout) layout.findViewById(R.id.keyWordLayout)).getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                ((LinearLayout) layout.findViewById(R.id.keyWordLayout)).getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                ((LinearLayout) layout.findViewById(R.id.keyWordLayout)).requestLayout();

            } else {
                ((LinearLayout) layout.findViewById(R.id.keyWordLayout)).getLayoutParams().width = 0;
                ((LinearLayout) layout.findViewById(R.id.keyWordLayout)).getLayoutParams().height = 0;
                ((LinearLayout) layout.findViewById(R.id.keyWordLayout)).requestLayout();

            }
        }
    }

    public static void ShowMenuPopup(Context fragment_context, Fragment fragment, ClassItBelongsTo classItBelongsTo) {
        MainActivity.fragment = fragment;
        MainActivity.classItBelongsTo = classItBelongsTo;
        LayoutInflater layoutInflater = (LayoutInflater) fragment_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_menu_popup, null);
        layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        if (MainActivity.large_device) {
            ((FloatingActionButton) layout.findViewById(R.id.close_window_button)).setCustomSize(150);
            ((FloatingActionButton) layout.findViewById(R.id.close_window_button)).setMaxImageSize(140);
            ((FloatingActionButton) layout.findViewById(R.id.close_window_button)).requestLayout();
            ((Switch) layout.findViewById(R.id.visionImpairedModeChange)).setTextSize(50);
            ((TextView) layout.findViewById(R.id.header_radioButtons)).setTextSize(50);
            ((RadioButton) layout.findViewById(R.id.fontSize_S)).setTextSize(50);
            ((RadioButton) layout.findViewById(R.id.fontSize_M)).setTextSize(50);
            ((RadioButton) layout.findViewById(R.id.fontSize_L)).setTextSize(50);
            ((RadioButton) layout.findViewById(R.id.fontSize_XL)).setTextSize(50);
            ((Button) layout.findViewById(R.id.tutorialVideo)).setTextSize(50);
            ((TextView) layout.findViewById(R.id.keyWordsTitle)).setTextSize(50);
            ((TextView) layout.findViewById(R.id.to_wake_up)).setTextSize(35);

            ((TextView) layout.findViewById(R.id.keyWordsTextView)).setTextSize(40);

        }

        POPUP_WINDOW_SCORE = new PopupWindow(fragment_context);
        POPUP_WINDOW_SCORE.setContentView(layout);
        POPUP_WINDOW_SCORE.setWidth((int) MainActivity.x_px);
        POPUP_WINDOW_SCORE.setHeight((int) MainActivity.y_px);
        POPUP_WINDOW_SCORE.setFocusable(false);


        // prevent clickable background
        POPUP_WINDOW_SCORE.setBackgroundDrawable(null);

        POPUP_WINDOW_SCORE.showAtLocation(layout, Gravity.CENTER, 1, 1);
        POPUP_WINDOW_SCORE.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                MainActivity.fragment.onResume();
            }
        });


        // Getting a reference to button one and do something
        ((Switch) layout.findViewById(R.id.visionImpairedModeChange)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingInflatedId")
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (MainActivity.is_vision_impaired != isChecked) {
                    SharedPreferences.Editor editor = MainActivity.settings.edit();
                    editor.putBoolean(MainActivity.prefVisionImpairedKey, isChecked);
                    editor.putBoolean(MainActivity.prefFirstTimeKey, true);
                    editor.commit();
                    MainActivity.is_vision_impaired = isChecked;
                    if (isChecked) {
                        ((RadioButton) layout.findViewById(R.id.fontSize_XL)).setChecked(true);
                        MainActivity.binding.mainLayout.setBackgroundColor(fragment.getResources().getColor(R.color.light_background));
                        MainActivity.binding.toolbar.setBackgroundColor(fragment.getResources().getColor(R.color.light_background));
                    } else {
                        ((RadioButton) layout.findViewById(R.id.fontSize_M)).setChecked(true);
                        MainActivity.binding.mainLayout.setBackgroundColor(fragment.getResources().getColor(R.color.background));
                        MainActivity.binding.toolbar.setBackgroundColor(fragment.getResources().getColor(R.color.background));

                    }
                    MainActivity.update_keyWords(layout);
                }
            }
        });

        switch (fontSize) {
            case S:
                ((RadioButton) layout.findViewById(R.id.fontSize_S)).setChecked(true);
                break;
            case M:
                ((RadioButton) layout.findViewById(R.id.fontSize_M)).setChecked(true);
                break;
            case L:
                ((RadioButton) layout.findViewById(R.id.fontSize_L)).setChecked(true);
                break;
            case XL:
                ((RadioButton) layout.findViewById(R.id.fontSize_XL)).setChecked(true);
                break;
        }
//        ((Switch) layout.findViewById(R.id.visionImpairedModeChange)).setChecked(!MainActivity.is_vision_impaired);
        ((Switch) layout.findViewById(R.id.visionImpairedModeChange)).setChecked(MainActivity.is_vision_impaired);
        MainActivity.update_keyWords(layout);
        ((RadioButton) layout.findViewById(R.id.fontSize_S)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.fontSize = FontSize.S;
                    SharedPreferences.Editor editor = MainActivity.settingsFontSize.edit();
                    editor.putBoolean(MainActivity.prefFontSizeS, true);
                    editor.putBoolean(MainActivity.prefFontSizeM, false);
                    editor.putBoolean(MainActivity.prefFontSizeL, false);
                    editor.putBoolean(MainActivity.prefFontSizeXL, false);
                    editor.commit();
                }
            }
        });
        ((RadioButton) layout.findViewById(R.id.fontSize_M)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.fontSize = FontSize.M;
                    SharedPreferences.Editor editor = MainActivity.settingsFontSize.edit();
                    editor.putBoolean(MainActivity.prefFontSizeS, false);
                    editor.putBoolean(MainActivity.prefFontSizeM, true);
                    editor.putBoolean(MainActivity.prefFontSizeL, false);
                    editor.putBoolean(MainActivity.prefFontSizeXL, false);
                    editor.commit();
                }
            }
        });
        ((RadioButton) layout.findViewById(R.id.fontSize_L)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.fontSize = FontSize.L;
                    SharedPreferences.Editor editor = MainActivity.settingsFontSize.edit();
                    editor.putBoolean(MainActivity.prefFontSizeS, false);
                    editor.putBoolean(MainActivity.prefFontSizeM, false);
                    editor.putBoolean(MainActivity.prefFontSizeL, true);
                    editor.putBoolean(MainActivity.prefFontSizeXL, false);
                    editor.commit();
                }
            }
        });
        ((RadioButton) layout.findViewById(R.id.fontSize_XL)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.fontSize = FontSize.XL;
                    SharedPreferences.Editor editor = MainActivity.settingsFontSize.edit();
                    editor.putBoolean(MainActivity.prefFontSizeS, false);
                    editor.putBoolean(MainActivity.prefFontSizeM, false);
                    editor.putBoolean(MainActivity.prefFontSizeL, false);
                    editor.putBoolean(MainActivity.prefFontSizeXL, true);
                    editor.commit();
                }
            }
        });

        // Getting a reference to button two and do something
        ((FloatingActionButton) layout.findViewById(R.id.close_window_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POPUP_WINDOW_SCORE.dismiss();
            }
        });
        ((Button) layout.findViewById(R.id.tutorialVideo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "JCXP7xl-Xqw";
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" + id));
                try {
                    MainActivity.fragment.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    MainActivity.fragment.startActivity(webIntent);
                }

            }
        });
        switch (classItBelongsTo) {
            case ENTER_RECIPE_LINK:
                ((EnterRecipeLink) fragment).onStop();
                break;
            case SHOW_RECIPE:
                ((ShowRecipe) fragment).onStop();
                break;
            case SHOW_CHECKLIST:
                if (!is_vision_impaired) {
                    ShowCheckList.checking = false;
                }
                ((ShowCheckList) fragment).onStop();
                break;
            case MAKING_RECIPE_PHASE:
                ((MakingRecipePhase) fragment).onStop();
                break;
        }

    }
}
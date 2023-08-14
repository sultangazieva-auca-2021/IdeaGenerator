package com.example.ideagenerator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ideagenerator.databinding.ActivityMainBinding;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isRandom = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.title.setTextColor(Color.BLUE);
        binding.pictureTheme.setTextColor(Color.BLUE);

        binding.pictureTheme.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return buttonPressedAction();
                }

                return false;
            }
        });

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPressedAction();
            }
        });

        binding.nextButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getApplicationContext(), "Your Picture is...",  Toast.LENGTH_SHORT).show();
                return false; // We just ignored this action that therefore onClick will also be performed, if it will be true than onClick will not be performed
            }
        });

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                isRandom = binding.radioGroup.getCheckedRadioButtonId() == binding.yesButton.getId();
                updateUI();
            }
        });

//        binding.checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isRandom = binding.checkbox.isChecked();
//                updateUI();
//            }
//        });
//
//        binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//            }
//        });

        updateUI();
    }

    public boolean buttonPressedAction() {
        String keywordText = binding.pictureTheme.getEditableText().toString();
        if (!isRandom && keywordText.isEmpty()) {
            binding.pictureTheme.setError("Theme is empty");
            return true;
        }

        String encodedKeyword;
        try {
            encodedKeyword = URLEncoder.encode(keywordText, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String url = "https://source.unsplash.com/random/800x600?";
        if (!isRandom) {
            url += encodedKeyword;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        Glide.with(MainActivity.this)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        binding.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        binding.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(binding.centerImage);

        return false;
    }

    private void updateUI() {
        if (isRandom) {
            binding.pictureTheme.setVisibility(View.INVISIBLE);
        } else {
            binding.pictureTheme.setVisibility(View.VISIBLE);
        }
    }
}

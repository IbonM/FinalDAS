package com.montoya.picedit.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.montoya.picedit.databinding.ActivityShareBinding;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityShareBinding binding = ActivityShareBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}
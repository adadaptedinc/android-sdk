package com.adadapted.sdktestapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.sdktestapp.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        AdAdapted.INSTANCE
                .withAppId(getResources().getString(R.string.adadapted_api_key))
                .inEnv(AdAdapted.Env.DEV)
                .start(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, createFragment())
                .commit();
    }
}

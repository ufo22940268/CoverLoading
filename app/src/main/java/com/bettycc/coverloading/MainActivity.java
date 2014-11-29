package com.bettycc.coverloading;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bettycc.coverloading.library.CoverView;


public class MainActivity extends Activity {

    private CoverView mCoverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCoverView = (CoverView) findViewById(R.id.cover);
        mCoverView.startLoading();
    }


    public void startLoading(View view) {
        mCoverView.startLoading();
    }
}

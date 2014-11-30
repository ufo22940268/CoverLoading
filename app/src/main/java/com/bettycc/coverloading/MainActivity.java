package com.bettycc.coverloading;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bettycc.coverloading.library.CoverView;


public class MainActivity extends Activity {

    private CoverView mCoverView;
    private int[] sleepTimes = {
            1, 2, 4, 2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCoverView = (CoverView) findViewById(R.id.cover);
    }


    public void startLoading(View view) {
        view.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int p = 0;
                while (!mCoverView.isFinished()) {
                    p = (int) (p + Math.random() * 20);
                    final int finalP = p;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCoverView.setProgress(finalP);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

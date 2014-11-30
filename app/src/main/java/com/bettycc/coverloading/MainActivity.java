package com.bettycc.coverloading;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bettycc.coverloading.library.CoverView;


public class MainActivity extends Activity {

    private CoverView mCoverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCoverView = (CoverView) findViewById(R.id.cover);

        mCoverView.setOnPauseResumeListener(new CoverView.OnPauseResumeListener() {
            @Override
            public void onPause() {
                Toast.makeText(MainActivity.this, "paused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResume() {
                Toast.makeText(MainActivity.this, "resumed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void startLoading(final View view) {
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCoverView.resetValues();
                        view.setEnabled(true);
                    }
                });
            }
        }).start();
    }
}

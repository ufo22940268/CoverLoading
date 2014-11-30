package com.bettycc.coverloading;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bettycc.coverloading.library.CoverView;


public class MainActivity extends Activity {

    private CoverView[] mCoverView = new CoverView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCoverView[0] = (CoverView) findViewById(R.id.cover_small);
        mCoverView[1] = (CoverView) findViewById(R.id.cover_big);
        mCoverView[2] = (CoverView) findViewById(R.id.cover_square);

        mCoverView[0].setOnPauseResumeListener(new CoverView.OnPauseResumeListener() {
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


    public void startLoading(final View view, final CoverView coverView) {
        view.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int p = 0;
                while (!coverView.isFinished()) {
                    p = (int) (p + Math.random() * 20);
                    final int finalP = p;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            coverView.setProgress(finalP);
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
                        coverView.resetValues();
                        view.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    public void startSmall(View view) {
        startLoading(view, mCoverView[0]);
    }

    public void startBig(View view) {
        startLoading(view, mCoverView[1]);
    }

    public void startSquare(View view) {
        startLoading(view, mCoverView[2]);
    }
}

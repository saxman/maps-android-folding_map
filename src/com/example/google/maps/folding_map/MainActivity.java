/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.google.maps.folding_map;

import com.example.android.foldinglayout.FoldingLayout;
import com.example.android.foldinglayout.OnFoldListener;
import com.example.google.maps.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

public class MainActivity extends FragmentActivity implements OnFoldListener {
    static final long FOLD_ANIMATION_DURATION = 3000;

    private FoldingLayout mFoldingLayout;
    private GoogleMap mMap;

    private MenuItem mMapAction;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();

        // Map can be null if there is a Google Play services issue.
        if (mMap != null) {
            // Wait until the map has loaded before we allow it to be folded.
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMapAction.setEnabled(true);
                }
            });
        }

        mFoldingLayout = (FoldingLayout) findViewById(R.id.folding_layout);

        // Wait until the FoldingLayout has be laid out; it needs dimensions.
        mFoldingLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {
                        mFoldingLayout.setNumberOfFolds(3);
                        mFoldingLayout.setBackgroundColor(Color.BLACK);
                        mFoldingLayout.setFoldListener(MainActivity.this);

                        ViewTreeObserver obs = mFoldingLayout.getViewTreeObserver();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            obs.removeOnGlobalLayoutListener(this);
                        } else {
                            obs.removeGlobalOnLayoutListener(this);
                        }
                    }
                });

        mImageView = (ImageView) findViewById(R.id.image_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        mMapAction = menu.findItem(R.id.action_map);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                mMapAction.setEnabled(false);
                
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        mImageView.setImageBitmap(snapshot);
                        animateFold();
                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void animateFold()
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFoldingLayout, "foldFactor",
                mFoldingLayout.getFoldFactor(), 1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(1);
        animator.setDuration(FOLD_ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    @Override
    public void onStartFold() {
    }

    @Override
    public void onEndFold() {
        mMapAction.setEnabled(true);
        mImageView.setImageBitmap(null);
    }
}

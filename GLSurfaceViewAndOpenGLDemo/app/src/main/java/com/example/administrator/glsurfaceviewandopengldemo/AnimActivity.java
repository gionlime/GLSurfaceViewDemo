/*
 *
 *  *
 *  *  * Copyright (C) 2016 ChillingVan
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.example.administrator.glsurfaceviewandopengldemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;
import com.chillingvan.canvasgl.textureFilter.ContrastFilter;
import com.chillingvan.canvasgl.textureFilter.HueFilter;
import com.chillingvan.canvasgl.textureFilter.PixelationFilter;
import com.chillingvan.canvasgl.textureFilter.SaturationFilter;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;
import com.example.administrator.glsurfaceviewandopengldemo.bubble.Bubble;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimActivity extends AppCompatActivity {

    public static final float VY_MULTIPLIER = 0.01f; // px/ms
    public static final float VX_MULTIPLIER = 0.01f;
    public static final int MIN_VY = 10;
    public static final int MAX_VY = 30;
    public static final int MIN_VX = 10;
    public static final int MAX_VX = 30;

    private List<Bubble> bubbles = new ArrayList<>();
    private List<Bubble> downBubbles = new ArrayList<>();
    private List<TextureFilter> upFilterList = new ArrayList<>();
    private List<TextureFilter> downFilterList = new ArrayList<>();
    private Bitmap bitmap;
    private AnimGLView animGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);

        /** 初始化相關需求 */
        initFilterList(upFilterList);
        initFilterList(downFilterList);
        animGLView = (AnimGLView) findViewById(R.id.anim_gl_view);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pokemon_ball2);

        /** 將9個bitmap轉化成9個Bubble, 並放入animGLView中 讓其顯示 */
        for (int i = 0; i < 10; i++) {
            bubbles.add(createBubble(upFilterList));
        }
        animGLView.setBubbles(bubbles);
    }

    /** 將指定的TextureFilter集合, 加入各種特效的TextureFilter */
    private void initFilterList(List<TextureFilter> filterList) {
        filterList.add(new BasicTextureFilter());
        filterList.add(new ContrastFilter(1.6f));
        filterList.add(new SaturationFilter(1.6f));
        filterList.add(new PixelationFilter(12));
        filterList.add(new HueFilter(100));
        filterList.add(new HueFilter(100));
        filterList.add(new HueFilter(100));
    }

    /** 從傳入的filterList集合 隨機選擇一個特效, 並產生一個新的Bubble */
    private Bubble createBubble(List<TextureFilter> filterList) {
        Random random = new Random();
        TextureFilter textureFilter = filterList.get(random.nextInt(filterList.size()));            // 從filterList裡面 隨機取一種特效出來
        float vy = -(MIN_VY + random.nextInt(MAX_VY)) * VY_MULTIPLIER;
        float vx = (MIN_VX + random.nextInt(MAX_VX)) * VX_MULTIPLIER;
        vx = random.nextBoolean() ? vx : -vx;                                                       // 隨機將vx變成-vx
        float vRotate = 0.05f;                                                                      // 自轉的幅度
        return new Bubble(new PointF(260, 260), vx, vy, vRotate, bitmap, textureFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        animGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        animGLView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

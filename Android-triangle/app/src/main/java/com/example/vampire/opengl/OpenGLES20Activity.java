package com.example.vampire.opengl;
/**
 * Created by Vampire on 2018/4/27.
 * 主显示文件，将GLSurfaceView放到Activity中显示，并添加按钮监听控制
 */
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
//以下为添加button并实现监听所需的import
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class OpenGLES20Activity extends Activity {

    private MyGLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);

        /**
         * 以下为在GLSurfaceView上叠加button并添加监听事件所需代码
         */
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        Button b_name = new Button(this);
        b_name.setText("转动");
        ll.addView(b_name);

        addContentView(ll, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        b_name.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 点击button后，触发OnClickListener，执行以下语句
                mGLView.move();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}

package com.wordpress.bytedebugger.simplecamera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.squareup.picasso.Picasso;


public class MainActivity extends Activity {
    private Camera mCamera = null;
    private CameraView mCameraView = null;
    Button rotate;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout relative_layout = (RelativeLayout)findViewById(R.id.relative_layout);
        LinearLayout relativeLayout = (LinearLayout) LinearLayout.inflate(MainActivity.this, R.layout.custom_image_view, null);
       // relative_layout.addView(relativeLayout);

        LinearLayout sub_layout = (LinearLayout)relativeLayout.findViewById(R.id.sub_layout);
       // rotate = (Button)findViewById(R.id.rotate);

        try{
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
            camera_view.addView(createView(0));
        }


        //btn to close the application
        final ImageView imgClose = (ImageView)relativeLayout.findViewById(R.id.img_main);
        sub_layout.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
        //sub_layout.setOnTouchListener(new MoveViewTouchListener(getApplicationContext(), sub_layout));


    }

    public View createView(final int index){

        //Main LinearLayout
        final LinearLayout sub_layout = new LinearLayout(this);
        sub_layout.setTag("sub_layout"+index);
        sub_layout.setId(index);
        sub_layout.setBackgroundResource(R.drawable.image_border);
      //  sub_layout.setGravity(center_vertical|center_horizontal);
        sub_layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layout_379 = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        sub_layout.setLayoutParams(layout_379);

        //Child RelativeLayout
        final RelativeLayout sub_relative_layout = new RelativeLayout(this);
        //  sub_relative_layout.setId(R.id.sub_relative_layout);
        sub_relative_layout.setTag("sub_relative_layout_"+index);
        sub_relative_layout.setId(index);
        RelativeLayout.LayoutParams layout_892 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        // layout_892.weight = 0.09;
        sub_relative_layout.setLayoutParams(layout_892);


        //relativeLayout's main imageView
        final ImageView img_main = new ImageView(this);
        img_main.setId(index);
        img_main.setTag("img_main_"+index);
        //img_main.setPaddingRelative((10/getApplicationContext().getResources().getDisplayMetrics().density), 0, 0, 0);
        RelativeLayout.LayoutParams layout_329 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        img_main.setLayoutParams(layout_329);
        Picasso.with(getApplicationContext()).load("https://www.google.co.in/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png").into(img_main);
        sub_relative_layout.addView(img_main);


        //************** relativeLayout's zoom_btn imageView
        ImageView zoom_btn = new ImageView(this);
        zoom_btn.setId(index);
        zoom_btn.setTag("zoom_btn_"+index);

        zoom_btn.setImageResource(android.R.drawable.ic_menu_zoom);
        RelativeLayout.LayoutParams layout_703 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        layout_703.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layout_703.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        layout_703.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        zoom_btn.setLayoutParams(layout_703);
        sub_relative_layout.addView(zoom_btn);


        //************** relativeLayout's close_btn imageView
        ImageView close_btn = new ImageView(this);
        close_btn.setId(index);
        close_btn.setTag("close_btn_"+index);
        close_btn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        RelativeLayout.LayoutParams layout_391 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        close_btn.setLayoutParams(layout_391);
        sub_relative_layout.addView(close_btn);


        //************** relativeLayout's rotate_btn imageView
        ImageView rotate_btn = new ImageView(this);
        rotate_btn.setId(index);
        rotate_btn.setTag("rotate_btn_"+index);
       // rotate_btn.setImageResource(R.drawable.selection_rotate_blue_hdpi);
        RelativeLayout.LayoutParams layout_551 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_551.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rotate_btn.setLayoutParams(layout_551);
        sub_relative_layout.addView(rotate_btn);
        sub_layout.addView(sub_relative_layout);

        rotate_btn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                sub_layout.setRotation(sub_layout.getRotation() + 90);
            }
        });



        sub_layout.setOnTouchListener(new MoveViewTouchListener(getApplicationContext(), sub_layout));



        View view = sub_layout;

        return view;
    }
}

package com.wordpress.bytedebugger.simplecamera;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Camera2 extends AppCompatActivity implements View.OnTouchListener,
        SurfaceHolder.Callback{
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private ViewGroup view_layout, main_layout;
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    String logoImageId = "";
    Bitmap bitmap = null;
    private android.hardware.Camera camera = null;
    private SurfaceView cameraSurfaceView = null;
    private SurfaceHolder cameraSurfaceHolder = null;
    private boolean previewing = false;
    RelativeLayout relativeLayout, buttom_relative;
    LinearLayout relative_layout;
    int currentCameraId = 0;
    private Button btnCapture = null, add_btn;
    ImageButton useOtherCamera = null;
    ImageView logoImageView, preview;
    int width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     getWindow().setFormat(PixelFormat.TRANSLUCENT);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
               WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_camera2);
        width = getWindowManager().getDefaultDisplay().getWidth();
       // logoImageView = (ImageView) findViewById(R.id.logoImageView);
        relative_layout = (LinearLayout)findViewById(R.id.relative_layout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            logoImageId = extras.getString("logoImageId ");
        }
        try {
            /*File file = new File(Environment.getExternalStorageDirectory()
                    + "/" + getPackageName() + "/logo/" + logoImageId
                    + ".jpg");
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());*/
            //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check);
           // logoImageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //logoImageView.setOnTouchListener(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.containerImg);
       // buttom_relative = (RelativeLayout) findViewById(R.id.buttom_relative);
        view_layout = (RelativeLayout)findViewById(R.id.view_layout);
        main_layout = (FrameLayout)findViewById(R.id.main_layout);
        preview = (ImageView)findViewById(R.id.preview);
     //   add_btn = (Button)findViewById(R.id.add);
        relativeLayout.setDrawingCacheEnabled(true);
        cameraSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        cameraSurfaceHolder = cameraSurfaceView.getHolder();
        cameraSurfaceHolder.addCallback(this);
        btnCapture = (Button) findViewById(R.id.button);

        view_layout.setDrawingCacheEnabled(true);
        view_layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view_layout.layout(0, 0, view_layout.getMeasuredWidth(), view_layout.getMeasuredHeight());
        view_layout.buildDrawingCache(true);

        View view = createView(0);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/2,width/2);
        view.setLayoutParams(param);
        view_layout.addView(view);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                bitmap = Bitmap.createBitmap(view_layout.getDrawingCache());
                view_layout.setDrawingCacheEnabled(true);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                camera.takePicture(null, null, cameraPictureCallbackJpeg);
            }
        });

       /* add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), PreviewActivity.class);
                startActivityForResult(i, 1);
            }
        });*/
    }
int viewTagCounter =1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //   seekBar.setProgress(0);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                View newView = createView(viewTagCounter);
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/2,width/2);
                newView.setLayoutParams(param);
                view_layout.addView(newView);
                //((FrameLayout.LayoutParams)newView.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;

                setBorderColor(newView, true);
                viewTagCounter++;

            }
        }
    }

    public int getActionBarHeight(){
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight= contentViewTop - statusBarHeight;
return statusBarHeight+titleBarHeight;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // TODO Auto-generated method stub
        try {
            camera = android.hardware.Camera.open();
            android.hardware.Camera.Parameters params = camera.getParameters();


            // Check what resolutions are supported by your camera
            List<android.hardware.Camera.Size> sizes = params.getSupportedPictureSizes();

            // setting small image size in order to avoid OOM error
            android.hardware.Camera.Size cameraSize = null;
            for (android.hardware.Camera.Size size : sizes) {
                //set whatever size you need
              //  if(size.height< relativeLayout.getHeight()) {
                cameraSize = size;
                break;
               // }
            }

            if (cameraSize != null) {

                params.setPictureSize(cameraSize.width, cameraSize.height);

                camera.setParameters(params);

                float ratio = relativeLayout.getHeight()*1f/cameraSize.height;
                float w = cameraSize.width*ratio;
                float h = cameraSize.height*ratio;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)w, (int)h);
                cameraSurfaceView.setLayoutParams(lp);
            }
        } catch (RuntimeException e) {
            Toast.makeText(
                    getApplicationContext(),
                    "Device camera  is not working properly, please try after sometime.",
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }
        try {

            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
               camera.setDisplayOrientation(90);
                android.hardware.Camera.Size cameraSize = camera.getParameters().getPictureSize();
                int wr = relativeLayout.getWidth();
                int hr = relativeLayout.getHeight();
                float ratio = relativeLayout.getWidth()*1f/cameraSize.height;
                float w = cameraSize.width*ratio;
                float h = cameraSize.height*ratio;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)h, (int)w);
                cameraSurfaceView.setLayoutParams(lp);
            }else {
                camera.setDisplayOrientation(0);
                android.hardware.Camera.Size cameraSize = camera.getParameters().getPictureSize();
                float ratio = relativeLayout.getHeight()*1f/cameraSize.height;
                float w = cameraSize.width*ratio;
                float h = cameraSize.height*ratio;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)w, (int)h);
                cameraSurfaceView.setLayoutParams(lp);
            }

            camera.setPreviewDisplay(cameraSurfaceHolder);
            camera.startPreview();
            previewing = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.release();
        camera = null;
        previewing = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null && event.getPointerCount() == 3) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (view.getWidth() / 2) * sx;
                        float yc = (view.getHeight() / 2) * sx;
                        matrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);

        return true;
    }
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    android.hardware.Camera.PictureCallback cameraPictureCallbackJpeg = new android.hardware.Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
            // TODO Auto-generated method stub

            BitmapFactory.Options options = new BitmapFactory.Options();
            //o.inJustDecodeBounds = true;
            Bitmap cameraBitmapNull = BitmapFactory.decodeByteArray(data, 0,
                    data.length, options);

            int wid = options.outWidth;
            int hgt = options.outHeight;
            Matrix nm = new Matrix();

            android.hardware.Camera.Size cameraSize = camera.getParameters().getPictureSize();
            float ratio = relativeLayout.getHeight()*1f/cameraSize.height;
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                nm.postRotate(90);
                nm.postTranslate(hgt, 0);
                wid = options.outHeight;
                hgt = options.outWidth;
                ratio = relativeLayout.getWidth()*1f/cameraSize.height;

            }else {
                wid = options.outWidth;
                hgt = options.outHeight;
                ratio = relativeLayout.getHeight()*1f/cameraSize.height;
            }

            float[] f = new float[9];
            matrix.getValues(f);

            f[0] = f[0]/ratio;
            f[4] = f[4]/ratio;
            f[5] = f[5]/ratio;
            f[2] = f[2]/ratio;
            matrix.setValues(f);

            Bitmap newBitmap = Bitmap.createBitmap(wid, hgt,
                    Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(newBitmap);
            Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0,
                    data.length, options);

            canvas.drawBitmap(cameraBitmap, nm, null);
            cameraBitmap.recycle();

            canvas.drawBitmap(bitmap, matrix, null);
            bitmap.recycle();

            File storagePath = new File(
                    Environment.getExternalStorageDirectory() + "/PhotoAR/");
            storagePath.mkdirs();

            File myImage = new File(storagePath, Long.toString(System
                    .currentTimeMillis()) + ".jpg");

            try {
                FileOutputStream out = new FileOutputStream(myImage);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                out.flush();
                out.close();

                final Bitmap bitmap = BitmapFactory.decodeFile(myImage.getPath(),
                        options);
                relativeLayout.setVisibility(View.GONE);
                preview.setVisibility(View.VISIBLE);
                preview.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                Log.d("In Saving File", e + "");
            } catch (IOException e) {
                Log.d("In Saving File", e + "");
            }

        }
    };

    public View createView(int index){

        //Main LinearLayout
        final RelativeLayout sub_layout = new RelativeLayout(this);
        sub_layout.setTag("sub_layout_"+index);
        sub_layout.setId(index);
        sub_layout.setBackgroundResource(R.drawable.image_border);
        sub_layout.setGravity(1);
        //sub_layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layout_379 = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout_379.gravity = Gravity.BOTTOM;
        sub_layout.setLayoutParams(layout_379);


        //Child RelativeLayout
        final RelativeLayout sub_relative_layout = new RelativeLayout(this);
        //  sub_relative_layout.setId(R.id.sub_relative_layout);
        sub_relative_layout.setTag("sub_relative_layout_"+index);
        sub_relative_layout.setId(index);
        sub_relative_layout.setGravity(RelativeLayout.CENTER_HORIZONTAL|RelativeLayout.CENTER_VERTICAL);
        //sub_relative_layout.setBackgroundResource(R.drawable.image_border);
        RelativeLayout.LayoutParams layout_892 = new RelativeLayout.LayoutParams((int) ((width/2)*0.7),
                (int) ((width/2)*0.7));
        sub_relative_layout.setLayoutParams(layout_892);
        sub_layout.addView(sub_relative_layout);
        layout_892.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layout_892.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layout_892.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);


        //relativeLayout's main imageView
        RelativeLayout.LayoutParams layout_329 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        // layout_329.addRule(RelativeLayout.BELOW, R.id.below_id);
        final ImageView img_main = new ImageView(this);
        img_main.setId(index);
        img_main.setTag("img_main_"+index);
        img_main.setScaleType(ImageView.ScaleType.FIT_XY);
        img_main.setLayoutParams(layout_329);
        // img_main.setImageResource(R.drawable.maxresdefault);
        Picasso.with(getApplicationContext()).load(Constant.image_url).into(img_main);
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
        sub_layout.addView(zoom_btn);


        //************** relativeLayout's close_btn imageView
        ImageView close_btn = new ImageView(this);
        close_btn.setId(index);
        close_btn.setTag("close_btn_"+index);
        close_btn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        RelativeLayout.LayoutParams layout_391 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_391.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layout_391.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layout_391.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        close_btn.setLayoutParams(layout_391);
        sub_layout.addView(close_btn);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_layout.removeView(sub_layout);
            }
        });
        sub_layout.setOnTouchListener(this);

        //************** relativeLayout's rotate_btn imageView
        ImageView rotate_btn = new ImageView(this);
        rotate_btn.setId(index);
        rotate_btn.setTag("rotate_btn_"+index);
        // rotate_btn.setImageResource();
        RelativeLayout.LayoutParams layout_551 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_551.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        layout_551.addRule(RelativeLayout.ALIGN_RIGHT, Integer.parseInt("close_btn_"+index));
        rotate_btn.setLayoutParams(layout_551);
        sub_layout.addView(rotate_btn);


        rotate_btn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                sub_layout.setRotation(sub_layout.getRotation() + 90);
            }
        });

        sub_relative_layout.setOnTouchListener(new MoveViewTouchListener(getApplication(), sub_layout));
        zoom_btn.setOnTouchListener(new ZoomViewTouchListner(getApplication(), sub_layout));


        //*********header options

        sub_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Toast.makeText(getApplicationContext(),"test", Toast.LENGTH_SHORT).show();
                setBorderColor(v, false);
                sub_layout.setOnTouchListener(new MoveViewTouchListener(getApplicationContext(), sub_layout));
            }
        });


        View view = sub_layout;

        return view;
    }

    public void setBorderColor(View view, boolean fromAdd){
        int strokeColor;
        for(int i=0;i<view_layout.getChildCount();i++){
            if(view_layout.getChildAt(i)== (fromAdd? view:view.getParent())){
                strokeColor = Color.parseColor("#00A3E5");
            }else{
                strokeColor = Color.LTGRAY;
            }
            Drawable background1 = view_layout.getChildAt (i).getBackground();
            if (background1 instanceof GradientDrawable) {
                // cast to 'GradientDrawable'
                GradientDrawable gradientDrawable = (GradientDrawable) background1;
                // gradientDrawable.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
                gradientDrawable.setStroke(4, strokeColor);
                view_layout.getChildAt(i).invalidate();
            }
        }
    }


}

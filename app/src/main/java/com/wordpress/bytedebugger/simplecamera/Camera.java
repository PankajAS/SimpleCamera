package com.wordpress.bytedebugger.simplecamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Camera extends Activity {

    private android.hardware.Camera mCamera = null;
    private CameraView mCameraView = null;
    private static final String IMAGE_DIRECTORY_NAME = "NG Design";
    ImageView imgeOnCamera, img_main, rotate_btn, close_btn, flip_btn,
            flip_oppposite, save_img, zoom_btn, imgPreview, share_btn, category_btn;
    FrameLayout frameLayout;
    SeekBar seekBar;
    Button take_pic, add_btn;
    LinearLayout option_layout, share_layout,linear_view_layout;
    View linearLayout;
    private ViewGroup mainLayout, view_layout, main_layout;
    private Matrix matrix = new Matrix();
    Bitmap bitmap = null;
    File cameraImage, galleyImage;
    String uri = "";
    GradientDrawable gd;
    int width;
    List<View> viewContainer = new ArrayList<>();
   // RelativeLayout sub_layout;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 123;

    @SuppressLint("WrongViewCast")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        setContentView(R.layout.activity_main);
        permissionForGallery();
        width = getWindowManager().getDefaultDisplay().getWidth();
     //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();
        Intent intent = getIntent();
        uri =intent.getStringExtra("Uri");

        //sub inflate layout
        //RelativeLayout linearLayout = (RelativeLayout)findViewById(R.id.relative_layout);

        mainLayout.setDrawingCacheEnabled(true);
        mainLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mainLayout.layout(0, 0, mainLayout.getMeasuredWidth(), mainLayout.getMeasuredHeight());
        mainLayout.buildDrawingCache(true);

        try{

            mCamera = android.hardware.Camera.open();
           // mainLayout.addView(createView(0));
            }
        catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {
            mCameraView = new CameraView(getApplicationContext(), mCamera);//create a SurfaceView to show camera data
            mainLayout.addView(mCameraView);//add the SurfaceView to the layout
            Constant.image_url = "http://torus.math.uiuc.edu/jms/Images/IMU-logo/transp/IMU-logo-wt.png";
            View view = createView(0);
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/2,width/2);
            view.setLayoutParams(param);
            view_layout.addView(view);
            ((RelativeLayout)view).setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        }

        take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(view);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCamera!=null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }

                Intent i = new Intent(getApplicationContext(), PreviewActivity.class);
                startActivityForResult(i, 1);
            }
        });

    }
    // setImage();




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            if(mCamera!=null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mCamera!=null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        super.onBackPressed();
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void saveImage(View view){

       for(int i=0;i<mainLayout.getChildCount();i++) {
            View view1 =  mainLayout.getChildAt(i);
            if(view1.getTag()!=null && view1.getTag().toString().indexOf("sub_layout_")!=-1) {
                ViewGroup viewGroup = (ViewGroup) view1;
               // RelativeLayout relativeView = (RelativeLayout) viewGroup.getChildAt(0);
                viewGroup.setBackground(null);
                for (int j = 0; j < viewGroup.getChildCount(); j++) {
                    if (viewGroup.getChildAt(j).getTag().toString().indexOf("sub_relative_layout_")==-1){
                        viewGroup.getChildAt(j).setVisibility(View.GONE);
                    }
                }
            }

        }
        bitmap = Bitmap.createBitmap(mainLayout.getDrawingCache());
        mainLayout.setDrawingCacheEnabled(true);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);


        if(mCamera!=null) {
            android.hardware.Camera.AutoFocusCallback mAutoFocusCallback = new android.hardware.Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, android.hardware.Camera camera) {
                    mCameraView.getCamera().takePicture(null, null, new android.hardware.Camera.PictureCallback() {
                        // @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {


                            Log.e("data", getTime());

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            //o.inJustDecodeBounds = true;
                            Bitmap cameraBitmapNull = BitmapFactory.decodeByteArray(data, 0,
                                    data.length, options);



                            int wid = options.outWidth;
                            int hgt = options.outHeight;
                            Log.e("options", hgt +" "+wid);
                            Log.e("bitmap", bitmap.getHeight() +" "+bitmap.getWidth());
                            Matrix nm = new Matrix();

                            android.hardware.Camera.Size cameraSize = camera.getParameters().getPictureSize();
                            Log.e("cameraSize", cameraSize.height +" "+cameraSize.width);
                            float ratio = imgPreview.getHeight() * 1f / cameraSize.height;
                            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                                nm.postRotate(90);
                                nm.postTranslate(hgt, 0);
                                wid = options.outHeight;
                                hgt = options.outWidth;
                                ratio = mainLayout.getWidth() * 1f / cameraSize.height;
                                Log.e("ratio", getTime());

                            } else {
                                wid = options.outWidth;
                                hgt = options.outHeight;
                                ratio = mainLayout.getHeight() * 1f / cameraSize.height;
                                Log.e("ratio", getTime());
                            }

                            float[] f = new float[9];
                            matrix.getValues(f);

                            f[0] = f[0] / ratio;
                            f[4] = f[4] / ratio;
                            f[5] = f[5] / ratio;
                            f[2] = f[2] / ratio;
                            matrix.setValues(f);
                            Log.e("matrix", getTime());

                            Bitmap newBitmap = Bitmap.createBitmap(wid, hgt,
                                    Bitmap.Config.ARGB_8888);

                            Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0,
                                    data.length, options);

                            Log.e("canvas", getTime());
                            Canvas canvas = new Canvas(newBitmap);
                            canvas.drawBitmap(cameraBitmap, nm, null);
                            cameraBitmap.recycle();

                            canvas.drawBitmap(bitmap, matrix, null);
                            bitmap.recycle();

                            Log.e("recycle", getTime());


                            File storagePath = new File(
                                    Environment.getExternalStorageDirectory() + "/NG Desings/");
                            storagePath.mkdirs();
                            Log.e("storagePath", getTime());
                            cameraImage = new File(storagePath, Long.toString(System
                                    .currentTimeMillis()) + ".jpg");
                            Constant.IMAGE_PATH = String.valueOf(cameraImage);
                            Constant.bitmap = newBitmap;
                            Log.e("cameraImage", getTime());

                          //  startActivity(new Intent(Camera.this, PreviewActivity.class));

                           try {
                                FileOutputStream out = new FileOutputStream(cameraImage);
                                newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

                                out.flush();
                                out.close();
                                mainLayout.setVisibility(View.GONE);
                               // option_layout.setVisibility(View.GONE);
                               // share_layout.setVisibility(View.VISIBLE);
                                imgPreview.setVisibility(View.VISIBLE);
                                BitmapFactory.Options option = new BitmapFactory.Options();

                                // downsizing image as it throws OutOfMemory Exception for larger
                                // images
                               // options.inSampleSize = 8;

                                final Bitmap bitmap = BitmapFactory.decodeFile(cameraImage.getPath(),
                                        options);
                              //  mCamera.stopPreview();
                             //   mCamera.startPreview();

                                imgPreview.setImageBitmap(bitmap);
                                // Constants.selection_from = "From Camera";


                            } catch (FileNotFoundException e) {
                                Log.d("In Saving File", e + "");
                            } catch (IOException e) {
                                Log.d("In Saving File", e + "");
                            }

                        }
                    });
                }
            };
            mCamera.autoFocus(mAutoFocusCallback);

        }else{
            galleyImage = new File(Environment.getExternalStorageDirectory() + File.separator + System
                    .currentTimeMillis()+".jpg");

            try {
                galleyImage.createNewFile();
                FileOutputStream fo = new FileOutputStream(galleyImage);
                fo.write(bytes.toByteArray());
                fo.close();
                mainLayout.setVisibility(View.GONE);
                option_layout.setVisibility(View.GONE);
                share_layout.setVisibility(View.VISIBLE);
                imgPreview.setVisibility(View.VISIBLE);
                BitmapFactory.Options option = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                // option.inSampleSize = 5;

                final Bitmap bitmap = BitmapFactory.decodeFile(galleyImage.getPath(),
                        option);
                imgPreview.setImageBitmap(bitmap);
               // Constants.selection_from = "From Gallery";
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private String getTime() {
        Long tsLong = System.currentTimeMillis()/1000;
        return tsLong.toString();
    }

    public void permissionForGallery(){
       try
       {
           if (ActivityCompat.checkSelfPermission(Camera.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
           } else {

           }

       }catch (Exception e) {
           e.printStackTrace();
       }

   }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {


                }
                else
                {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }

                break;
        }
    }



    private void setImage() {
//        Picasso.with(getApplicationContext()).load(Constants.image_url).into(img_main);
    }

    public void share_image(){
        final Intent intent = new Intent(     android.content.Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cameraImage!=null? cameraImage:galleyImage));
        intent.setType("image/png");
        startActivity(intent);
    }

    public void initView(){
       // option_layout =  (LinearLayout)findViewById(R.id.option_layout);
        //share_layout =  (LinearLayout)findViewById(R.id.share_layout);
        mainLayout = (RelativeLayout)findViewById(R.id.camera_view);
        view_layout  = (RelativeLayout)findViewById(R.id.view_layout);
        main_layout  = (FrameLayout)findViewById(R.id.main_layout);
        take_pic = (Button)findViewById(R.id.save_img);
        add_btn = (Button)findViewById(R.id.add_btn);
        imgPreview = (ImageView)findViewById(R.id.imgPreview);
        //seekBar = (SeekBar)findViewById(R.id.seekBar);
       // flip_btn = (ImageView)findViewById(R.id.flip_btn);
       // flip_oppposite = (ImageView)findViewById(R.id.flip_oppposite);
        //save_img = (ImageView)findViewById(R.id.save_img);
      //  imgPreview = (ImageView)findViewById(R.id.imgPreview);
       // share_btn = (ImageView)findViewById(R.id.share_btn);
       // category_btn = (ImageView)findViewById(R.id.categoryIcon);
        //add_btn = (ImageView)findViewById(R.id.add_btn);

               // Intent i = new Intent(getApplicationContext(), CategoryList.class);
              // startActivityForResult(i, 1);

               // Constants.selection_from = "From Add";
                //overridePendingTransition(R.anim.slide_in_left, R.anim.stay);

    }

    int viewTagCounter = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


     //   seekBar.setProgress(0);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if(mCamera==null){
                    mCamera = android.hardware.Camera.open();

                    mCameraView = new CameraView(getApplicationContext(), mCamera);
                    mainLayout.addView(mCameraView);

                }

                View newView = createView(viewTagCounter);
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/2,width/2);
                newView.setLayoutParams(param);
                view_layout.addView(newView);
                //((FrameLayout.LayoutParams)newView.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;

                setBorderColor(newView, true);
                viewTagCounter++;

/*
                    for (int i = 0; i < mainLayout.getChildCount(); i++) {
                        View view = mainLayout.getChildAt(i);
                        Log.e("view", view.toString());
                        if (view.getTag() != null && view.getTag().toString().indexOf("sub_layout_") != -1) {
                            Log.e("viewTag", view.getTag().toString());
                                view.bringToFront();
                        }
                    }
*/
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
                //sub_layout.setOnTouchListener(new MoveViewTouchListener(sub_layout));
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

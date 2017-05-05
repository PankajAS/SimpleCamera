package com.wordpress.bytedebugger.simplecamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {

    ImageView imgPreview;
    ListView img_list;
    List<String> imgList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        img_list = (ListView)findViewById(R.id.img_list);
        imgList = new ArrayList<>();
        imgList.add("https://cdn.pixabay.com/photo/2017/01/11/08/31/icon-1971128_960_720.png");
        imgList.add("http://www.pngpix.com/wp-content/uploads/2016/10/PNGPIX-COM-Android-PNG-Transparent-Image-2.png");
        imgList.add("http://icons.iconarchive.com/icons/igh0zt/ios7-style-metro-ui/256/MetroUI-Folder-OS-OS-Android-icon.png");
        imgList.add("http://www.android-hilfe.de/attachments/htc-tattoo/7476d1272541296-android-logo-android-logo.png");
        imgList.add("https://cdn.pixabay.com/photo/2017/01/11/08/31/icon-1971128_960_720.png");

        ArrayAdapter adapter = new ArrayAdapter(PreviewActivity.this, android.R.layout.simple_list_item_1, imgList);
        img_list.setAdapter(adapter);

        img_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Constant.image_url = imgList.get(i);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                //overridePendingTransition(R.anim.stay, R.anim.slide_down);
                finish();
            }
        });





    }
}

package com.example.imageimportdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==1)
        {
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                   getPhoto();
            }
        }
    }
     public void onClick(View view)
     {
         if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
         {
             requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
         }else {
             getPhoto();
         }

     }
    public void getPhoto(){


        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }

    TextView textViewDate;
    /*if (Intent.ACTION_SEND_MULTIPLE.equals(data.getAction())
            && Intent.hasExtra(Intent.EXTRA_STREAM)) {
        // retrieve a collection of selected images
        ArrayList<Parcelable> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        // iterate over these images
        if( list != null ) {
            for (Parcelable parcel : list) {
                Uri uri = (Uri) parcel;
                // TODO handle the images one by one here
            }
        }
    }

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==1 && data != null && resultCode==RESULT_OK){

            try {

                ImageView imageView=findViewById(R.id.imageView);
                List<Bitmap> bitmaps=new ArrayList<>();
                ClipData clipData=data.getClipData();

                //if clipdata > 0 ,then multiple i=selections...
                if (clipData !=null)
                {
                    for (int i=0;i<clipData.getItemCount();i++)
                    {
                        Uri imageUri=clipData.getItemAt(i).getUri();

                        InputStream is=getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap=BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap);

                        String realPath = getRealPathFromURI(imageUri);
                        File selectedFile = new File(realPath);
                        Date date = new Date(selectedFile.lastModified());
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

                    }
                }
                else {
                    Uri imageUri=data.getData();
                    InputStream is=getContentResolver().openInputStream(imageUri);

                    Bitmap bitmap=BitmapFactory.decodeStream(is);
                    bitmaps.add(bitmap);



                    //new code
                    // Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImg);
                    imageView.setImageBitmap(bitmap);
                  /*  String realPath = getRealPathFromURI(imageUri);

                    File selectedFile = new File(realPath);
                    Date date = new Date(selectedFile.lastModified());
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

                    textViewDate = (TextView) findViewById(R.id.textViewDate);
                    textViewDate.setText(time);
                    
                   */
                }


                Log.i("Image uploaded","success");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private String getRealPathFromURI(Uri selectedImg) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImg, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}

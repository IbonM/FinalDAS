package com.montoya.picedit.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.montoya.picedit.databinding.ActivityEditBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EditActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityEditBinding binding = ActivityEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        final Button bFilter = binding.bFilter;
        final Button bRotate = binding.bRotate;
        final Button bCut = binding.bCut;
        final Button bExit = binding.bExit;
        final Button bSave = binding.bSave;
        final ImageView pic = binding.imageView2;

        final Uri uri = getIntent().getParcelableExtra("uriImageEdit");

        Bitmap bitmap = null;
        if (uri != null) {
            try{
                Glide.with(EditActivity.this)
                        .load(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri))
                        .into(pic);
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                pic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final Bitmap original = bitmap;
        } else {
            bitmap = ((BitmapDrawable)pic.getDrawable()).getBitmap();
            pic.setImageBitmap(bitmap);
        }
        if (savedInstanceState!=null){
            Uri savedUri = savedInstanceState.getParcelable("imageUri");
            Glide.with(EditActivity.this)
                    .load(savedUri)
                    .into(pic);
        }

        final Bitmap original = bitmap;
        bFilter.setContentDescription("0");
        bRotate.setContentDescription("0");
        bCut.setContentDescription("0");

        pic.setImageBitmap(original);




        //Boton para aplicar filtros a la foto
        bFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Filtro blanco y negro
                if (bFilter.getContentDescription() == "0"){

                    Bitmap foto = ((BitmapDrawable)pic.getDrawable()).getBitmap();
                    int height = foto.getHeight();
                    int width = foto.getWidth();
                    Bitmap fotobyn = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(fotobyn);
                    Paint paint = new Paint();
                    ColorMatrix cm = new ColorMatrix();
                    cm.setSaturation(0);
                    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                    paint.setColorFilter(f);
                    c.drawBitmap(foto, 0, 0, paint);

                    pic.setImageBitmap(fotobyn);

                    bFilter.setContentDescription("1");
                }

                //Filtro negativo
                else if(bFilter.getContentDescription() == "1"){

                    resetFilter(pic,original,bRotate.getContentDescription().toString(),bCut.getContentDescription());

                    Bitmap foto = ((BitmapDrawable)pic.getDrawable()).getBitmap();
                    Bitmap inversion = foto.copy(Bitmap.Config.ARGB_8888, true);
                    int width = inversion.getWidth();
                    int height = inversion.getHeight();
                    int pixels = width * height;
                    // Get original pixels
                    int[] pixel = new int[pixels];
                    inversion.getPixels(pixel, 0, width, 0, 0, width, height);
                    int RGB_MASK = 0x00FFFFFF;
                    // Modify pixels
                    for (int i = 0; i < pixels; i++)
                        pixel[i] ^= RGB_MASK;
                    inversion.setPixels(pixel, 0, width, 0, 0, width, height);
                    pic.setImageBitmap(inversion);

                    bFilter.setContentDescription("2");


                }

                //Filtro rojo
                else if(bFilter.getContentDescription() == "2"){

                    resetFilter(pic,original,bRotate.getContentDescription().toString(),bCut.getContentDescription());

                    Bitmap foto = ((BitmapDrawable)pic.getDrawable()).getBitmap();
                    int height = foto.getHeight();
                    int width = foto.getWidth();
                    Bitmap fotoBin = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(fotoBin);
                    //https://stackoverflow.com/questions/5699810/how-to-change-bitmap-image-color-in-android
                    float[] colorTransform = {
                            0, 1f, 0, 0, 0,
                            0, 0, 0f, 0, 0,
                            0, 0, 0, 0f, 0,
                            0, 0, 0, 1f, 0};

                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(0f); //Remove Colour
                    colorMatrix.set(colorTransform); //Apply the Red

                    ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                    Paint paint = new Paint();
                    paint.setColorFilter(colorFilter);
                    canvas.drawBitmap(foto, 0, 0, paint);
                    pic.setImageBitmap(fotoBin);

                    bFilter.setContentDescription("3");
                }

                //Filtro amarillo
                else if(bFilter.getContentDescription() == "3"){

                    resetFilter(pic,original,bRotate.getContentDescription().toString(),bCut.getContentDescription());

                    Bitmap foto = ((BitmapDrawable)pic.getDrawable()).getBitmap();
                    int height = foto.getHeight();
                    int width = foto.getWidth();
                    Bitmap fotoBin = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(fotoBin);
                    //https://stackoverflow.com/questions/5699810/how-to-change-bitmap-image-color-in-android
                    float[] colorTransform = {
                            0, 1f, 0, 0, 0,
                            0, 0, 1f, 0, 0,
                            0, 0, 0, 0f, 0,
                            0, 0, 0, 1f, 0};

                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(0f); //Remove Colour
                    colorMatrix.set(colorTransform); //Apply the Red

                    ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                    Paint paint = new Paint();
                    paint.setColorFilter(colorFilter);
                    canvas.drawBitmap(foto, 0, 0, paint);
                    pic.setImageBitmap(fotoBin);

                    bFilter.setContentDescription("4");
                }

                //Filtro azul
                else if(bFilter.getContentDescription() == "4"){

                    resetFilter(pic,original,bRotate.getContentDescription().toString(),bCut.getContentDescription());

                    Bitmap foto = ((BitmapDrawable)pic.getDrawable()).getBitmap();
                    int height = foto.getHeight();
                    int width = foto.getWidth();
                    Bitmap fotoBin = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(fotoBin);
                    //https://stackoverflow.com/questions/5699810/how-to-change-bitmap-image-color-in-android
                    float[] colorTransform = {
                            0, 0f, 0, 0, 0,
                            0, 0, 1f, 0, 0,
                            0, 0, 0, 1f, 0,
                            0, 0, 0, 1f, 0};

                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(0f); //Remove Colour
                    colorMatrix.set(colorTransform); //Apply the Red

                    ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                    Paint paint = new Paint();
                    paint.setColorFilter(colorFilter);
                    canvas.drawBitmap(foto, 0, 0, paint);
                    pic.setImageBitmap(fotoBin);

                    bFilter.setContentDescription("5");
                }

                //Foto sin filtros
                else{

                    resetFilter(pic,original,bRotate.getContentDescription().toString(),bCut.getContentDescription());
                    bFilter.setContentDescription("0");

                }
            }
        });
        //Boton para rotar la imagen 90 grados
        bRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rotate(pic,1);
                int num = Integer.parseInt(bRotate.getContentDescription().toString());
                bRotate.setContentDescription(String.valueOf((num + 1)%4));
            }
        });
        //Boton para recortar la imagen a un cuadrado o deshacer el corte
        bCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap cropImg;
                Bitmap foto = ((BitmapDrawable)pic.getDrawable()).getBitmap();

                if(foto.getHeight()==foto.getWidth())
                {
                    if (original!=((BitmapDrawable)pic.getDrawable()).getBitmap())
                    {
                        cropImg = original;
                        pic.setImageBitmap(cropImg);
                        int times = Integer.parseInt(bRotate.getContentDescription().toString());
                        rotate(pic,times);
                        bCut.setContentDescription("0");
                        for (int i=0;i<6;i++)
                        {
                            bFilter.callOnClick();
                        }
                    }
                }
                else{
                    cut(pic);
                    bCut.setContentDescription("1");
                }

            }
        });
        //Boton de vuelta a la actividad principal sin guardar
        bExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic.setImageBitmap(original);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Uri uri = saveImageToInternalStorage(((BitmapDrawable)pic.getDrawable()).getBitmap(), "temporal.png");
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        intent.putExtra("uriImage", uri);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Uri uri = saveImageToInternalStorage(((BitmapDrawable)pic.getDrawable()).getBitmap(), "temporal.png");
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        intent.putExtra("uriImage", uri);
                        startActivity(intent);
                        finish();
                    }
                }


            }
        });
    }



    //Rota la imagen 90 grados por numero recibido
    public void rotate(ImageView pic, int times){
        Bitmap rotImg;
        Bitmap foto = ((BitmapDrawable)pic.getDrawable()).getBitmap();
        Matrix matrix = new Matrix();
        matrix.postRotate(90*times);
        rotImg = Bitmap.createBitmap(foto, 0, 0, foto.getWidth(), foto.getHeight(), matrix, true);
        pic.setImageBitmap(rotImg);
    }
    //Corta la imagen en un cuadrado
    public void cut(ImageView pic) {
        Bitmap cropImg;
        Bitmap foto = ((BitmapDrawable)pic.getDrawable()).getBitmap();
        if (foto.getHeight() > foto.getWidth()) {
            cropImg = Bitmap.createBitmap(foto, 0, (foto.getHeight() - foto.getWidth()) / 2, foto.getWidth(), foto.getWidth());
            pic.setImageBitmap(cropImg);
        } else{
            cropImg = Bitmap.createBitmap(foto, (foto.getWidth() - foto.getHeight()) / 2, 0, foto.getHeight(), foto.getHeight());
            pic.setImageBitmap(cropImg);
        }
    }
    //Devuelve la imagen al filtro original manteniendo rotacion y corte
    public void resetFilter(ImageView pic, Bitmap original,String rotate, CharSequence cut)
    {
        pic.setImageBitmap(original);
        int times = Integer.parseInt(rotate);
        rotate(pic,times);
        if (cut == "1")
        {
            cut(pic);
        }
    }



    Uri saveImageToInternalStorage(Bitmap bitmap, String id) {
        File file = new File(Environment.getExternalStorageDirectory() + "/PicEdit/");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file, id);

        try {
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();

        } catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Return the saved image Uri
        return Uri.parse(file.getAbsolutePath());
    }
}

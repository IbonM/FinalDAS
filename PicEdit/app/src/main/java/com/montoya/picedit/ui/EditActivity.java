package com.montoya.picedit.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.montoya.picedit.databinding.ActivityEditBinding;

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
        final Bitmap original = ((BitmapDrawable)pic.getDrawable()).getBitmap();
        bFilter.setContentDescription("0");
        bRotate.setContentDescription("0");
        bCut.setContentDescription("0");

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
                //Filtro rojo
                else if(bFilter.getContentDescription() == "1"){

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

                    bFilter.setContentDescription("2");
                }

                //Filtro amarillo
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

                    bFilter.setContentDescription("3");
                }

                //Filtro azul
                else if(bFilter.getContentDescription() == "3"){

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

                    bFilter.setContentDescription("4");
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

                    cropImg = original;
                    pic.setImageBitmap(cropImg);
                    int times = Integer.parseInt(bRotate.getContentDescription().toString());
                    rotate(pic,times);
                    bCut.setContentDescription("0");
                    for (int i=0;i<5;i++)
                    {
                        bFilter.callOnClick();
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
                finish();
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}

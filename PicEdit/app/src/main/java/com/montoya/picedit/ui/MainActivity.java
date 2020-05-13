package com.montoya.picedit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.montoya.picedit.R;
import com.montoya.picedit.databinding.ActivityMainBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

/**
 * Actividad principal que se carga solo para los usuarios logeados
 *
 * Aquí vamos a cargar en un recycler view todos los TODOs que el usuario tenga
 * Además vamos a tener la capacidad de crear nuevos TODOs, de marcarlos como terminados y de borrarlos
 * Desde aquí tambien vamos a mostrar en el menú la opción de ayuda con instrucciones para el usuario y la opción de perfil
 * para ver el perfil del usuario
 */
public class MainActivity extends AppCompatActivity {

    // Definición de variables
    private String userId;
    FirebaseFirestore db;

    ImageView profilePic;

    private final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private final int MY_PERMISSIONS_REQUEST_VIDEO = 4;
    static final int CODIGO_FOTO = 1;
    private static int VIDEO_REQUEST = 101;

    // Variables de User Interface
    ProgressBar progressBar;
    private EasyImage easyImage;
    private Uri uri;
    private Uri videoUri;
    private Button bEditar;
    private Button bShare;
    private int type=0;

    /**
     * Método OnCreate dónde obtenremos el usuario logeado, iniciaremos nuestros objetos de la ui y cargaremos los TODOs
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, 1);
            }
        }
        Log.i("AAAAAA","0");
        // Para estar aquí tenemos que estar logeados, sino nos salimos de la actividad
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // guardamos el id de usuario
            userId = user.getUid();

            // Binding para hacer referencia de nuestros objetos del XML
            ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);
            Button bPhoto = binding.bPhoto;
            Button bVideo = binding.bVideo;
            bEditar = binding.bEditar;
            bShare = binding.bUpload;
            Button bGaleria = binding.bGaleria;

            profilePic = binding.profilePic;


            uri = getIntent().getParcelableExtra("uriImage");

            //if (uri != null) {
                try {
                    File fileFolder = new File(Environment.getExternalStorageDirectory() + "/PicEdit/");
                    File file = new File(fileFolder, "temporal.png");
                    uri = Uri.fromFile(file);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    Glide.with(MainActivity.this)
                            .load(bitmap)
                            .into(profilePic);

                    bEditar.setVisibility(View.VISIBLE);
                    bShare.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            //}

            // Obtenemos instancia de base de datos
            db = FirebaseFirestore.getInstance();

            // Inicializamos objetos de UI
            progressBar = binding.progressBar2;

            progressBar.setVisibility(View.GONE);
            //Boton Camara
            bPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }

            });

            bVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    captureVideo();
                }

            });

            bGaleria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            //.setAspectRatio(1,1)
                            //.setFixAspectRatio(true)
                            .start(MainActivity.this);
                }
            });
            bEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                            }, 3);
                        } else {
                            edit();
                        }
                    }
                }
            });
            bShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type==0) { //COMPARTIR FOTOGRAFÍA
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/png");
                        share.putExtra(Intent.EXTRA_STREAM, uri);

                        File fileFolder = new File(Environment.getExternalStorageDirectory() + "/PicEdit/");
                        File file = new File(fileFolder, "temporal.png");
                        Uri apkURI = FileProvider.getUriForFile(
                                MainActivity.this,
                                MainActivity.this.getApplicationContext()
                                        .getPackageName() + ".provider", file);
                        share.setDataAndType(apkURI, "image/png");
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        startActivity(Intent.createChooser(share, "Share Image"));
                    }else{ //COMPARTIR VIDEO
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("video/mp4");
                        share.putExtra(Intent.EXTRA_STREAM, videoUri);
                        try{
                            startActivity(Intent.createChooser(share,"Share Video"));
                        }catch (android.content.ActivityNotFoundException ex){

                        }
                    }
                }
            });

        // Sino estamos logeados nos salimos
        } else {
            finish();
        }
    }

    /**
     * Método OnResume dónde revisaremos que el usuario este logeado y sino lo esta lo mandaremos a la actividad de login
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Para estar aquí tenemos que estar logeados, sino nos salimos de la actividad
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
    }

    /**
     * Con este método creamos el menú en el toolbar con el archivo de menu_main
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Método llamado al hacer click en algun item del menú
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Si da click en la opción perfil abrimos la actividad de perfil
        if (id == R.id.action_profile) {

            Intent intent = new Intent(this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finish();

            return true;

        // Si da click en ayuda desplegamos las instrucciones
        } else
            if (id == R.id.action_help) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.instructions)
                        .setMessage(getString(R.string.takepic) +
                                getString(R.string.edit_it) +
                                getString(R.string.share_it))
                        .setPositiveButton(android.R.string.yes, null).show();

                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    public void edit() {
        // Si da click en la opción perfil abrimos la actividad de perfil
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("uriImageEdit", uri);

        startActivity(intent);


    }

    //Metodo que se encarga de pedir permisos de camara y llamar a una app de fotografia
    protected void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

        } else {
            easyImage = new EasyImage.Builder(MainActivity.this)
                    .setCopyImagesToPublicGalleryFolder(false)
                    .setFolderName("EasyImage sample")
                    .allowMultiple(true)
                    .build();
            easyImage.openCameraForImage(MainActivity.this);

        }
    }

    protected void captureVideo(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_VIDEO);

        } else {
            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (videoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(videoIntent, VIDEO_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                takePicture();
            }
            else
            {
                Toast.makeText(this, R.string.cameradeny, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 3)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                edit();
            }
            else
            {
                Toast.makeText(this, R.string.editdeny, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_VIDEO)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                captureVideo();
            }
            else
            {
                Toast.makeText(this, R.string.cameradeny, Toast.LENGTH_SHORT).show();
            }
        }
    }



    //Al sacar la foto, si el codigo de respuesta es correcto, se recortará la foto con forma cuadrada y se trabajara con ella
    //En este momento la unica funcion implementada para la foto es mostrarla para comprobar que hasta ahi funciona correctamente
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Revisamos si es la petición de video
        if(requestCode==VIDEO_REQUEST && resultCode==RESULT_OK){
            videoUri= data.getData();
            Glide.with(MainActivity.this)
                    .load(videoUri)
                    .into(profilePic);

            type=1;
            bEditar.setVisibility(View.GONE);
            bShare.setVisibility(View.VISIBLE);

        }else {
            // Revisamos que sea nuestra petición con id CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                // Obtenemos el resultado
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                // Verificamos que el resultado sea correcto
                if (resultCode == RESULT_OK) {

                    // obtenemos el uri de la foto
                    final Uri resultUri = result.getUri();

                    uri = resultUri;

                    // En caso de que se guarde exitosamente la cargamos
                    Glide.with(MainActivity.this)
                            .load(resultUri)
                            .into(profilePic);

                    bEditar.setVisibility(View.VISIBLE);
                    type=0;
                    bShare.setVisibility(View.VISIBLE);
                }
            } else {
                easyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                    @Override
                    public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {
                        uri = Uri.parse(imageFiles[0].getFile().toURI().toString());

                        // En caso de que se guarde exitosamente la cargamos
                        Glide.with(MainActivity.this)
                                .load(uri)
                                .into(profilePic);

                        bEditar.setVisibility(View.VISIBLE);
                        type=0;
                        bShare.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                        //Some error handling
                        error.printStackTrace();
                    }

                    @Override
                    public void onCanceled(@NonNull MediaSource source) {
                        //Not necessary to remove any files manually anymore
                    }
                });

            }
        }
    }
}

package com.montoya.picedit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    static final int CODIGO_FOTO = 1;

    // Variables de User Interface
    ProgressBar progressBar;

    /**
     * Método OnCreate dónde obtenremos el usuario logeado, iniciaremos nuestros objetos de la ui y cargaremos los TODOs
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            Button bUpload = binding.bUpload;

            // Obtenemos instancia de base de datos
            db = FirebaseFirestore.getInstance();

            // Inicializamos objetos de UI
            progressBar = binding.progressBar2;

            progressBar.setVisibility(View.GONE);
            //Boton Camara
            bPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //takePicture();

                }

            });
            bVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edit();
                }
            });
            bUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


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
            startActivity(intent);
            return true;

        // Si da click en ayuda desplegamos las instrucciones
        } else
            if (id == R.id.action_help) {
                new AlertDialog.Builder(this)
                        .setTitle("Instrucciones")
                        .setMessage("Manten presionada un TODO para eliminarlo" +
                                "\n\nClick en el botón '+' para agregar TODOs" +
                                "\n\nClick en el switch para marcarlo como completado")
                        .setPositiveButton(android.R.string.yes, null).show();

                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    public void edit() {
        // Si da click en la opción perfil abrimos la actividad de perfil
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    //Metodo que se encarga de pedir permisos de camara y llamar a una app de fotografia
    protected void takePicture() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                takePicture();
            }
        } else {
            Intent elIntentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (elIntentFoto.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(elIntentFoto, CODIGO_FOTO);
            }
        }
    }

    //Al sacar la foto, si el codigo de respuesta es correcto, se recortará la foto con forma cuadrada y se trabajara con ella
    //En este momento la unica funcion implementada para la foto es mostrarla para comprobar que hasta ahi funciona correctamente
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_FOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap cropImg;
            Bitmap laminiatura = (Bitmap) extras.get("data");
            if (laminiatura.getHeight() > laminiatura.getWidth()) {
                cropImg = Bitmap.createBitmap(laminiatura, 0, (laminiatura.getHeight() - laminiatura.getWidth()) / 2, laminiatura.getWidth(), laminiatura.getWidth());
            } else if (laminiatura.getHeight() < laminiatura.getWidth()) {
                cropImg = Bitmap.createBitmap(laminiatura, (laminiatura.getWidth() - laminiatura.getHeight()) / 2, 0, laminiatura.getHeight(), laminiatura.getHeight());
            } else {
                cropImg = laminiatura;
            }
            //elImageView.setImageBitmap(cropImg);
        }

    }
}

package com.montoya.picedit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.montoya.picedit.databinding.ActivityProfileBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Locale;


/**
 * Actividad para visualizar la información del perfil de usuario actual
 *
 * En ella podremos cambiar la foto de perfil desde la cámara o la galería y guardarla en la nube
 * Podremos ver el nombre y correo electrónico del usuario
 * Y podremos cerrar sesión y volver a la pantalla de login
 */
public class ProfileActivity extends AppCompatActivity {

    // Variables de la ui
    private ImageView profilePic;
    private ProgressBar progressBar;
    NotificationManager elManager;
    NotificationCompat.Builder elBuilder;

    /**
     * Método OnCreate dónde inicializaremos las variables de la UI y obtendremos la información del usuario
     * desde nuestra base de datos de Firebase
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding para hacer referencia de nuestros objetos del XML
        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        // Para estar aquí tenemos que estar logeados, sino nos salimos de la actividad
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            // Creamos todas las referencias del UI e inicializamos
            progressBar = binding.progressBar;
            progressBar.setVisibility(View.GONE);

            profilePic = binding.profilePic;
            TextView changeProfilePic = binding.changeProfilePic;
            TextView name = binding.userName;
            TextView mail = binding.mail;
            TextView signOut = binding.logout;
            ImageButton bEsp = binding.bEsp;
            ImageButton bEng = binding.bEng;

            name.setText(user.getDisplayName());
            mail.setText(user.getEmail());

            // Obtenemos el uri de la foto de perfil guardada en firebase
            Uri profilePicUri = user.getPhotoUrl();

            // Solamente si existe una foto de perfil vamos a cargarla desde el uri
            if (profilePicUri != null) {
                String profilePicUrl = profilePicUri.toString();

                // Por default firebase nos da una foto de perfil muy chiquita asi que si es así cambiamos
                // el url para obtener una más grande
                if (profilePicUrl.contains("s96-c")) {
                    profilePicUrl = profilePicUrl.replace("s96-c", "s400-c");
                }

                // Usamos la librería Glide para cargar la foto en el ImageView
                Glide.with(this)
                        .load(profilePicUrl)
                        .circleCrop()
                        .into(profilePic);
            }

            // Ponemos un listener en el rofilepic para cambiar la foto de perfil
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launcheImagePicker();
                }
            });

            // Ponemos un listener en el label de cambiar foto para cambiar la foto de perfil
            changeProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launcheImagePicker();
                }
            });

            //Boton ESP (Cambiar idioma a espanol)
            bEsp.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                            .setContentTitle("Idioma cambiado")
                            .setContentText("La aplicacion se muestra ahora en castellano")
                            .setSubText("");
                    elManager.notify(1, elBuilder.build());
                    language("es");
                }
            });
            //Boton ENG (Cambiar idioma a ingles)
            bEng.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                            .setContentTitle("Language changed")
                            .setContentText("App is now shown in english")
                            .setSubText("");
                    elManager.notify(1, elBuilder.build());
                    language("en");
                }
            });
            //Sistema de notificaciones
            elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            elBuilder= new NotificationCompat.Builder(this, "ChannelID");
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
                NotificationChannel elCanal= new NotificationChannel("ChannelID", "ChannelName", NotificationManager.IMPORTANCE_DEFAULT);
                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setAutoCancel(true);
                elCanal.setDescription("Channel Description");
                elCanal.enableLights(true);
                elCanal.setLightColor(Color.RED);
                elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                elCanal.enableVibration(true);
                elManager.createNotificationChannel(elCanal);
            }

            // Ponemos un listener el el label de sign out para cerrar sesión
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Hacemos logout en la librería de autenticación de firebase
                    AuthUI.getInstance()
                            .signOut(ProfileActivity.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Cerramos Profile Activity para regresar a LoginActivity
                                    // Primero pasamos por MainActivity pero en el onResume checa que no esta
                                    // logueado y se regresa a Login
                                    finish();
                                }
                            });
                }
            });
        }
    }

    /**
     * Usamos la librería CircleCrop para iniciar una nueva actividad que nos dejará escojer entre
     * sacar una foto con la cámara y obtenerla de la galería. Ademas le pedimos proporciones de una
     * foto cuadrada con aspect ratio 1:1 que es lo que necesitamos para la foto de perfil.
     */
    private void launcheImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setFixAspectRatio(true)
                .start(this);
    }

    //Cambiar idioma de la aplicacion
    private void language(String lang){
        Resources res = getResources();
        Configuration con = res.getConfiguration();
        con.setLocale(new Locale(lang));
        res.updateConfiguration(con,res.getDisplayMetrics());
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /**
     * Obtenemos en OnActivityResult la foto del usuario cuadrada obtenida de la camara o la galería
     * Después la subimos a firebase para que se quede guardada en la nube
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Revisamos que sea nuestra petición con id CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            // Obtenemos el resultado
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            // Verificamos que el resultado sea correcto
            if (resultCode == RESULT_OK) {

                // Mostramos el progressbar
                progressBar.setVisibility(View.VISIBLE);

                // obtenemos el uri de la foto
                final Uri resultUri = result.getUri();

                // Obtenemos el usuario actual de firebase
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;

                //Generamos la peticion de cambio de foto
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(resultUri)
                        .build();


                // Guardar la nueva foto de perfil
                user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // En caso de que se giarde exitosamente la cargamos
                        Glide.with(ProfileActivity.this)
                                .load(resultUri)
                                .circleCrop()
                                .into(profilePic);

                        // Quitamos el progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                })
                // Agregamos un listener en caso de errores para informar al usuario
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Mostramos al usuario el error
                        Toast.makeText(ProfileActivity.this, "Error al subir foto", Toast.LENGTH_LONG).show();

                        // Quitamos el progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                });

            // En caso de error mostramos al usuario el error
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Error al recuperar foto", Toast.LENGTH_LONG).show();
            }
        }
    }
}

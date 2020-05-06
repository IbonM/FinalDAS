package com.montoya.picedit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.montoya.picedit.databinding.ActivityLoginBinding;
import java.util.Arrays;
import java.util.List;

/**
 * Launcher Activity para implementar la lógica de inicio de sesión, registro y recuperar contraseña
 * Esta actividad usa Firebase auth para manejar toda la lógica y permite iniciar sesión con google o por correo
 * Una vez el usuario se ha autenticado procedemos a abrir la Main Activity
 *
 * Toda la lógica de inicio de sesión se hace con ayuda de la librería FirebaseAuth, con ella guardamos en
 * la nube a los usuarios
 */
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 0;
    private ActivityLoginBinding binding;

    /**
     * Método on resume en donde revisamos si el usuario esta logeado o no
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Obtenemos el usuario actual y revisamos si estamos logeados
        //if (FirebaseAuth.getInstance().getCurrentUser() == null) {
          if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            // Llenamos los parametros de Firebase Auth para poder iniciar sesión con google y correo
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Iniciamos la nueva actividad de Firebase auth esperando un resultado de inicio de sesión
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);

        // Si ya estamos loggeados entonces cargamos el contenido principal
        } else {
            goToMainActivity();
        }
    }

    /**
     * Método OnCreate con los bindings para facilitar la conexión de objetos de la ui con el código
     * En esta actividad no los usamos
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    /**
     * Método para ir a la actividad principal
     */
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Método callback dónde obtenemos el resultado de inicio de sesión con Firebase Auth
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Revisamos que haya sido nuestra petición con el código RC_SIGN_IN
        if (requestCode == RC_SIGN_IN) {
            // Si se logeo correctamente seguimos
            if (resultCode == RESULT_OK) {
                // Aquí podríamos obtener el usuario
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Vamos a la actividad principal
                goToMainActivity();

            // Si no se inició sesión correctamente mostramos un mensaje
            } else {
                Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_LONG).show();
            }
        }
    }
}

package com.montoya.picedit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

            // Obtenemos instancia de base de datos
            db = FirebaseFirestore.getInstance();

            // Inicializamos objetos de UI
            progressBar = binding.progressBar2;

            progressBar.setVisibility(View.GONE);


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
}

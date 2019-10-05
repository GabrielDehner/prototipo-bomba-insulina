package ingenieria.insulinestats.Visual;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import ingenieria.insulinestats.Logica.ControladoraLogica;
import ingenieria.insulinestats.R;

import static java.lang.Thread.sleep;

public class InicioSesion extends AppCompatActivity {
    public static final int REGISTRO_OK = 1;
    EditText txtUsuario;
    EditText txtContrasenia;
    Button btnIniciarSesion;
    Button btnRegistrar;
    ProgressDialog pdProgreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        btnIniciarSesion = (Button) findViewById(R.id.btnIS);
        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtContrasenia = (EditText) findViewById(R.id.txtContrasenia);
        btnRegistrar = (Button) findViewById(R.id.btnReg);
        request = Volley.newRequestQueue(this);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesionWebService();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registro.class);
                startActivityForResult(intent, InicioSesion.REGISTRO_OK);
            }
        });
    }

    private void iniciarSesionWebService() {
        pdProgreso = new ProgressDialog(this);
        pdProgreso.setMessage("Iniciando Sesión...");
        pdProgreso.show();

        final String usuario = txtUsuario.getText().toString();
        final String pass = txtContrasenia.getText().toString();

        ControladoraLogica.iniciarSesionWebService(this, usuario, pass, new IniciarSesionResultCallback(){

            @Override
            public void onSuccess() {
                pdProgreso.hide();
                mostrarMensaje("Sesión iniciada");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("usuario", usuario);
                returnIntent.putExtra("pass", pass);
                setResult(ControladoraLogica.SESION_INICIADA, returnIntent);
                finish();
            }

            @Override
            public void onError() {
                pdProgreso.hide();
                mostrarMensaje("Usuario y/o contraseña incorrectos");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REGISTRO_OK) {
            String usuario = data.getStringExtra("usuario");
            String pass = data.getStringExtra("pass");

            Intent returnIntent = new Intent();
            returnIntent.putExtra("usuario", usuario);
            returnIntent.putExtra("pass", pass);
            setResult(ControladoraLogica.SESION_INICIADA, returnIntent);
            finish();
        }
    }

    private void mostrarMensaje(String msj){
        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show();
    }

    public interface IniciarSesionResultCallback {
        void onSuccess();
        void onError();
    }
}

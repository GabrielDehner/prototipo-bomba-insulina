package ingenieria.insulinestats.Visual;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.Date;

import ingenieria.insulinestats.Logica.ControladoraLogica;
import ingenieria.insulinestats.R;

public class Registro extends AppCompatActivity {
    ProgressDialog pdProgreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    EditText txtUsuario;
    EditText txtContrasenia;
    EditText txtNombre;
    EditText txtApellido;
    DatePicker dateFechaNacimiento;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtUsuario = (EditText) findViewById(R.id.txtIdUs);
        txtContrasenia = (EditText) findViewById(R.id.txtContrasenia);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtApellido = (EditText) findViewById(R.id.txtApellido);
        dateFechaNacimiento = (DatePicker) findViewById(R.id.dateFechaNacimiento);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);

        request = Volley.newRequestQueue(this);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarWebService();
            }
        });
    }
    private void registrarWebService() {
        pdProgreso = new ProgressDialog(this);
        pdProgreso.setMessage("Registrando...");
        pdProgreso.show();

        final String usuario = txtUsuario.getText().toString();
        final String pass = txtContrasenia.getText().toString();
        final String nombre = txtNombre.getText().toString();
        final String apellido = txtApellido.getText().toString();
        Calendar fechaNacimiento = Calendar.getInstance(); //se obtiene la fecha actual
        fechaNacimiento.set(dateFechaNacimiento.getYear(), dateFechaNacimiento.getMonth(), dateFechaNacimiento.getDayOfMonth());

        ControladoraLogica.registrarWebService(this, usuario, pass, nombre, apellido, fechaNacimiento, new registrarResultCallback(){
            @Override
            public void onSuccess() {
                pdProgreso.hide();
                mostrarMensaje("Se ha registrado exitosamente");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("usuario", usuario);
                returnIntent.putExtra("pass", pass);
                setResult(InicioSesion.REGISTRO_OK, returnIntent);
                finish();
            }

            @Override
            public void onError() {
                pdProgreso.hide();
                mostrarMensaje("No se pudo registrar");
            }
        });
    }

    private void mostrarMensaje(String msj){
        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show();
    }

    public interface registrarResultCallback {
        void onSuccess();
        void onError();
    }
}

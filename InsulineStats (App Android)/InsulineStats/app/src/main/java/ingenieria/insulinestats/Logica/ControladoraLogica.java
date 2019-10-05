package ingenieria.insulinestats.Logica;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ingenieria.insulinestats.Visual.InicioSesion;
import ingenieria.insulinestats.Visual.Principal;
import ingenieria.insulinestats.Visual.Registro;

import static java.lang.Thread.sleep;

public class ControladoraLogica extends AppCompatActivity implements Serializable {
    private int idControladora;
    private GestorComunicacion miGestorComunicacion;
    public static int SESION_INICIADA = 2;
    public static int CONECTAR_BLUETOOTH = 1;
    public static int NUMERO_INTENTO_CONEXION_BT = 0;
    private String mac;
    private Float periodicidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        miGestorComunicacion = new GestorComunicacion(1);

        if(existenDatosBomba()){
            //sincronizar datos
            almacenar_datos_nube(mac, periodicidad);
        }
        else{
            if(existenDatosUsuario()){
                //conectar bomba
                Intent intent = new Intent(this, Principal.class);
                startActivityForResult(intent, CONECTAR_BLUETOOTH);
            }
            else{
                //iniciar sesion
                Intent intent = new Intent(this, InicioSesion.class);
                startActivityForResult(intent, SESION_INICIADA);
            }
        }
    }

    private boolean existenDatosUsuario(){
        boolean existenDatos = false;

        SharedPreferences sharedPref = this.getPreferences(this.MODE_PRIVATE); //retorna el archivo de preferencias en modo privado
        String usuario = sharedPref.getString("usuario", "error");
        String pass = sharedPref.getString("pass", "error");
        System.out.println("Datos usuario recuperados");

        if(!(usuario.equals("error") || pass.equals("error"))){
            System.out.println("Datos usuario recuperados exitosamente");
            existenDatos = true;
        }

        return existenDatos;
    }

    private boolean existenDatosBomba() {
        boolean existenDatos = false;

        SharedPreferences sharedPref = this.getPreferences(this.MODE_PRIVATE); //retorna el archivo de preferencias en modo privado
        String id_bomba_almacenado = sharedPref.getString("id_bomba", "error");
        Float periodicidad_guardada = sharedPref.getFloat("periodicidad", -1);
        System.out.println("Datos bomba recuperados");

        if(!(id_bomba_almacenado.equals("error") || periodicidad_guardada == -1)){
            System.out.println("Datos bomba recuperados exitosamente");
            mac = id_bomba_almacenado;
            periodicidad = periodicidad_guardada;
            existenDatos = true;
        }

        return existenDatos;
    }

    private void almacenarDatosBomba(String id_bomba, Float periodicidad){
        SharedPreferences sharedPref = this.getPreferences(this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("id_bomba", id_bomba);
        editor.putFloat("periodicidad", periodicidad);
        editor.commit();
        System.out.println("Datos bomba guardados");
    }

    private void almacenarDatosUsuario(String usuario, String pass){
        SharedPreferences sharedPref = this.getPreferences(this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("usuario", usuario);
        editor.putString("pass", pass);
        editor.commit();
        System.out.println("Datos usuario guardados");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == ControladoraLogica.CONECTAR_BLUETOOTH){
            //almacenar datos nube
            mac = data.getStringExtra("MAC");
            periodicidad = Float.parseFloat(data.getStringExtra("PER"));
            this.almacenar_datos_nube(mac, periodicidad);
        }
        if(resultCode == ControladoraLogica.SESION_INICIADA){
            //almacenar datos usuario
            String usuario = data.getStringExtra("usuario");
            String pass = data.getStringExtra("pass");
            almacenarDatosUsuario(usuario, pass);

            //conectar bomba
            Intent intent = new Intent(this, Principal.class);
            startActivityForResult(intent, CONECTAR_BLUETOOTH);
        }
    }

    public void almacenar_datos_nube(String id_bomba, float periodicidad)   {
        System.out.println("Primer inicio");
        try {
            miGestorComunicacion.conectarDispositivo(mac, ControladoraLogica.this);
            almacenarDatosBomba(id_bomba, periodicidad); //se guarda recien aca porque asegura que el dispositivo ha sido el correcto (bomba)
            miGestorComunicacion.subirHistorialesDosis();

            int milisegundos = Math.round(periodicidad*60000); //convierte de minutos a milisegundos, y redondea a int
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Siguiente inicio");
                    FuncionParaEsteHilo();
                }
            }, milisegundos, milisegundos);

        } catch (IOException e) {
            //permite diferenciar excepciones del tipo "dispositivo incorrecto", y de "dispositivo fuera del alcance"
            if(existenDatosBomba()){
                //el usuario inició anteriormente, problema de "dispositivo fuera del alcance". No hacer nada.
                System.out.println("Dispositivo fuera del alcance");
                //de todos modos, si hubieran HistorialesDosis, éstos deben subirse
                miGestorComunicacion.subirHistorialesDosis();
            }
            else{
                //es el primer inicio, problema de "dispositivo incorrecto"
                System.out.println("Dispositivo incorrecto");
                mostrarErrorDispositivoIncorrecto();
            }
        }
    }

    private void mostrarErrorDispositivoIncorrecto() {
        AlertDialog.Builder alertBuilderSubirNube = new AlertDialog.Builder(this);
        alertBuilderSubirNube
                .setTitle("Dispositivo Incorrecto")
                .setMessage("Ha seleccionado un dispositivo bluetooth incorrecto")
                .setCancelable(false)
                .setPositiveButton("REINTENTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ControladoraLogica.this, Principal.class);
                        startActivityForResult(intent, CONECTAR_BLUETOOTH);
                    }
                });
        AlertDialog alertSubirNube = alertBuilderSubirNube.create();
        alertSubirNube.show();
    }

    private void FuncionParaEsteHilo() {
        //Esta función es llamada desde dentro del Timer
        //Para no provocar errores ejecutamos el Accion
        //Dentro del mismo Hilo
        this.runOnUiThread(Accion);
    }

    private Runnable Accion = new Runnable() {
        public void run() {
            //Aquí iría lo que queramos que haga,
            //en este caso mostrar un mensaje.
            System.out.println("ESTAMOS EN ACCION!!!");
            try {
                miGestorComunicacion.conectarDispositivo(mac, ControladoraLogica.this);
                miGestorComunicacion.subirHistorialesDosis();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public static void iniciarSesionWebService(Context context, String usuario, String pass, InicioSesion.IniciarSesionResultCallback callback){
        GestorComunicacion.iniciarSesionWebService(context, usuario, pass, callback);
    }

    public static void registrarWebService(Context context, String usuario, String pass, String nombre, String apellido, Calendar fechaNacimiento, Registro.registrarResultCallback callback) {
        GestorComunicacion.registrarWebService(context, usuario, pass, nombre, apellido, fechaNacimiento, callback);
    }
}

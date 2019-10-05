package ingenieria.insulinestats.Logica;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.UUID;

import ingenieria.insulinestats.Visual.InicioSesion;
import ingenieria.insulinestats.Visual.Registro;

public class GestorComunicacion {
    private int idGestor;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private HiloConexionBT miConexionBluetooth;
    private Handler bluetoothIn;
    private final int handlerState = 0;
    private StringBuffer DataStringIN = new StringBuffer();
    private LinkedList<HistorialDosis> miListaHistorialDosis = new LinkedList<HistorialDosis>();
    private static int ids=1;
    private int valor = 0;
    private static BluetoothSocket miSocket;
    private ControladoraLogica miControladoraLogica;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public GestorComunicacion(int idGestor) {
        this.idGestor = idGestor;
    }

    public void recuperarHistorialesDosis() {
        SharedPreferences sharedPref = miControladoraLogica.getPreferences(ControladoraLogica.MODE_PRIVATE); //retorna el archivo de preferencias en modo privado
        String json = sharedPref.getString("miListaHistorialDosis", "error");
        System.out.println("Historiales recuperados");

        if(!json.contains("error")){
            Type listType = new TypeToken<LinkedList<HistorialDosis>>() {}.getType();
            miListaHistorialDosis = new Gson().fromJson(json, listType);
            System.out.println("Historiales recuperados exitosamente");
        }
    }

    public void sincronizar(ControladoraLogica principal, String url){
        request = Volley.newRequestQueue(principal);
        runWebService(url);
    }

    private void runWebService(String url) {
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                valor = 1;
                System.out.println("Se registró exitosamente...");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                valor = -1;
                System.out.println("No funcionó "+error.toString());
            }
        });
        request.add(jsonObjectRequest);
    }

    public void conectarDispositivo(String mac, ControladoraLogica miControladoraLogica) throws IOException {
        this.miControladoraLogica = miControladoraLogica;
        recuperarHistorialesDosis();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice miDispositivo = mBluetoothAdapter.getRemoteDevice(mac);

        miSocket = miDispositivo.createRfcommSocketToServiceRecord(BTMODULEUUID);
        miSocket.connect(); //aca tira excepcion si ya hay un hilo conectado
        miConexionBluetooth = new HiloConexionBT(miSocket);
        miConexionBluetooth.start();

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        String otrosDatos = DataStringIN.substring(dataInPrint.length(), DataStringIN.length());
                        if (otrosDatos.length() > 1) {
                            dataInPrint = dataInPrint + otrosDatos;
                        }

                        dataInPrint = dataInPrint.replace("#", "");
                        String[] particiones = dataInPrint.split(":");

                        calcularHoraInicio(particiones);

                        for (int i = 0; i < particiones.length - 1; i++) {
                            particiones[i] = particiones[i].replaceAll("^\\s*", "");
                            String[] atributos = particiones[i].split("\r\n");
                            //int idHistorial, int hora, float dosis_insulina, float nvl_azucar

                            String hora = String.valueOf(atributos[0]).toString();//desp hacerlo bien

                            int dosis_insulina = Integer.parseInt(String.valueOf(atributos[1]).toString());

                            int nvl_azucar = Integer.parseInt(String.valueOf(atributos[2]).toString());

                            HistorialDosis miHistorialDosis = new HistorialDosis(ids, hora, dosis_insulina, nvl_azucar);
                            ids++;
                            miListaHistorialDosis.add(miHistorialDosis);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String horaFormateada =format.format(miHistorialDosis.getHora().getTime());

                            System.out.println(String.valueOf(atributos[0]).toString() + "  " + horaFormateada + "  " +
                                    miHistorialDosis.getDosis_insulina() + "  " + miHistorialDosis.getNvl_azucar() + "\n ------ \n");
                        }

                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }

            private void calcularHoraInicio(String[] particiones) {
                //mayor elemento de la trama, para recalcular el horario relativo del conjunto de datos recibido
                int segundoMayor = 0;
                for(int i = 0; i < particiones.length - 1; i++){
                    particiones[i] = particiones[i].replaceAll("^\\s*", "");
                    String[] atributos = particiones[i].split("\r\n");

                    int miSegundo = Integer.parseInt(String.valueOf(atributos[0]).toString());
                    System.out.println("**EL SEGUNDO ES**: " + miSegundo);
                    if(segundoMayor < miSegundo){
                        segundoMayor = miSegundo;
                    }
                }
                HistorialDosis.calcularHoraInicio(segundoMayor);
            }
        };

        System.out.println("Se manda un uno");
        miConexionBluetooth.write("1");
    }

    private void sobreescribirHistorialesDosis() {
        SharedPreferences sharedPref = miControladoraLogica.getPreferences(ControladoraLogica.MODE_PRIVATE); //retorna el archivo de preferencias en modo privado
        SharedPreferences.Editor editorsharedPref = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(miListaHistorialDosis);
        editorsharedPref.putString("miListaHistorialDosis", json);
        editorsharedPref.commit();
        System.out.println("Historiales sobreescritos exitosamente");
    }

    private void borrarHistorialesDosis(){
        SharedPreferences sharedPref = miControladoraLogica.getPreferences(ControladoraLogica.MODE_PRIVATE); //retorna el archivo de preferencias en modo privado
        SharedPreferences.Editor editorsharedPref = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson("error");
        editorsharedPref.putString("miListaHistorialDosis", json);
        editorsharedPref.commit();
        System.out.println("Historiales borrados exitosamente");
    }

    public void subirHistorialesDosis(){
        if(!miListaHistorialDosis.isEmpty()){
            Paciente miPaciente = new Paciente("generico", "generico");
            String url;
            HistorialDosis miHistorialRecorre;
            LinkedList<HistorialDosis>miNuevaListaHistorial = new LinkedList<HistorialDosis>();
            for(int pos = 0; pos < miListaHistorialDosis.size(); pos++) {
                miHistorialRecorre = miListaHistorialDosis.get(pos);
                
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String horaFormateada =format.format(miHistorialRecorre.getHora().getTime());

                url = "https://segurossisrd.000webhostapp.com/TPFINALINGE/" +
                        "wsJSONAniadirHistorial.php?nvl_azucar=" + miHistorialRecorre.getNvl_azucar() +
                        "&dosis_insulina=" + miHistorialRecorre.getDosis_insulina() +
                        "&hora=" + horaFormateada.toString()+
                        "&idPaciente=" + miPaciente.getUsuario();

                url = url.replace(" ", "%20");
                sincronizar(miControladoraLogica, url);
                if(valor < 0){
                    miNuevaListaHistorial.add(miHistorialRecorre);
                }
            }
            miListaHistorialDosis = miNuevaListaHistorial;

            //se persisten los datos no guardados
            if(!miListaHistorialDosis.isEmpty()){
                sobreescribirHistorialesDosis();
            }
            else{
                borrarHistorialesDosis();
            }

        }
    }

    public static void iniciarSesionWebService(Context context, final String usuario, final String pass, final InicioSesion.IniciarSesionResultCallback callback) {
        String url = "https://segurossisrd.000webhostapp.com/TPFINALINGE/wsJSONIniciarSesion.php?"+
                "idUsuario="+usuario+
                "&contrasenia="+pass;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Paciente miPaciente = new Paciente();
                        JSONArray json = response.optJSONArray("PacienteIni");
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = json.getJSONObject(0);

                            miPaciente.setUsuario(jsonObject.optString("idUsuario"));// los nombres entre parentesis son los
                            miPaciente.setContrasenia(jsonObject.optString("contrasenia"));// que devuelve la base de datos
                            if(miPaciente.getUsuario().equals(usuario)
                                    && miPaciente.getContrasenia().equals(pass)) {

                                callback.onSuccess();
                            }else{
                                callback.onError();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError();
                        System.out.println("Error al consultar usuario"+error.getMessage());
                    }
                }
        );
        RequestQueue request = Volley.newRequestQueue(context);
        request.add(jsonObjectRequest);
    }

    public static void registrarWebService(Context context, String usuario, String pass, String nombre, String apellido, Calendar fechaNacimiento, final Registro.registrarResultCallback callback) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String horaFormateada =format.format(fechaNacimiento.getTime());

        String url = "https://segurossisrd.000webhostapp.com/TPFINALINGE/wsJSONAniadirPaciente.php?" +
                "idUsuario=" + usuario+
                "&contrasenia=" + pass+
                "&nombre=" + nombre +
                "&apellido=" + apellido +
                "&fechaNacimiento=" + horaFormateada.toString();
                //+"&idBomba=" + idBomba; ESTE LINK DEBE MODIFICARSE (quitar idBomba)

        url = url.replace(" ", "%20");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess();
                        System.out.println("Exito al registrar usuario");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError();
                        System.out.println("Error al registrar usuario"+error.getMessage());
                    }
            }
        );

        RequestQueue request = Volley.newRequestQueue(context);
        request.add(jsonObjectRequest);
    }

    private class HiloConexionBT extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public HiloConexionBT(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes = 0;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            try {
                sleep(1000);
                bytes = mmInStream.read(buffer);
                String readMessage = new String(buffer, 0, bytes);
                // Envia los datos obtenidos hacia el evento via handler
                bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            } catch (IOException e) {
                System.out.println("Excepcion capturada en recibir mensajes");
            } catch (InterruptedException e) {
                System.out.println("Error en llamada a sleep");
                e.printStackTrace();
            }
            try {
                mmInStream.close();
                mmOutStream.close();
                miSocket.close();
                System.out.println("Socket cerrado");
            } catch (IOException e) {
                System.out.println("Error al cerrar socket");
                e.printStackTrace();
            }
        }

        //Envio de trama
        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e) {
                //si no es posible enviar datos se cierra la conexión
                //Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                //finish();
            }
        }

    }

    public void setMiControladoraLogica(ControladoraLogica miControladoraLogica) {
        this.miControladoraLogica = miControladoraLogica;
    }
}
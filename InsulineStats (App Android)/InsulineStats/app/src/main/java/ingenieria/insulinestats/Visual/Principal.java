package ingenieria.insulinestats.Visual;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import ingenieria.insulinestats.Logica.ControladoraLogica;
import ingenieria.insulinestats.R;

import static android.widget.Toast.LENGTH_SHORT;

public class Principal extends AppCompatActivity {
    static ArrayAdapter<String> adapterDispositivosDisponibles;
    private String mac;
    private BroadcastReceiver mReceiver;
    private EditText txtPeriodicidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        txtPeriodicidad = (EditText) findViewById(R.id.txtPeriodicidad);

        if(ControladoraLogica.NUMERO_INTENTO_CONEXION_BT > 0){
            obtenerDispositivosVinculados();
        } else{
            try {
                ControladoraLogica.NUMERO_INTENTO_CONEXION_BT++;
                consultarSubirNube();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void consultarSubirNube() throws InterruptedException {
        /*System.out.println("Llegooo! antes de sleep");
        Thread.sleep(15000);
        System.out.println("Llegooo! despues de sleep");*/
        AlertDialog.Builder alertBuilderSubirNube = new AlertDialog.Builder(this);
        alertBuilderSubirNube
                .setTitle("Almacenamimento Remoto")
                .setMessage("¿Desea almacenar los datos en el Database Server?")
                .setCancelable(false)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //El sistema pregunta si se desea vincular la aplicación (InsulineStats) con la bomba (SCIBIP)
                        consultarVincularBomba();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        salir();
                    }
                });
        AlertDialog alertSubirNube = alertBuilderSubirNube.create();
        alertSubirNube.show();
    }

    public void consultarVincularBomba(){
        AlertDialog.Builder alertBuilderVincularBomba = new AlertDialog.Builder(this);
        alertBuilderVincularBomba
                .setTitle("Vinculación de InsulineStats con SCIBIP")
                .setMessage("¿Desea vincularse con la bomba de insulina?")
                .setCancelable(false)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //El sistema pide permiso para habilitar el módulo bluetooth
                        consultarHabilitarBluetooth();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        salir();
                    }
                });
        AlertDialog alertVincularBomba = alertBuilderVincularBomba.create();
        alertVincularBomba.show();
    }

    public void consultarHabilitarBluetooth(){
        BluetoothAdapter mBtAdapter;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null){
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", LENGTH_SHORT);
        }else{
            if(mBtAdapter.isEnabled()){
                obtenerDispositivosVinculados();
            }else{
                //solicita al usuario que active el bt
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            System.out.println("onActivityResult");
            obtenerDispositivosVinculados();
        }
    }

    private void obtenerDispositivosVinculados(){
        adapterDispositivosDisponibles = new ArrayAdapter<String>(this, R.layout.bluetooth_list_item);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //mBluetoothAdapter.startDiscovery();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //adiciona un disp previo emparejado al array
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                adapterDispositivosDisponibles.add(device.getName() + "\n" + device.getAddress());
            }
        }

        ListView listViewDispositivos = (ListView) findViewById(R.id.listViewDispositivos);
        listViewDispositivos.setOnItemClickListener(listViewOnClickListener);
        listViewDispositivos.setAdapter(adapterDispositivosDisponibles);
    }

    //cuando se presiona un elemento del ListView
    private AdapterView.OnItemClickListener listViewOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Obtener la dirección MAC del dispositivo
            String[] info = ((TextView) v).getText().toString().split("\n");
            System.out.println(info[1]);
            mac = info[1];

            //confirmar
            confirmarDispositivo();
        }
    };

    private void confirmarDispositivo(){
        AlertDialog.Builder alertBuilderVincularBomba = new AlertDialog.Builder(this);
        alertBuilderVincularBomba
                .setTitle("Confirmar dispositivo bluetooth")
                .setMessage("¿Desea vincularse con el dispositivo seleccionado?")
                .setCancelable(false)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //devuelve la MAC a ControladoraLogica
                        ///controlar que ingrese valor de periodicidad
                        if(txtPeriodicidad.getText().toString().length() > 0 ) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("MAC", mac);
                            returnIntent.putExtra("PER", txtPeriodicidad.getText().toString());
                            setResult(ControladoraLogica.CONECTAR_BLUETOOTH, returnIntent);
                            finish();
                        }else{
                            Toast.makeText(Principal.this, "Debe ingresar el período.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //no hace nada
                    }
                });
        AlertDialog alertVincularBomba = alertBuilderVincularBomba.create();
        alertVincularBomba.show();
    }

    public void salir(){
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}

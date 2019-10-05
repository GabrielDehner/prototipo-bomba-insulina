/*
 * Se incluye la libreria que incluye el protocolo UART RS232, y todas aquellas otras clases requeridas
 */
#include <SoftwareSerial.h> 
#include "Clases.h"
/*
 *  Tiempo de "testeo" de azucar en sangre
 */
#ifndef TIEMPO_DELAY_V
#define TIEMPO_DELAY_V
const int TIEMPO_DELAY = 5000;
#endif //TIEMPO_DELAY_V

/*
 * Se incluye libreria para el manejo de memoria EEPROM del microcontrolador
 */
#ifndef EEPROM_H
#define EEPROM_H
#include <EEPROM.h>
#endif //EEPROM_H
/*
 * Definicion de los objetos a utilizar y variables requeridas
 */
Dosis miDosis;
AzucarEnSangre miAzucar;
Estado miEstado;
HardwareBomba miHardwareBomba;
HistorialDosis miHistorialDosis;
ReservorioInsulina miReservorioInsulina;
Scibip miScibip;
boolean error;
int dosisAplicar;
int id=0;

void setup() {
/*
 * Establecimiento de parametros requeridos antes del inicio del blucle infinito
 */  
  Serial.begin(9600);
  miDosis.setValues();
  miReservorioInsulina.setValues();
  miEstado.setValues();
}

void loop() {
/*
 * Comienzo del programa
 */
 
  if(miEstado.get_nombre_estado() == "EJECUTANDO" || miEstado.get_nombre_estado() == "ALERTA"){
/*
 *Se actualizan los niveles
 */     
    miAzucar.actualizar_niveles();
    id++;
    if(miReservorioInsulina.get_insulina_disponible() >= miDosis.get_dosis_unitaria_max()){  
      error = miDosis.dosisDiariaMaxSuperada(); 
      if(error == false){
/*
 * Calculo de dosis
 */       
        dosisAplicar= miDosis.calcular_dosis(miAzucar.get_nvl_azucar0(),miAzucar.get_nvl_azucar1(),miAzucar.get_nvl_azucar2(),miAzucar.get_tasa1(),miAzucar.get_tasa2());  
        if(dosisAplicar == -1){
          miHardwareBomba.sonar_alarma();
          miHardwareBomba.mensajeAlerta("Azucar Baja");
          delay(1000);
          miEstado.estadoAlerta();
          dosisAplicar = 0;
        }
        error = miDosis.dosisDiariaMaxPorSuperar(); //si ya la tiene la 
        if(error == true){
          dosisAplicar = miDosis.calcular_dosis_minima();
          miEstado.estadoAlerta(); 
          miHardwareBomba.sonar_alarma();
        }else{
              if(dosisAplicar >= miDosis.get_dosis_unitaria_max()){
                  miDosis.set_dosisAplicar(miDosis.get_dosis_unitaria_max());
                  dosisAplicar = miDosis.get_dosisAplicar();////
              }
        }
        miReservorioInsulina.suministrar_insulina(dosisAplicar);
        miDosis.suministrar_insulina();   
/*
 * Se crean historiales para posteriormente un intercambio de datos con InsulineStats
 */
        id = miHistorialDosis.create_historial_dosis(id, dosisAplicar, miAzucar.get_nvl_azucar2());      
        
        miHardwareBomba.mostrar_ultima_dosis(dosisAplicar, miAzucar.get_nvl_azucar2());
        error = miReservorioInsulina.nivelInsulinaBajo(miDosis.get_dosis_unitaria_max()); 
        if(error == true){
           miEstado.estadoAlerta();
           miHardwareBomba.mensajeAlerta("Insulina Baja!");
        }  
      }else{
        miEstado.estadoError();
        miHardwareBomba.mensajeError("Dosis diaria excedida!");
      }
    }else{
/*
 * Hubo un error con el total de la dosis diaria suministrada
 */      
      id = miHistorialDosis.create_historial_dosis(id, 0, miAzucar.get_nvl_azucar2());
      delay(TIEMPO_DELAY);
      miHardwareBomba.mensajeAlerta("Nivel insulina menor a la dosis unitaria maxima");
    }
  }
/*
 * Se testea si hay una solicitud de conexion de InsulineStats.
 */ 
  miScibip.conexionEnvio(id);
}


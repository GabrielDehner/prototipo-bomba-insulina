#ifndef TIEMPO_DELAY_V
#define TIEMPO_DELAY_V
const int TIEMPO_DELAY = 5000;
#endif //TIEMPO_DELAY_V

#ifndef EEPROM_H
#define EEPROM_H
#include <EEPROM.h>
#endif //EEPROM_H


struct Historial1 { 
  byte idHistorial; 
  int dosis_insulina;
  int nvl_azucar;
  byte escritura;
};

  
class Scibip{ 
     private:
      String atributoInstanciaDatos = "";
     public:
      Scibip(){}
      ///metodo que busca los datos en memoria y envia los datos
      
      void conexionEnvio(int id){

        int times = 0;
        boolean salir = false;
        while((times*100) < TIEMPO_DELAY && !salir){
          if(Serial.available()){
            

            int compara = 0;
                Historial1 miHist; 
                
                

            while(compara < id){
              //delay(100);
              //Serial.println("BUCLEEE");
              if(Serial.available()){
              EEPROM.get(compara*sizeof(Historial1), miHist);
                           // Serial.println("BUCLEEE IF");
              if(miHist.escritura == 0){
                
                  Serial.println(miHist.idHistorial);
                  Serial.println(miHist.dosis_insulina);
                  Serial.println(miHist.nvl_azucar);
                  Serial.println(":");
                  //Serial.println(miHist.escritura);
                  miHist.escritura = 1; //marcado desde donde recorrer.. marcado = 65;
                  
                  if(Serial.available()){//necesitaria a mande un dato
                    EEPROM.put(compara*sizeof(Historial1), miHist);
                  }


              }

              compara++;

            }
           }

            Serial.println("#");
            salir = true;
          }
          times++;
          delay(100);
        }
        for(int i = times; (i*100) < TIEMPO_DELAY; i++){
          delay(100);  
        }
       
      }

};

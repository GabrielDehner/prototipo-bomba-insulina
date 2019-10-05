#ifndef VISUAL_H
#define VISUAL_H
#include "Visual.h"
#endif // VISUAL_H

#ifndef DOSIS_H
#define DOSIS_H
#include "Dosis.h"
#endif //DOSIS_H

class HardwareBomba{ 
       private:
          int lectura = A0;
          int alarma = 13; 
          Visual miVisual;
          Dosis miDosis;
       public:
          HardwareBomba(){
              pinMode(alarma, OUTPUT);   
          }
          int get_lectura(){
              return establecer_en_rango(analogRead(lectura));
          }
          void suministrar_insulina(int dosisAplicar){
              miDosis.suministrar_insulina();
          }
          void sonar_alarma(){
              for(int i = 0; i < 3; i++){
                digitalWrite(alarma, HIGH);  
                delay(500);
                digitalWrite(alarma, LOW); 
                delay(100);
              }   
          }
          void mensajeAlerta(String mensajeAlerta){
            miVisual.mensajeAlerta(mensajeAlerta);            
          }
          void mensajeAlertaInsulina(String mensajeAlerta){
              miVisual.mensajeAlerta(mensajeAlerta);
            }
          void mensajeError(String mensajeError){
             miVisual.mensajeAlerta("Dosis diaria excedida!");
          }    
          void mostrar_ultima_dosis(int dosisAplicar){
            String msj = "Ultima Dosis: ";
            msj.concat(dosisAplicar);
            miVisual.mensajeAlerta(msj);
          }
          void mostrar_ultima_dosis(int dosisAplicar, int nvl2){
            miVisual.mensajeNvls(dosisAplicar, nvl2);
          }
          int establecer_en_rango(int valor_leido){
            return((int)(valor_leido/(1023/22)));
          }

};


#ifndef DOSIS_H
#define DOSIS_H
#include "Dosis.h"
#endif //DOSIS_H

#ifndef HARDWAREBOMBA_H
#define HARDWAREBOMBA_H
#include "HardwareBomba.h"
#endif //HARDWAREBOMBA_H

class ReservorioInsulina{ 
       private:
          int capacidad;
          int insulina_disponible;
          //int lectura_reservorio;
          int aguja;
          Dosis miDosis;
          HardwareBomba miHardwareBomba;
       public:
          ReservorioInsulina(){}
          void setValues(){
            capacidad=100;
            //lectura_reservorio = A1;
            aguja = 12;
            insulina_disponible= capacidad;//establecer_en_rango(analogRead(lectura_reservorio));
         }
          void suministrar_insulina(int dosisAplicar){
              insulina_disponible = insulina_disponible - dosisAplicar;
              pinMode(aguja, OUTPUT);
              
              for(int i = 0; i < dosisAplicar; i++){
                digitalWrite(aguja, HIGH);
                delay(500);
                digitalWrite(aguja, LOW);
                delay(250);
              }
            }
          boolean nivelInsulinaBajo(int dosis_unitaria_max){
              boolean error = true;
              if(insulina_disponible < (dosis_unitaria_max * 4)){
                  error =true;
              }else{
                  error = false;
              }
              return error;
          }

          /*int establecer_en_rango(int valor_leido){
            return((int)(valor_leido/(1023/100)));
          }*/
          int get_insulina_disponible(){
            return insulina_disponible;
          }
};

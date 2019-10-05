#ifndef AZUCARENSANGRE_H
#define AZUCARENSANGRE_H
#include "AzucarEnSangre.h"
#endif //AZUCARENSANGRE

class Dosis{ 
       private:
          int min_seguro;
          int max_seguro;
          int dosis_diaria_max;
          int dosis_unitaria_max;
          int dosis_minima;
          int dosis_acumulada;
          int dosisAplicar=0;
          ///da error al usar el azucar en sangre
       public:
          Dosis(){}
          void setValues(){
              dosisAplicar = 0;
              min_seguro=6;
              max_seguro=14;
              dosis_diaria_max=25;
              dosis_unitaria_max=4;
              dosis_minima=1;
              dosis_acumulada=0;
            }
          int getDosisMinima(){
              return dosis_minima;
            }
          int calcular_dosis(int nvl0, int nvl1, int nvl2, int tasa1, int tasa2 ){ 
              if(nvl2 < min_seguro){//azucar baja
                  dosisAplicar = azucar_baja();
              }else if(nvl2 >= min_seguro && nvl2 <= max_seguro){//azucar ok
                  dosisAplicar = azucar_ok(nvl0, nvl1, nvl2, tasa1, tasa2);
              }else if(nvl2 > max_seguro){//azucar alta
                  dosisAplicar = azucar_alta(nvl0, nvl1, nvl2, tasa1, tasa2);
              }
              return dosisAplicar;
            }
          int redondear_abajo(){
            }
          void suministrar_insulina(){
            dosis_acumulada = dosis_acumulada + dosisAplicar;    
          }
          boolean dosisDiariaMaxPorSuperar(){
              boolean error = true;
              if((dosis_acumulada + dosisAplicar)>dosis_diaria_max){
                error = true;
              }else{
                error = false;
              }
              return error;
            }
          boolean dosisDiariaMaxSuperada(){
              boolean error = true;
              if(dosis_acumulada > dosis_diaria_max){//creo q no va el igual del esquema
                error = true;
              }else{
                error = false;
              }
              return error;
            }
          int calcular_dosis_minima(){
              dosisAplicar = dosis_diaria_max - dosis_acumulada;
              return dosisAplicar;
            }

          int azucar_baja(){
              return -1;
          }
          int azucar_ok(int nvl0, int nvl1, int nvl2, int tasa1, int tasa2){
            //Nivel de azucar estable o disminuyendo...
            int dosis_calculada;
            if(nvl2 < nvl1){
                dosis_calculada = 0;
            }
            //Nivel de azúcar aumentando, pero tasa de aumento disminuyendo  
            if(nvl2 > nvl1 && tasa2 < tasa1){
                dosis_calculada = 0;
            }
            //Nivel azucar aumentando y la tasa de aumento incrementando, si se redondea
            //a 0 al dividir por 4, suministrar dosis minima...
            if(nvl2 > nvl1 && tasa2 >= tasa1 &&(redondear_abajo(tasa2) == 0)){
                dosis_calculada = dosis_minima;
            }
            if(nvl2 > nvl1 && tasa2 >= tasa1 &&(redondear_abajo(tasa2) > 0)){
                dosis_calculada = redondear_abajo(tasa2);
            }
              return dosis_calculada;
          }
          
          int azucar_alta(int nvl0, int nvl1, int nvl2, int tasa1, int tasa2){
            int dosis_calculada;
            ///El nivel de azucar aumenta. Redondear hacia abajo si está por debajo de una
            ///unidad.
            if(nvl2 > nvl1 && redondear_abajo(tasa2) == 0){
              dosis_calculada = dosis_minima;
            }
            if(nvl2 > nvl1 && redondear_abajo(tasa2) > 0){
              dosis_calculada = redondear_abajo(tasa2);
            }
            //El nivel de azucar es estable
            if(nvl2 == nvl1){
              dosis_calculada = dosis_minima;
            }
            //Nivel de azucar disminuye y la tasa de disminucion aumenta
            if(nvl2 < nvl1 && tasa2 <= tasa1){
              dosis_calculada = 0;
            }
            //El nivel de azucar disminuye y la tasa de disminucion decrementa
            if(nvl2 < nvl1 && tasa2> tasa1){
              dosis_calculada = dosis_minima;
            }
            return dosis_calculada;
          }
            
          int redondear_abajo(int tasa2){
              int bandera = 0;
              float redondeo;
              redondeo = (float)(tasa2/4);
              bandera = (int) redondeo;
              return bandera;           
            }
            int get_dosis_unitaria_max(){
              return dosis_unitaria_max;  
            }
            void set_dosisAplicar(int nueva_dosis){
              dosisAplicar = nueva_dosis;  
            }
            int get_dosisAplicar(){
              return dosisAplicar;  
            }
};

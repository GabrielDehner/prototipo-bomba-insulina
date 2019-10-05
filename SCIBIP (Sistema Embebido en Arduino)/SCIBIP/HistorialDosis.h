#ifndef DOSIS_H
#define DOSIS_H
#include "Dosis.h"
#endif //DOSIS_H

#ifndef TIEMPO_DELAY_V
#define TIEMPO_DELAY_V
const int TIEMPO_DELAY = 5000;
#endif //TIEMPO_DELAY_V

#ifndef EEPROM_H
#define EEPROM_H
#include <EEPROM.h>
#endif //EEPROM_H


struct Historial { 
  byte idHistorial; 
  int dosis_insulina;
  int nvl_azucar;
  byte escritura;
};


class HistorialDosis{ 
       private:
          int idHistorial;
          int hora;
          int dosis_insulina;
          int nvl_azucar;
          Dosis miDosis;
       public:
          HistorialDosis(){}
          int create_historial_dosis(int id, int dosisAplicar, int nvl_azucar2){
              if(id < 144){
                Historial miHist;
                miHist.idHistorial= id;
                miHist.dosis_insulina= dosisAplicar;
                miHist.nvl_azucar = nvl_azucar2;
                miHist.escritura = 0;
                int direccion=(id-1)*sizeof(Historial);
                EEPROM.put(direccion, miHist);  
              }else{ 
                id = 0;
              }
              return id;
            }
};

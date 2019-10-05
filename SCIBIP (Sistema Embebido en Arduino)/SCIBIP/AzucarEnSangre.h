#ifndef HARDWAREBOMBA_H
#define HARDWAREBOMBA_H
#include "HardwareBomba.h"
#endif //HARDWAREBOMBA_H

class AzucarEnSangre{ 
       private:
          int nvl_azucar0=0;
          int nvl_azucar1=0;
          int nvl_azucar2=0;
          int tasa1=0;
          int tasa2=0;  
          HardwareBomba miHardwareBomba;    
       public:
          AzucarEnSangre(){}
          void actualizar_niveles(){
              int nuevaLectura;
              nuevaLectura = miHardwareBomba.get_lectura();
              nvl_azucar0 = nvl_azucar1;
              nvl_azucar1 = nvl_azucar2;
              nvl_azucar2 = nuevaLectura;
              tasa1= nvl_azucar1-nvl_azucar0;
              tasa2= nvl_azucar2-nvl_azucar1;
            }
          int get_nvl_azucar0(){
              return nvl_azucar0;
            }
          int get_nvl_azucar1(){
              return nvl_azucar1;
            }
          int get_nvl_azucar2(){
              return nvl_azucar2;
            }
          int get_tasa1(){
              return tasa1;
            }
          int get_tasa2(){
              return tasa2;
            }
};

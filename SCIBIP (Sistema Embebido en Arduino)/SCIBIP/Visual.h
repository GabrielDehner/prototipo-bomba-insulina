#include <LiquidCrystal.h>
const int rs = 7, en = 6, d4 = 5, d5 = 4, d6 = 3, d7 = 2;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

class Visual{ 
	   private:
       public:
          Visual(){   
          }
          void setValues(){
            lcd.begin(16, 2);
          }
          void mostrar_ultima_dosis(int dosisAplicar){ 
          }
          void mensajeAlerta(String mensaje){
              int ii = 0;
              int strLength;
              String toShow;
              int terminar = 0;
              strLength = mensaje.length();
              setValues();
              if(strLength > 16){
                while(ii <= (strLength-16)){
                  lcd.home();
                  toShow = mensaje.substring(ii,ii+16);
                  lcd.print(toShow);
                  ii = ii + 2; 
                  delay(500);
                }
              }else{
                lcd.print(mensaje);
              }
            }
          void mensajeError(String mensaje){
            mensajeAlerta(mensaje);
          }
          void mensajeNvls(int dosisAplicar, int nvl2){
            String msj;
            setValues();
            lcd.clear();
            msj = "Ultima Dosis: ";
            msj.concat(dosisAplicar);
            lcd.setCursor (0, 0);
            lcd.print(msj);
            
            msj = "Nvl Azucar: ";
            msj.concat(nvl2);
            lcd.setCursor (0, 1);
            lcd.print(msj);
            delay(500);
            
          }

            
};

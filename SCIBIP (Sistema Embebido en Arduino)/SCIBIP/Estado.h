class Estado{ 
       private:
          String nombre_estado= "EJECUTANDO";
          
       public:
          Estado(){}
          void setValues(){
              nombre_estado= "EJECUTANDO";
            }
          void estadoAlerta(){
              nombre_estado = "ALERTA";
            }
          void estadoError(){
              nombre_estado = "ERROR";
            }
          String get_nombre_estado(){
            return nombre_estado;  
          }

};

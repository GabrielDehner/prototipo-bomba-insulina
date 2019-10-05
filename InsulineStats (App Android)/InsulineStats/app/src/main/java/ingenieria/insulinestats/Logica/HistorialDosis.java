package ingenieria.insulinestats.Logica;

import java.util.Calendar;

/**
 * Created by mejor on 13/2/2018.
 */

public class HistorialDosis implements Cloneable {
    private int idHistorial;
    private Calendar hora;
    private int dosis_insulina;
    private int nvl_azucar;
    private static Calendar horaInicio = null;
    private static int banderaPrimerHistorial = 1;
    private static int frecuenciaHistoriales = 5;//10*60; //se genera un historial en SCIBIP cada 10*60 segundos

    public HistorialDosis() {
    }

    public HistorialDosis(int idHistorial, String hora, int dosis_insulina, int nvl_azucar) {
        this.idHistorial = idHistorial;
        this.setHora(Integer.parseInt(hora));
        this.dosis_insulina = dosis_insulina;
        this.nvl_azucar = nvl_azucar;
    }

    public static void calcularHoraInicio(int segundoMayor) {
        if(banderaPrimerHistorial==1) {
            Calendar horaActual = Calendar.getInstance(); //se obtiene la hora actual
            horaActual.add(Calendar.SECOND, -((segundoMayor - 1) * frecuenciaHistoriales)); //se le restan los segundos
            horaInicio = horaActual;
            banderaPrimerHistorial++;
        }
    }

    public HistorialDosis create(int datos_bomba){
        HistorialDosis miHistorialDosis = new HistorialDosis();

        return miHistorialDosis;
    }

    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Calendar getHora() {
        return hora;
    }

    public void setHora(int hora) {


        Calendar horaHistorial = (Calendar) this.horaInicio.clone();

        horaHistorial.add(Calendar.SECOND, ((hora-1)*frecuenciaHistoriales));
        this.hora = horaHistorial;
    }

    public int getDosis_insulina() {
        return dosis_insulina;
    }

    public void setDosis_insulina(int dosis_insulina) {
        this.dosis_insulina = dosis_insulina;
    }

    public int getNvl_azucar() {
        return nvl_azucar;
    }

    public void setNvl_azucar(int nvl_azucar) {
        this.nvl_azucar = nvl_azucar;
    }
}

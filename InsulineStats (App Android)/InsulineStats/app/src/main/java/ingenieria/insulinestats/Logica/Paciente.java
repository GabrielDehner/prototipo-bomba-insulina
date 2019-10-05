package ingenieria.insulinestats.Logica;

/**
 * Created by mejor on 13/2/2018.
 */

public class Paciente {
    private String usuario;
    private String contrasenia;

    public Paciente(){

    }

    public Paciente(String usuario, String contrasenia) {
        this.usuario = usuario;
        this.contrasenia = contrasenia;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}

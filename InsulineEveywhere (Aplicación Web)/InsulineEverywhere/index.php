<!DOCTYPE html>
<html>
<head>
    <form method="post">
        Ingrese el ID del paciente:
        <input id="dnipaciente" name="dnipaciente" type="text" value="0" />
        <input type="submit" value="Enviar" />
    </form>

</head>
<body>
	<hr>
	<div id="resultadoWS">    
		<?php
		    if($_SERVER['REQUEST_METHOD'] == 'POST') {
			include_once "wsDatosBD.php";
			$json_p = array();
			$json = array();
			$dni = $_POST["dnipaciente"];
			$conexion = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost); 
		    $consulta = "SELECT * FROM HistorialDosis WHERE idPaciente = '$dni' ORDER BY hora";
			$resultado = mysqli_query($conexion, $consulta);

		    while ($registro = mysqli_fetch_array($resultado)){
					$json[] = $registro;
				}
		    echo "<br><br><br>";
			echo '<div class="tablecontainer">
		            <table class="table table-stripped" style="text-align:center;"
		            width="550px;" margin-left="auto;" margin-right="auto;"> 
		                <tr>
		                    <td>Fecha Y Hora:    </td>
		                    <td>Nivel de Azucar:    </td>
		                    <td>Dosis Administrada:</td>
		                </tr>';
			$c= count($json);
			for ($i=0; $i<$c;$i++){
			     echo "<tr><td>" . $json[$i]["hora"] . " </td><td> ". $json[$i]["nvl_azucar"] . " </td><td> " . $json[$i]["dosis_insulina"] . "</td></tr>";
			}
			echo '</table></div>';
			}
		?>
	</div>
</body>
</html>

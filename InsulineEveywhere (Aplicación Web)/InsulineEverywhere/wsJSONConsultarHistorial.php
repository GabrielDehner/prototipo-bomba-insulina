<?PHP
include_once "wsDatosBD.php";
$json=array();
				
		$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

		$consulta="SELECT idHistorialDosis, nvl_azucar, dosis_insulina, hora, idPaciente
					FROM HistorialDosis";
		$resultado=mysqli_query($conexion,$consulta);
		
		while($registro=mysqli_fetch_array($resultado)){
			$json['HistorialDosis'][]=$registro;
		}
		mysqli_close($conexion);
		echo json_encode($json);
?>


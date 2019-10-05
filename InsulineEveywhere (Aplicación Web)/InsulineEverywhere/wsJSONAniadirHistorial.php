<?PHP
include_once "wsDatosBD.php";

$json = array();

//esto seria mas de la controladora logica, esta todo mezclado pero en el tp se planteo de hacer una controladora logica y la parte de persistencia aparte..
	if(isset($_GET["nvl_azucar"]) && isset($_GET["dosis_insulina"]) && isset($_GET["hora"]) && isset($_GET["idPaciente"])){
		$nvl_azucar = $_GET['nvl_azucar'];
		$dosis_insulina = $_GET['dosis_insulina'];
		$hora = $_GET['hora'];
		$idPaciente = $_GET['idPaciente'];
		

		$conexion = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

		$insert = "INSERT INTO HistorialDosis (nvl_azucar, dosis_insulina, hora, idPaciente) 
					VALUES ('{$nvl_azucar}', '{$dosis_insulina}', '{$hora}', '{$idPaciente}')";


		$resultado_insert = mysqli_query($conexion, $insert);

		if($resultado_insert){
			$consulta = "SELECT * FROM HistorialDosis";
			$resultado = mysqli_query($conexion, $consulta);
			if($registro = mysqli_fetch_array($resultado)){
				$json['HistorialDosis'][] = $registro;
			}
			mysqli_close($conexion);
			echo json_encode($json);

		}else{
			$resulta["nvl_azucar"] = 0;
			$resulta["dosis_insulina"] = 'No Registra';
			$resulta["hora"] = 'No Registra';
			$resulta["idPaciente"] = 'No Registra';
			$json['HistorialDosis'][] = $resulta;
			echo json_encode($json);
		}
	}else{
			$resulta["nvl_azucar"] = 0;
			$resulta["dosis_insulina"] = 'WS No Retorna';
			$resulta["hora"] = 'WS No Retorna';
			$resulta["idPaciente"] = 'WS No Retorna';
			$json['HistorialDosis'][] = $resulta;
			echo json_encode($json);
	}

?>
<?PHP
include_once "wsDatosBD.php";

$json = array();

	if(isset($_GET["idUsuario"]) && isset($_GET["contrasenia"])){
		

		$idUsuario = $_GET['idUsuario'];
		$contrasenia = $_GET['contrasenia'];
		

		$conexion = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

		$consulta = "SELECT idUsuario, contrasenia
					FROM Paciente p
					WHERE p.idUsuario = '{$idUsuario}'
					AND p.contrasenia = '{$contrasenia}'";


		$resultado = mysqli_query($conexion, $consulta);

		if($registro=mysqli_fetch_array($resultado)){
			$json['PacienteIni'][]=$registro;

		}else{
			$resulta["idUsuario"] = 0;
			$resulta["contrasenia"] = 'No Registra';
			$json['PacienteIni'][] = $resulta;
			
		}
		
		mysqli_close($conexion);
		echo json_encode($json);
	}
	else{
		$resulta["success"]=0;
		$resulta["message"]='Ws no Retorna';
		$json['PacienteIni'][]=$resulta;
		echo json_encode($json);
	}

?>
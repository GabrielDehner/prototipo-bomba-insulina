<?PHP
include_once "wsDatosBD.php";

$json = array();

//esto seria mas de la controladora logica, esta todo mezclado pero en el tp se planteo de hacer una controladora logica y la parte de persistencia aparte..
	if(isset($_GET["idUsuario"]) && isset($_GET["contrasenia"]) && isset($_GET["nombre"]) && isset($_GET["apellido"])
		&& isset($_GET["fechaNacimiento"]) && isset($_GET["idBomba"])){
		$idUsuario = $_GET['idUsuario'];
		$contrasenia = $_GET['contrasenia'];
		$nombre = $_GET['nombre'];
		$apellido = $_GET['apellido'];
		$fechaNacimiento = $_GET['fechaNacimiento'];
		$idBomba = $_GET['idBomba'];

		/*$nombre = "asdasd";
		$apellido = "asdasd";
		$fechaNacimiento = "2020-02-13 2010:40:00";
		$idBomba = "456645";*/


		$conexion = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

		$insert = "INSERT INTO Paciente (idUsuario, contrasenia, nombre, apellido, fechaNacimiento, idBomba) 
					VALUES ('{$idUsuario}', '{$contrasenia}', '{$nombre}', '{$apellido}', '{$fechaNacimiento}', '{$idBomba}')";


		$resultado_insert = mysqli_query($conexion, $insert);

		if($resultado_insert){
			$consulta = "SELECT * FROM Paciente WHERE idUsuario ='{$idUsuario}'";
			$resultado = mysqli_query($conexion, $consulta);
			if($registro = mysqli_fetch_array($resultado)){
				$json['Paciente'][] = $registro;
			}
			mysqli_close($conexion);
			echo json_encode($json);

		}else{
			$resulta["idUsuario"] = 0;
			$resulta["contrasenia"] = 'No Registra';

			$json['Paciente'][] = $resulta;
			echo json_encode($json);
		}
	}else{
			$resulta["idUsuario"] = 0;
			$resulta["contrasenia"] = 'WS No Retorna';

			$json['Paciente'][] = $resulta;
			echo json_encode($json);
	}

?>
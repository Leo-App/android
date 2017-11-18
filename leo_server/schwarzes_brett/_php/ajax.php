<?php

	require_once('../../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
		die("-ERR db");

	$toArr = $_POST['to'];

	$data = $_POST['addon'];
	$fileName = $_POST['filename'];

	if($fileName != "") {
		$serverFile = date().$fileName;
		$fp = fopen('../uploads/'.$serverFile,'w');
		$url = "/schwarzes_brett/uploads/".$serverFile;
		$date_decoded = base64_decode(substr($data, 28));

		fwrite($fp, $date_decoded);
		fclose($fp);
	} else {
		$url = 'null';
	}

	if($toArr == "")
		die("-ERR m");

	foreach ($toArr as $adressat) {
		if($adressat == "sek1") {
			$adressat = "Sek I";
		} else if($adressat == "sek2") {
			$adressat = "Sek II";
		} else if($adArray == "Alle") {
			$adressat = "Alle";
		}

		$heute = date("Y-m-d H:i:s");
		$titel = $db->real_escape_string($_POST['title']);
		$inhalt = $db->real_escape_string($_POST['content']);
		$ablaufdatum = $db->real_escape_string($_POST['date']);

		$titel = str_replace("_ae_", "ä", $titel);
		$titel = str_replace("_oe_", "ö", $titel);
		$titel = str_replace("_ue_", "ü", $titel);

		$titel = str_replace("_Ae_", "Ä", $titel);
		$titel = str_replace("_Oe_", "Ö", $titel);
		$titel = str_replace("_Ue_", "Ü", $titel);

		$inhalt = str_replace("_ae_", "ä", $inhalt);
		$inhalt = str_replace("_oe_", "ö", $inhalt);
		$inhalt = str_replace("_ue_", "ü", $inhalt);

		$inhalt = str_replace("_Ae_", "Ä", $inhalt);
		$inhalt = str_replace("_Oe_", "Ö", $inhalt);
		$inhalt = str_replace("_Ue_", "Ü", $inhalt);

		if($titel==""||$inhalt==""||$ablaufdatum=="")
			die("-ERR m");

		$query = "INSERT INTO Einträge VALUES ('null', 'null', '".$adressat."', '".$titel."', '".$inhalt."', '".$url."' , '".$heute."', '".$ablaufdatum."')";


		$result = $db->query($query);
		if ($result === false) {
			echo $db->error;
			die("-ERR db");
		}

		echo "+OK";
	}

	$db->close();

?>

<?php

	require_once('../../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
		die("-ERR db");

	$toArr = $_GET['to'];
	
	$url = 'null';


	if($toArr == "")
		die("-ERR m");

	if($adressat == "Sek1") {
		$adressat = "Sek I";
	} else if($adressat == "Sek2") {
		$adressat = "Sek II";
	} else if($adressat == "Alle") {
		$adressat = "all";
	}

	$heute = date("Y-m-d H:i:s");
	$titel = $db->real_escape_string($_GET['title']);
	$inhalt = $db->real_escape_string($_GET['content']);
	$ablaufdatum = $db->real_escape_string($_GET['date']);

	if($titel==""||$inhalt==""||$ablaufdatum=="")
		die("-ERR m");

	$query = "INSERT INTO Einträge VALUES ('null', 'null', '".$adressat."', '".utf8_encode($titel)."', '".utf8_encode($inhalt)."', 'null' , '".$heute."', '".$ablaufdatum."')";
	$result = $db->query($query);
	if ($result === false) {
		echo $db->error;
		die("-ERR db");
	}

	echo "+OK";

	$db->close();

?>
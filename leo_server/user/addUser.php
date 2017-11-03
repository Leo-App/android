<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	//erwartete GET-Werte: checksum, name, permission
	$checksum = $_GET['checksum'];
	$name = $db->real_escape_string($_GET['name']);
	
	$hex = "";
	for ($i = 0; $i < 6; $i++) {
		$char = (ord($name[$i]) - 97) % 16;
		if ($char > 9) {
			$hex = $hex . chr(55 + $char);
		} else {
			$hex = $hex . $char;
		}
		if (strlen($name) == 12) {
			$hex = $hex . $name[6 + $i];
		}
	}
	
	if ($hex != $checksum) {
		die("-permission denied");
	}

	$query = "SELECT uname, uid, uklasse FROM Users WHERE udefaultname = '".$name."'";
	$result = $db->query($query);
	if ($result === false)
		die("-error in query");
	if($result->num_rows == 0) { //User existiert noch nicht und wird erstellt
		$permission = $db->real_escape_string($_GET['permission']);
		$klasse = "N/A";
		if($permission == 2) {
			$klasse = "TEA";
		}
		$date = date("Y-m-d");
		
		$query = "INSERT INTO Users VALUES (null, '".$name."', '".$name."', '".$klasse."', ".$permission.", '".$date."')";
		$result = $db->query($query);
		if ($result === false)
			die("-error in query");
	}

	echo "+";

	$db->close();

?>

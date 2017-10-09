<?php
	
	require_once('dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: name, klasse, permission
	$name = $_SERVER["REMOTE_USER"];
	$klasse = $db->real_escape_string($_GET['klasse']);
	$permission = $db->real_escape_string($_GET['permission']);
	$date = date("Y-m-d");
	
	$query = "SELECT uname, uid, uklasse FROM Users WHERE udefaultname = '".$name."'"; //Existiert user mit dem Namen $name bereits? Wenn ja return Name, ID und Klasse

	$result = $db->query($query);

	if ($result === false)
		die("-error in query");
	
	if($result->num_rows > 0) { //Ja, User existiert
		echo "+";
	} else { //Nein, User existiert nicht.
		if($amount == 0) { //Da nicht vorhanden, fügen wir ihn hinzu
			$query = "INSERT INTO Users VALUES (null, '".$name."', '".$name."', '".$klasse."', '".$permission."', '".$date."')";
			$result = $db->query($query);
			if ($result === false)
				die("-error in query");
		}

		echo "+";
	}

	$db->close();

?>

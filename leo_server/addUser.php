<?php
	
	if ($_GET['key'] != 5453)
		die("-permission denied!");
	
	require_once('dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-Connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: name, klasse, permission
	$name = $db->real_escape_string($_GET['name']);
	$klasse = $db->real_escape_string($_GET['klasse']);
	$permission = $db->real_escape_string($_GET['permission']);
	$date = date("Y-m-d");
	
	$query = "SELECT uname, uid, uklasse FROM Users WHERE udefaultname = '".$name."'"; //Existiert user mit dem Namen $name bereits? Wenn ja return Name, ID und Klasse

	$result = $db->query($query);

	if ($result === false)
		die("-error in query");
	
	if($result->num_rows > 0) { //Ja, User existiert
		$assoc = $result->fetch_assoc();
		$uname = $assoc['uname'];
		$id = $assoc['uid'];
		$uklasse = $assoc['uklasse'];
	
		echo "_old_separator_".$id."_separator_".$uname."_separator_".$uklasse;
	} else { //Nein, User existiert nicht.
		if($amount == 0) { //Da nicht vorhanden, fügen wir ihn hinzu
			$query = "INSERT INTO Users VALUES (null, '".$name."', '".$name."', '".$klasse."', '".$permission."', '".$date."')";
			$result = $db->query($query);
			if ($result === false)
				die("-error in query");
		}	

		$query = "SELECT uid FROM Users WHERE udefaultname = '".$name."'"; //ID des Users bestimmen

		$result = $db->query($query);
		if ($result === false)
			die("-error in query");

		$id = $result->fetch_assoc()['uid'];

		echo "_new_".$id; //Id ausgeben
	}

	$db->close();

?>
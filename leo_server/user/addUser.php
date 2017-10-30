<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: name, klasse, permission
	$name = $_SERVER["REMOTE_USER"];
	$date = date("Y-m-d");

	$query = "SELECT uname, uid, uklasse FROM Users WHERE udefaultname = '".$name."'";

	$result = $db->query($query);

	if ($result === false)
		die("-error in query");

	if($result->num_rows == 0) { //User existiert noch nicht und wird erstellt
		$query = "INSERT INTO Users VALUES (null, '".$name."', '".$name."', '".$klasse."', '".$permission."', '".$date."')";
		$result = $db->query($query);
		if ($result === false)
			die("-error in query");
	}

	echo "+";

	$db->close();

?>

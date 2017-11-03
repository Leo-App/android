<?php

	require_once('dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	$id = $db->real_escape_string($_GET['remote']);

	$query = "UPDATE  Einträge SET Gelesen = Gelesen + 1 WHERE EintragID = ".$id;
	
	if ($db->query($query) !== true) {
		echo "-ERR";
	}

	$db->close();

?>
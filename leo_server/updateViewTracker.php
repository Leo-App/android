<?php

if ($_GET['key'] != 5453)
		die("-permission denied!");
	
	require_once('dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-Connection failed: ".$db->connect_error);

	$id = $db->real_escape_string($_GET['remote']);

	$query = "UPDATE  Einträge SET Gelesen = Gelesen + 1 WHERE  EintragID = ".$id;
	
	if ($db->query($query) !== TRUE) {
		echo "-ERR";
	}


	$db->close();

?>
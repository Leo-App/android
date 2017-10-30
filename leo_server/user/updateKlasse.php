<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: userklasse

	$name = $_SERVER['REMOTE_USER'];
	$klasse = $db->real_escape_string($_GET['userklasse']);

	$query = "UPDATE Users SET uklasse = '".$klasse."' WHERE udefaultname = '".$name."'";

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	$db->close();

?>

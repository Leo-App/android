<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	//erwartete GET-Werte: uid, uklasse
	$uid = $db->real_escape_string($_GET['uid']);
	$uklasse = $db->real_escape_string($_GET['uklasse']);

	$query = "UPDATE Users SET uklasse = '".$uklasse."' WHERE uid = ".$uid;
	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	$db->close();

?>

<?php
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: uid, vid, grund
	
	$uid = $db->real_escape_string($_GET['uid']);
	$vid = $db->real_escape_string($_GET['vid']);
	$grund = $db->real_escape_string($_GET['grund']);
	$date = date("Y-m-d");

	//stimmung wird gesendet

	$query = "INSERT INTO Vote VALUES (".$vid.", ".$uid.", '".$date."', '".$grund."')";

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	$db->close();
?>
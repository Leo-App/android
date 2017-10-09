<?php
	
	if ($_GET['key'] != 5453)
		die("-permission denied!");
	
	require_once('dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($conn->connect_error)
    	die("-Connection failed: " . $conn->connect_error);

	// mitgegebene Werte über get: userid, userklasse

	$id = $db->real_escape_string($_GET['userid']);
	$klasse = $db->real_escape_string($_GET['userklasse']);
	
	$query = "UPDATE Users SET uklasse = '".$klasse."' WHERE uid = ".$id;

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	$db->close();

?>
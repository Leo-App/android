<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// erwartete GET-Werte: uid, uname
	$uid = $db->real_escape_string($_GET['uid']);
	$uname = $db->real_escape_string($_GET['uname']);

	$query = "SELECT * FROM Users WHERE uname = '".$uname."'";
	$result = $db->query($query);
	if ($result === false)
		die("-error in query");
	if ($result->num_rows > 0)
		die("-username already exists");

	$query = "UPDATE Users SET uname = '".$uname."' WHERE uid = ".$uid;
	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	echo "+";

	$db->close();

?>
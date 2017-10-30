<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: " . $db->connect_error);

	// mitgegebene Werte über get: username

	$name = $_SERVER['REMOTE_USER'];
	$username = $db->real_escape_string($_GET['username']);

	$query = "SELECT * FROM Users WHERE uname = '".$username."'";

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	if($result->num_rows > 0)
		die("-username already exists");

	$query = "UPDATE Users SET uname = '".$username."' WHERE udefaultname = '".$name."'";

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	echo "+";

	$db->close();

?>

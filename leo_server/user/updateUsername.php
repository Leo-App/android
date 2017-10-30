<?php

	if ($_GET['key'] != 5453)
		die("-permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($conn->connect_error)
    	die("-Connection failed: " . $conn->connect_error);

	// mitgegebene Werte �ber get: userid, username

	$id =	$db->real_escape_string($_GET['userid']);
	$username = $db->real_escape_string($_GET['username']);

	$query = "SELECT * FROM Users WHERE uname = '".$username."'";

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	if($result->num_rows > 0)
		die("-username already exists");

	$query = "UPDATE Users SET uname = '".$username."' WHERE uid = ".$id;

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	$db->close();

?>

<?php
	
	if (strcmp($_GET['key'],"1ST4if") != 0)
		die("-1");
	
	require_once('dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-1");
	
	$primary = $db->real_escape_string($_GET['id']);

	$query = "SELECT COUNT(*) as amount FROM Users WHERE uid = ".$primary;

	$result = $db->query($query);

	if ($result === false)
		die("-1");

	echo $result->fetch_assoc()[amount];

	$db->close();

?>
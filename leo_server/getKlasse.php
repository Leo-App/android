<?php

	if ($_GET['key'] != 5453)
		die("-permission denied!");
	
	require_once('dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-1");
	
	$primary = $db->real_escape_string($_GET['userid']);

	$query = "SELECT uklasse as level FROM Users WHERE uid = ".$primary;

	$result = $db->query($query);

	if ($result === false || $result->num_rows == 0)
		die("-1");
	
	echo $result->fetch_assoc()['level'];

	$db->close();

?>
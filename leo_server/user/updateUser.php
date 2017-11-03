<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	//erwartete GET-Werte: name
	$default = $db->real_escape_string($_GET['name']);

	$query = "SELECT * FROM Users WHERE udefaultname = '".$default."'";
	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	$row = $result->fetch_assoc();
	echo $row['uid'].";".$row['upermission'].";".$row['uname'];

	$db->close();

?>
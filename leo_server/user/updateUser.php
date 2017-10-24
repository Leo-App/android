<?php
	
	require_once('dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	$default = $_SERVER["REMOTE_USER"];

	$query = "SELECT * FROM Users WHERE udefaultname = '".$default."'";

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	$row = $result->fetch_assoc();
	echo $row['uid'].";".$row['upermission'].";".$row['uname'];

	$db->close();

?>
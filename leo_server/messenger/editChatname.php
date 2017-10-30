<?php

	if ($_SERVER['REMOTE_USER'] != "leoapp")
		die("-permission denied!");
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: chatname, chatid

	$chatname = $_GET['chatname'];
	$chatid = $_GET['chatid'];

	$query = "UPDATE Chats SET cname = '".$chatname."' WHERE cid = ".$chatid;

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	$db->close();

?>

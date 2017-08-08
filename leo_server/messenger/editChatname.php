<?php

	if ($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
		die("Connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: chatname, chatid

	$chatname = $_GET['chatname'];
	$chatid = $_GET['chatid'];

	$query = "UPDATE Chats SET cname = '".$chatname."' WHERE cid = ".$chatid;

	$result = $db->query($query);
	if ($result === false)
		die("error in query");

	$db->close();

?>
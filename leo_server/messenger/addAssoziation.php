<?php

	if ($_SERVER["REMOTE_USER"] != "leoapp")
		die("-permission denied!");
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: userid, chatid

	$chatid = $db->real_escape_string($_GET['chatid']);
	$userid = $db->real_escape_string($_GET['userid']);
	$date = date("Y-m-d H:i:s");

	$query = "INSERT INTO Assoziation VALUES(".$chatid.", ".$userid.")";
	$result = $db->query($query);
	if ($result === false)
		die("-error in query");
	
	$db->close();

?>

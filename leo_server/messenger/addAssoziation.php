<?php
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: uid, cid

	$cid = $db->real_escape_string($_GET['cid']);
	$uid = $db->real_escape_string($_GET['uid']);

	$query = "INSERT INTO Assoziation VALUES(".$chatid.", ".$userid.")";
	$result = $db->query($query);
	if ($result === false)
		die("-error in query");
	
	$db->close();

?>
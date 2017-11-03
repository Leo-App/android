<?php
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Wert über get: uid, cid

	$uid = $db->real_escape_string($_GET['uid']);
	$cid = $db->real_escape_string($_GET['cid']);

	$query = "DELETE FROM Assoziation WHERE uid = ".$userid." AND cid = ".$chatid;

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	$db->close();

?>
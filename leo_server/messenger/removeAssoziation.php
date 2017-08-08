<?php

	if ($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
		die("Connection failed: ".$db->connect_error);

	// mitgegebene Wert über get: userid, chatid

	$userid = $db->real_escape_string($_GET['userid']);
	$chatid = $db->real_escape_string($_GET['chatid']);
	$date = date("Y-m-d H:i:s");

	$query = "DELETE FROM Assoziation WHERE uid = ".$userid." AND cid = ".$chatid;

	$result = $db->query($query);
	if ($result === false)
		die("error in query");

	$db->close();

?>
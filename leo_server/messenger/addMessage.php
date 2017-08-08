<?php

	if ($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("Connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: userid, chatid
	$userid = $db->real_escape_string($_GET['userid']);
	$chatid = $db->real_escape_string($_GET['chatid']);
	$message = $db->real_escape_string($_GET['message']);

	//assoziation wird überprüft
	if ($db->query("SELECT * FROM Assoziation WHERE cid = '".$chatid."' AND uid = '".$userid."'")->num_rows == 0)
		die("User nicht in diesem Chat");
	$date = date("Y-m-d H:i:s");

	//nachricht wird gesendet
	$query = "INSERT INTO Messages VALUES (null, '".$userid."', '".$message."', '".$chatid."', '".$date."')";
	$result = $db->query($query);
	if ($result === false)
		die("error in query");

	$db->close();
?>
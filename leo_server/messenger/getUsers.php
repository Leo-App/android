<?php

	if ($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("Connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: userid

	$userid = $db->real_escape_string($_GET['userid']);

	$query = "SELECT uid, uname, uklasse, upermission FROM Users WHERE uid != ".$userid;
	$result = $db->query($query);
	if ($result !== false)
		while ($row = $result->fetch_assoc())
			echo $row['uid']."_;_".$row['uname']."_;_".$row['uklasse']."_;_".$row['upermission']."_nextUser_";
		
	$db->close();

?>
<?php

	if ($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("Connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: userid

	$userid = $db->real_escape_string($_GET['userid']);

	$query = "SELECT uid, uname, uklasse, upermission, udefaultname FROM Users WHERE uid != ".$userid;
	$result = $db->query($query);
	if ($result !== false)
		while ($row = $result->fetch_assoc())
			echo $row['uid']."_ ;_"
		.str_replace("_ next_", "_  next_", str_replace("_ ;_", "_  ;_", $row['uname']))."_ ;_"
		.$row['uklasse']."_ ;_"
		.$row['upermission']."_ ;_"
		.$row['udefaultname']."_ next_";
		
	$db->close();

?>
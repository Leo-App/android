<?php

	if ($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("Connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: userid

	$userid = $db->real_escape_string($_GET['userid']);

	$query = "SELECT a.cid, c.cname, c.ctype FROM Assoziation a, Chats c WHERE a.uid = ".$userid." AND c.cid = a.cid";
	$result = $db->query($query);
	if ($result !== false)
		while ($row = $result->fetch_assoc())
			echo $row['cid']."_ ;_"
		.str_replace("_ next_", "_  next_", str_replace("_ ;_", "_  ;_", $row['cname']))."_ ;_"
		.$row['ctype']."_ next_";
		
	$db->close();

?>
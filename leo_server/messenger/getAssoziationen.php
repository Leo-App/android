<?php

	if ($_SERVER['REMOTE_USER'] != "leoapp")
		die("-permission denied!");
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Wert über get: userid

	$userid = $db->real_escape_string($_GET['userid']);

	$query = "SELECT cid, uid FROM Assoziation";

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	while($row = $result->fetch_assoc())
		echo $row['cid'].",".$row['uid'].";";

	$db->close();

?>

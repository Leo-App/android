<?php
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Wert über get: uid

	$uid = $db->real_escape_string($_GET['uid']);

	$query = "SELECT a2.cid, a2.uid FROM Assoziation a1 INNER JOIN Assoziation a2 ON a1.cid = a2.cid WHERE a1.uid = " . $uid;

	$result = $db->query($query);
	if ($result === false)
		die("-error in query");

	while($row = $result->fetch_assoc())
		echo $row['cid'].",".$row['uid'].";";

	$db->close();

?>

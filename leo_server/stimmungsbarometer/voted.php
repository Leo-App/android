<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	//mitgegebene Werte über get: uid

	$uid = $_GET['uid'];
	$date = date("Y-m-d");

	$query = "SELECT vid FROM Vote WHERE uid = ".$uid." AND vdate = '".$date."'";
	$result = $db->query($query);

	if($result->num_rows > 0)
		echo $result->fetch_assoc()['vid'];
	else
		echo "0";

	$db->close();

?>
<?php

	if ($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("Connection failed: ".$db->connect_error);

	//mitgegebene Werte über get: userid

	$userid = $_GET['userid'];
	$date = date("Y-m-d");

	$query = "SELECT vid FROM Vote WHERE uid = ".$userid." AND vdate = '".$date."'";
	$result = $db->query($query);

	if($result->num_rows > 0)
		echo "true";
	else
		echo "false";

	$db->close();

?>
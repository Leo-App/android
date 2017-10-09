<?php

	if ($_SERVER["REMOTE_USER"] != "leoapp")
		die("-permission denied!");
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	//mitgegebene Werte über get: userid

	$userid = $_GET['userid'];
	$date = date("Y-m-d");

	$query = "SELECT vid FROM Vote WHERE uid = ".$userid." AND vdate = '".$date."'";
	$result = $db->query($query);

	if($result->num_rows > 0)
		echo $result->fetch_assoc()['vid'];
	else
		echo "0";

	$db->close();

?>

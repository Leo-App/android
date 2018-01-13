<?php

	

	require_once('../dbconfig.php');



	$db = new mysqli(dbhost, dbuser, dbpass, dbname);



	if ($db->connect_error)

    	die("-connection failed: ".$db->connect_error);



	// mitgegebene Werte über get: uid, vid, grund

	

	$hausaufgabenid = $db->real_escape_string($_GET['hausaufgabenid']);

	$fach = $db->real_escape_string($_GET['fach']);

	$hausaufgabe = $db->real_escape_string($_GET['hausaufgabe']);
  
  $autor = $db->real_escape_string($_GET['autor']);

	$datum = date("Y-m-d");
  
  $ablaufdatum = date("Y-m-d")


	$query = "INSERT INTO Homework VALUES (".$hausaufgabenid.", ".$fach.", '".$hausaufgabe."', '".$autor."', '".$datum."', '".$ablaufdatum."')";



	$result = $db->query($query);

	if ($result === false)

		die("-error in query");



	$db->close();


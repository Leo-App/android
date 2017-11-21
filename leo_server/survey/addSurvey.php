<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
	  	die("-connection failed: ".$db->connect_error);

	$id = $db->real_escape_string($_GET['id']);
	$to = $db->real_escape_string($_GET['to']);
	$title = $db->real_escape_string($_GET['title']);
	$answers = $db->real_escape_string($_GET['answers']);
	$description = $db->real_escape_string($_GET['desc']);
	$multiple = $db->real_escape_string($_GET['mult']);

	//REMOVE EXISTING SURVEY

	$query = "DELETE FROM Survey WHERE owner = ".$id;
	$db->query($query);

	$query = "DELETE FROM Answers WHERE survey = ".$id;
	$db->query($query);

	//INSERT SURVEY DATA
	switch($to) {
		case 0:
			$to = "Alle";
		break;
		case 1:
			$to = "Sek I";
	  	break;
		case 2:
			$to = "Sek II";
		break;
		case 8:
			$to = "EF";
		break;
		case 9:
			$to = "Q1";
		break;
		case 10:
			$to = "Q2";
		break;
		default:
			$to += 2;
	}

	$query = "INSERT INTO Survey VALUES (".$id.", '".$title."', '".$description."', '".$to."', '".$multiple."', '".date("Y-m-d H:i:s")."')";

	$result = $db->query($query);

	if ($result === false)
	  die("-error in query1");

	//INSERT ANSWERS

	foreach (explode("_;_", $answers) as $answer) {
	  $query = "INSERT INTO Answers VALUES ('null', '".$id."', '".$answer."')";
	  $result = $db->query($query);
	  if ($result === false)
		die("-error in query");
	}

	echo "+OK";

	$db->close();

?>

<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
		die("-connection failed: ".$db->connect_error);

	$text = $db->real_escape_string($_GET['text']);
	$thema = $db->real_escape_string($_GET['thema']);

	$sql = "INSERT INTO Entscheidungen VALUES (null, '".$text."', '".$thema."') ON DUPLICATE KEY UPDATE content='".$text."'";

	$result = $db->query($sql);

	if($result === false)
		die("-ERR 1");

	echo "+OK";

?>

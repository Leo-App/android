<?php

  require_once('../dbconfig.php');

  $db = new mysqli(dbhost, dbuser, dbpass, dbname);

  if ($db->connect_error)
    die("-connection failed: ".$db->connect_error);

  $survey = $db->real_escape_string($_GET['survey']);

  $query = "DELETE FROM Survey WHERE owner = ".$survey;
  $result = $db->query($query);

  $query = "DELETE FROM Answers WHERE survey = ".$survey;
  $result2 = $db->query($query);

  if($result === false || $result2 === false)
    die("-ERR");

  echo "+OK";

	$db->close();

?>

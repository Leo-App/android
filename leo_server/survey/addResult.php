<?php

  require_once('../dbconfig.php');

  $db = new mysqli(dbhost, dbuser, dbpass, dbname);

  if ($db->connect_error)
    die("-connection failed: ".$db->connect_error);

  $user = $db->real_escape_string($_GET['user']);
  $answer = $db->real_escape_string($_GET['answer']);

  $query = "INSERT INTO Result VALUES ('null', '".$user."', '".$answer."')";
  $result = $db->query($query);

  if($result === false)
    die("-ERR");

  echo "+OK";

	$db->close();

?>

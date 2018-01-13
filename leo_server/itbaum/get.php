<?php

  require_once('../dbconfig.php');

  $db = new mysqli(dbhost, dbuser, dbpass, dbname);

  if ($db->connect_error)
    die("-connection failed: ".$db->connect_error);

  $subject = $db->real_escape_string($_GET['subject']);

  $sql = "SELECT content FROM Entscheidungen WHERE thema ='".$subject."'";
  $result = $db->query($sql);

  if($result === false)
    die("-ERR");

  echo $result->fetch_assoc()['content'];

  echo "-";

?>

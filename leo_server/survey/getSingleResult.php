<?php

  require_once('../dbconfig.php');

  $db = new mysqli(dbhost, dbuser, dbpass, dbname);

  if ($db->connect_error)
    die("-Connection failed: ".$db->connect_error);

  $user = $db->real_escape_string($_GET['user']);

  $query = "SELECT answer as ans FROM Result WHERE user=".$user;
  $result = $db->query($query);

  if($result === false)
    die("-ERR");

  while($row = $result->fetch_assoc()) {

    echo $row['ans']."_;_";

  }


?>

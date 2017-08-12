<?php

$servername = "localhost";
$username = "d02566f2";
$password = "leoApp_2017";
$dbname = "d02566f2";

$id = "RW6SlQ";
$auth = $_GET['auth'];


if(strcmp($id, $auth) !== 0) {
	echo "false";
	return;
}

$conn = new mysqli($servername, $username, $password, $dbname);

$idD = $conn->real_escape_string($_GET['id']);
$pw = $conn->real_escape_string($_GET['pw']);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "SELECT * FROM user WHERE user_id=".$idD." AND pass='".$pw."'";
$result = $conn->query($sql); 

if($result->num_rows > 0)
	echo "true";
else
	echo "false";


$conn->close();




?> 
 <?php
require_once ('../dbconfig.php');

// Create connection
$db = new mysqli(dbhost, dbuser, dbpass, dbname);

$kid = "1";
$uid = "2";
$Lohn ="5";
$sql = "INSERT INTO Gemeldet VALUES ('".$kid."','".$uid."','".$Lohn."')";
$result = $db->query($sql);
if ($result === false)
	 die("error in query");

$db->close();
?> 
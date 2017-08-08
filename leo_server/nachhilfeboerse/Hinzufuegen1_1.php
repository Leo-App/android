 <?php
require_once ('../dbconfig.php');

// Create connection
$db = new mysqli(dbhost, dbuser, dbpass, dbname);

$Fach = $_GET['f'];
$uid = $_GET['u'];
$Erstellungsdatum = $_GET['d'];
$sql = "INSERT INTO Kleinanzeige VALUES ('null','".$Fach."', '".$uid."', '".$Erstellungsdatum."')";
$result = $db->query($sql);
if ($result === false)
	 die("error in query");

$db->close();
?> 
 <?php
require_once ('../dbconfig.php');

// Create connection
$db = new mysqli(dbhost, dbuser, dbpass, dbname);

$sql = "SELECT Fach FROM Kleinanzeige";
$result = $db->query($sql);

if ($result !== false) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo utf8_encode($row["Fach"]. "_next_");
    }
} 
    

$db->close();
?> 
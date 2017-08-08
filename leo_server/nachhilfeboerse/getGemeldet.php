 <?php
require_once ('../dbconfig.php');

// Create connection
$db = new mysqli(dbhost, dbuser, dbpass, dbname);

$sql = "SELECT u.uname,g.Lohn FROM Users u, Gemeldet g WHERE u.uid = g.uid";
$result = $db->query($sql);

if ($result !== false) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo   $row["uname"].";".$row["Lohn"]. "_next_";
    }
} 
    

$db->close();
?>
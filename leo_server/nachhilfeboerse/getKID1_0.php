 <?php
require_once ('../dbconfig.php');

// Create connection
$db = new mysqli(dbhost, dbuser, dbpass, dbname);

$sql = "SELECT kid FROM Kleinanzeige";
$result = $db->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo "id: " . $row["id"]. " - Name: " . $row["firstname"]. " " . $row["lastname"]. "<br>";
    }
} else {
    echo "0 results";
}
$conn->close();
?> 
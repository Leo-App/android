<?php



	require_once('../dbconfig.php');



	$db = new mysqli(dbhost, dbuser, dbpass, dbname);



	if ($db->connect_error)

    	die("-connection failed: ".$db->connect_error);



	//mitgegebene Werte über get: hausaufgabenID



	$hausaufgabenid = $db->real_escape_string($_GET['hausaufgabenid']);



	$queryHausaufgabe = "SELECT FACH(Fach) as fach, HAUSAUFGABE(Hausaufgabe) as hausaufgabe, AUTOR(Autor) as autor, ERSTELLDATUM(Datum) as edatum, ABLAUFDATUM(Ablaufdatum) as adatum FROM Homework WHERE hausaufgabenid = ".$hausaufgabenid." GROUP BY fach";



	$result = $db->query($queryHausaufgabe);

	if ($result === false)

		die("-error in queryHausaufgabe");



	while($row = $result->fetch_assoc())

		echo $row['fach'].";".$row['hausaufgabe'].".".$row['autor'].".".$row['edatum'].".".$row['adatum']."_next_";



	$db->close();

?>

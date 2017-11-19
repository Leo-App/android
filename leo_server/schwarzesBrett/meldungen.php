<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	$date = date("Y-m-d");

	$query = "SELECT EintragID, Anhang, Gelesen, Titel, Adressat, Inhalt, UNIX_TIMESTAMP(Erstelldatum) as Erstell, UNIX_TIMESTAMP(Ablaufdatum) as Ablauf FROM Einträge WHERE Ablaufdatum >= '".$date."'";

	$result = $db->query($query);
	if ($result !== false) {
		while ($row = $result->fetch_assoc()) {
				echo utf8_encode($row['Titel']) . ";" . utf8_encode($row['Adressat']) . ";" . utf8_encode($row['Inhalt']) . ";" . $row['Erstell'] . ";" . $row['Ablauf']. ";" . $row['EintragID'] . ";" . $row['Gelesen'] . ";" . $row['Anhang'] . "_next_";
		}
	} else {
		die("-error in query");
	}

	$db->close();

?>

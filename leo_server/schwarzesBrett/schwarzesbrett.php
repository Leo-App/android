<?php

	if ($_SERVER["REMOTE_USER"] != "leoapp")
		die("-permission denied!");
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

    $sql = "SELECT Adressat,Titel,Inhalt, UNIX_TIMESTAMP(Erstelldatum) as Erstell, UNIX_TIMESTAMP(Ablaufdatum) as Ablauf FROM Einträge WHERE Ablaufdatum >= '".$date."'";
    $result = $db->query($sql);

    $i = 0;
	while($row = $result->fetch_assoc()){
		echo "<h4>Adressat: ".$row['Adressat']."<br>"."Überschrift: ".$row['Titel']."<br>"."Inhalt: ".$row['Inhalt']."<br>"."Erstelldatum: ".$row['Erstelldatum']."<br>"."Ablaufdatum: ".$row['Ablaufdatum']."</h4><p></p>";
		$i++;
	}

	echo "<h4>Anzahl Einträge:".$i."</h4>";

    $db->close();
    
?>

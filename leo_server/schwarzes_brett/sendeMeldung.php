<?php

	if ($_SERVER["REMOTE_USER"] != "leoapp")
		die("-permission denied!");
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	$heute = date("Y-m-d H:i:s");
    $titel = $_GET['titel'];
    $adressat = $_GET['adressat'];
	$inhalt = $_GET['inhalt'];
	$ablaufdatum = $_GET['ablaufdatum'];
    
if($titel==null||$adressat==null||$inhalt==null||$ablaufdatum==null){
	?>
<html>
	<h2><font face="Comic Sans Ms">Ihre Angaben waren nicht vollständig, bitte gehen Sie zurück und füllen Sie alle Felder aus</font></h2>
	<br>
  <form>
  <input type="button" value="Zurück" onclick="window.location.href='http://moritz.liegmanns.de/schwarzes_brett/NeueMeldung.php'" />
  </form> 
</html>
<?php
}
else{
	?>
<html>
	<p><font face="Comic Sans Ms">Der Eintrag wurde erfolgreich gespeichert! </font></p>
	<br>
  <form>
  <input type="button" value="Zurück" onclick="window.location.href='http://moritz.liegmanns.de/schwarzes_brett/NeueMeldung.php'" />
  </form> 
</html>
<?php
	$query = "INSERT INTO Einträge VALUES ('null', 'null', '".$adressat."', '".$titel."', '".$inhalt."', '".$heute."', '".$ablaufdatum."')";
	$result = $db->query($query);
	if ($result === false)
		die("-error in query");
}

	$db->close();

?>

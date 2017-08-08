<?php

	if ($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("Connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: userid, chatid
	$userid = $db->real_escape_string($_GET['userid']);
	$chatid = $db->real_escape_string($_GET['chatid']);
	$message = $_GET['message'];
	$vKey = $_GET['vKey'];
	$key2 = "ABCD";

	//assoziation wird überprüft
	if ($db->query("SELECT * FROM Assoziation WHERE cid = '".$chatid."' AND uid = '".$userid."'")->num_rows == 0)
		die("User nicht in diesem Chat");
	$date = date("Y-m-d H:i:s");

	//nachricht wird entschlüsselt
	for($i = 0; $i < strlen($message); $i++) {
		$keyChar = ord($vKey[$i]) - (ord($key2[$i % strlen($key2)]) - 65);
		if($keyChar < 65) {
			$keyChar = $keyChar + 26;
		}
		$textChar = ord($message[$i]);
		$decrypted = $textChar - ($keyChar - 65);
		if ($textChar >= 65 && $textChar <= 90) {
        	if ($decrypted < 65)
                $decrypted += 26;
			$message[$i] = chr($decrypted);
        } else if ($textChar >= 97 && $textChar <= 122) {
            if ($decrypted < 97)
                $decrypted += 26;
			$message[$i] = chr($decrypted);
        } else {
			$message[$i] = chr($textChar);
        }
	}

	$message = $db->real_escape_string($message);

	//nachricht wird gesendet
	$query = "INSERT INTO Messages VALUES (null, '".$userid."', '".$message."', '".$chatid."', '".$date."')";
	$result = $db->query($query);
	if ($result === false)
		die("error in query");

	$db->close();
?>
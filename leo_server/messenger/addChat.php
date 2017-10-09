<?php
	
	if ($_SERVER["REMOTE_USER"] != "leoapp")
		die("-permission denied!");
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: chatname, chattype

	$name = $db->real_escape_string($_GET['chatname']);
	$type = $db->real_escape_string($_GET['chattype']);
	$date = date("Y-m-d H:i:s");

	if($type == "private") {
		$str1 = "";
		$str2 = "";
		$str = " - ";
		$i = 0;
		for($x = 0; $x < strlen($name); $x++) {
			$char = $name[$x];
			if($i == 0) {
				if($char == ' ' || $char == '-')
					$i = 1;
				else
					$str1 = $str1.$char;
			} else if($i == 1) {
				if($char != ' ' && $char != '-')
					$str2 = $str2.$char;
			}
		}
		

		$name1 = $str2 + $str + $str1;
		$querytest = "SELECT cid FROM Chats WHERE cname = '".$name."' OR cname = '".$name1."'";
		$resulttest = $db->query($querytest);
		if($resulttest->num_rows != 0)
			die($resulttest->fetch_assoc()['cid']);
		
		$query = "INSERT INTO Chats VALUES (null, '".$name."', '".$type."', '".$date."')";

		$result = $db->query($query);
		if ($result === false)
			die("-error in query");
		
		$chatid = -1;
		$chatidqres = $db->query("SELECT cid FROM Chats WHERE ccreate = '".$date."'");
		if ($chatidqres !== false)
			$chatid = $chatidqres->fetch_assoc()['cid'];
		
		if($chatid != -1) {
			$query = "INSERT INTO Assoziation VALUES (".$chatid.", ".$str1.")";
			$db->query($query);
			$query = "INSERT INTO Assoziation VALUES (".$chatid.", ".$str2.")";
			$db->query($query);
		}
		
		echo $chatid;
	} else {
	
		$query = "INSERT INTO Chats VALUES (null, '".$name."', '".$type."', '".$date."')";

		$result = $db->query($query);
		if ($result === false)
			die("-error in query");

		$chatidqres = $db->query("SELECT cid FROM Chats WHERE ccreate = '".$date."'");
		if ($chatidqres !== false)
			echo($chatidqres->fetch_assoc()['cid']);
	}

	$db->close();

?>

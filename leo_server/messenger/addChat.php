<?php
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: cname, cype

	$cname = $db->real_escape_string($_GET['cname']);
	$ctype = $db->real_escape_string($_GET['cype']);
	$cdate = date("Y-m-d H:i:s");

	if($ctype == "private") {
		$id1 = "";
		$id2 = "";
		$i = 0;
		for($x = 0; $x < strlen($cname); $x++) {
			$char = $cname[$x];
			if($i == 0) {
				if($char == ' ' || $char == '-')
					$i = 1;
				else
					$id1 = $id1.$char;
			} else if($i == 1) {
				if($char != ' ' && $char != '-')
					$id2 = $id2.$char;
			}
		}
		
		$cname1 = $id2 + $str + $id1;
		
		$query = "SELECT cid FROM Chats WHERE cname = '".$cname."' OR cname = '".$cname1."'";
		$result = $db->query($query);
		if($result->num_rows != 0) {
			$cid = $result->fetch_assoc()['cid'];
		} else {
			$query = "INSERT INTO Chats VALUES (null, '".$cname."', '".$ctype."', '".$cdate."')";
			$result = $db->query($query);
			if ($result === false)
				die("-error in query");
			
			$cid = -1;
			$result = $db->query("SELECT cid FROM Chats WHERE cname = '".$cname."' AND ctype = 'private'");
			if ($result !== false)
				$cid = $result->fetch_assoc()['cid'];
		}
			
		if($cid != -1) {
			$query = "INSERT INTO Assoziation VALUES (".$cid.", ".$id1.")";
			$db->query($query);
			$query = "INSERT INTO Assoziation VALUES (".$cid.", ".$id2.")";
			$db->query($query);
		}
		
		echo $cid;
		
	} else {
		$query = "INSERT INTO Chats VALUES (null, '".$cname."', '".$ctype."', '".$cdate."')";

		$result = $db->query($query);
		if ($result === false)
			die("-error in query");

		$result = $db->query("SELECT cid FROM Chats WHERE ccreate = '".$date."'");
		if ($result !== false) {
			echo($result->fetch_assoc()['cid']);
		}
	}

	$db->close();

?>
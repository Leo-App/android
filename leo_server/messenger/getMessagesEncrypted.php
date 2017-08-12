<?php

	if($_GET['key'] != 5453)
		die("permission denied!");

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("Connection failed: ".$db->connect_error);

	// mitgegebene Werte über get: userid

	$userid = $db->real_escape_string($_GET['userid']);

	//chatids des users werden gesammelt
	$chatsqres = $db->query("SELECT cid FROM Assoziation WHERE uid = ".$userid);
	$chatids = [];
	if ($chatsqres !== false)
		while ($row = $chatsqres->fetch_assoc())
			$chatids[] = $row['cid'];
	else
		die("error in chats");

	foreach ($chatids as $chatid) {
		$query = "SELECT DISTINCT mid, uid, mtext, cid, UNIX_TIMESTAMP(mdate) as mdate FROM Messages WHERE cid = ".$chatid." ORDER BY cid, mdate";
		$result = $db->query($query);
		if ($result !== false)
			while ($row = $result->fetch_assoc())
				$array[$row['cid']][] = [
					'id' => $row['mid'],
					'nachricht' => $row['mtext'],
					'absender' => $row['uid'],
					'datum' => $row['mdate'],
					'chat' => $row['cid']
				];
		else
			die("error in query");
		$result = null;
	}

	$key2 = "ABCD"; //Oh wow
	foreach ($array as $chat) {
		foreach ($chat as $message) {
			$text = str_replace("_ ;_", "_  ;_", $message['nachricht']);
			$vKey = "";
			for($i = 0; $i < strlen($text); $i++) {
				$keyChar = rand(65, 90);
				$textChar = ord($text[$i]);
				$encrypted = $textChar + ($keyChar - 65);
				if ($textChar >= 65 && $textChar <= 90) {
					if ($encrypted > 90)
						$encrypted -= 26;
					$text[$i] = chr($encrypted);
				} else if ($textChar >= 97 && $textChar <= 122) {
					if ($encrypted > 122)
						$encrypted -= 26;
					$text[$i] = chr($encrypted);
				} else {
					$text[$i] = chr($textChar);
				}
				$key2Char = ord($key2[$i % strlen($key2)]) - 65;
				$keyChar += $key2Char;
				if($keyChar > 90)
					$keyChar -= 26;
				$vKey = $vKey.chr($keyChar);
			}
			echo $message['id']."_ ;_".$text."_ ;_".$vKey."_ ;_".$message['datum']."_ ;_".$message['chat']."_ ;_".$message['absender']."_ next_";
		}
	}
		
	$db->close();

?>
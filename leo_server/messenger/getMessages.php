<?php

	if ($_GET['key'] != 5453)
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

	$array = [];
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

	foreach ($array as $chat)
		foreach ($chat as $message)
			echo ($message['id']."_;_".$message['nachricht']."_;_".$message['datum']."_;_".$message['chat']."_;_".$message['absender']."_nextMessage_");
		
	$db->close();

?>
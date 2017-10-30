<?php

	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
	  	die("-Connection failed: ".$db->connect_error);

	$query = "SELECT * FROM Survey";
	$result = $db->query($query);

	if ($result !== false) {
			while ($row = $result->fetch_assoc()) {

		    $id = $row['owner'];
		    $query = "SELECT content, id FROM Answers WHERE survey=".$id." ORDER BY id ASC";
		    $answers = $db->query($query);

		    $query = "SELECT uname FROM Users WHERE uid = ".$id;
		    $res = $db->query($query);


		    if($res === false)
		      die($db->error);

		    echo $res->fetch_assoc()['uname']."_;_".$row['title']."_;_".$row['description']."_;_".$row['to']."_;_".$row['multiple']."_;_".$id;

		    while ($ansArray = $answers->fetch_assoc()) {
		        echo "_;_".$ansArray['id']."_;_".$ansArray['content'];
		    }

		    echo "_next_";
		}
	}


	$db->close();

?>

<?php
	
	require_once('../dbconfig.php');

	$db = new mysqli(dbhost, dbuser, dbpass, dbname);

	if ($db->connect_error)
    	die("-connection failed: ".$db->connect_error);

	//mitgegebene Werte über get: uid

	$uid = $db->real_escape_string($_GET['uid']);

	$queryIch = "SELECT DAY(vdate) as vday, MONTH(vdate) as vmonth, YEAR(vdate) as vyear, AVG(vid) as vvalue FROM Vote, Users WHERE Vote.uid = ".$uid." GROUP BY vdate ORDER BY vdate DESC";

	$result = $db->query($queryIch);
	if ($result === false)
		die("-error in queryIch");

	if ($result->num_rows == 0)
		echo "-1;".date("d.m.Y");
	while($row = $result->fetch_assoc())
		echo $row['vvalue'].";".$row['vday'].".".$row['vmonth'].".".$row['vyear']."_next_";
	
	$querySchueler = "SELECT DAY(vdate) as vday, MONTH(vdate) as vmonth, YEAR(vdate) as vyear, AVG(vid) as vvalue FROM Vote, Users WHERE Users.uid = Vote.uid AND Users.upermission != 2 GROUP BY vdate ORDER BY vdate DESC";

	$result = $db->query($querySchueler);
	if ($result === false)
		die("-error in querySchueler");

	echo "_abschnitt_";
	if ($result->num_rows == 0)
		echo "-1;".date("d.m.Y");
	while($row = $result->fetch_assoc())
		echo $row['vvalue'].";".$row['vday'].".".$row['vmonth'].".".$row['vyear']."_next_";

	$queryLehrer = "SELECT DAY(vdate) as vday, MONTH(vdate) as vmonth, YEAR(vdate) as vyear, AVG(vid) as vvalue FROM Vote, Users WHERE Users.uid = Vote.uid AND Users.upermission = 2 GROUP BY vdate ORDER BY vdate DESC";

	$result = $db->query($queryLehrer);
	if ($result === false)
		die("-error in queryLehrer");

	echo "_abschnitt_";
	if ($result->num_rows == 0)
		echo "-1;".date("d.m.Y");
	while($row = $result->fetch_assoc())
		echo $row['vvalue'].";".$row['vday'].".".$row['vmonth'].".".$row['vyear']."_next_";

	$queryAlle = "SELECT DAY(vdate) as vday, MONTH(vdate) as vmonth, YEAR(vdate) as vyear, AVG(vid) as vvalue FROM Vote GROUP BY vdate ORDER BY vdate DESC";

	$result = $db->query($queryAlle);
	if ($result === false)
		die("-error in queryAlle");

	echo "_abschnitt_";
	if ($result->num_rows == 0)
		echo "-1;".date("d.m.Y");
	while($row = $result->fetch_assoc())
		echo $row['vvalue'].";".$row['vday'].".".$row['vmonth'].".".$row['vyear']."_next_";

	$db->close();

?>
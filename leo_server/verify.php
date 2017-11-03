<?php

	$name = $_SERVER['REMOTE_USER'];

	$hex = "";
	for ($i = 0; $i < 6; $i++) {
		$char = (ord($name[$i]) - 97) % 16;
		if ($char > 9) {
			$hex = $hex . chr(55 + $char);
		} else {
			$hex = $hex . $char;
		}
		if (strlen($name) == 12) {
			$hex = $hex . $name[6 + $i];
		}
	}

	echo $hex;

?>
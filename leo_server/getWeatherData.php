<?php

foreach(glob(str_replace("getWeatherData.php", "", __FILE__).'*.*') as $file) {
	
	if(strpos("x".$file, 'weather_leoapp') !== false) {
		
		$date = date('G:i');
	
		$temp = str_replace(".json", "", substr($file, strlen(str_replace("getWeatherData.php", "weather_leoapp", __FILE__))));
		
		$timediff = (strtotime($date) - strtotime($temp))/60;
		
		if(abs($timediff) > 30) {
			unlink($file);	
			newFile($date);
		} else
			$date = str_replace(".json", "", substr($file, strlen(str_replace("getWeatherData.php", "weather_leoapp", __FILE__))));
		
		break;
	}
	
}

if(!isset($date)) {
	$date = date('G:i');
	newFile($date);
}


$file = fopen('weather_leoapp'.$date.'.json', 'r');
echo fread($file, filesize('weather_leoapp'.$date.'.json'));

function newFile($date) {
	$weatherdata = file_get_contents('http://api.openweathermap.org/data/2.5/weather?id=3247449&APPID=83f80b9d2c915771b43557fca0d1326a');
	
	$fp = fopen('weather_leoapp'.$date.'.json', 'w');
	fwrite($fp, $weatherdata);
	fclose($fp);
}

?>
<?php

class MCrypt
        {
                private $iv = '15p60peADF4tT8u8'; #Same as in JAVA
                private $key = 'jHsj1C4XyXpEh7L9m0cVTLPgLU5QfXvh'; #Same as in JAVA


                function __construct()
                {
                }

                function encrypt($str) {

                  //$key = $this->hex2bin($key);    
                  $iv = $this->iv;

                  $td = mcrypt_module_open('rijndael-128', '', 'cbc', $iv);

                  mcrypt_generic_init($td, $this->key, $iv);
                  $encrypted = mcrypt_generic($td, $str);

                  mcrypt_generic_deinit($td);
                  mcrypt_module_close($td);

                  return bin2hex($encrypted);
                }

                function decrypt($code) {
                  //$key = $this->hex2bin($key);
                  $code = $this->hex2bin($code);
                  $iv = $this->iv;

                  $td = mcrypt_module_open('rijndael-128', '', 'cbc', $iv);

                  mcrypt_generic_init($td, $this->key, $iv);
                  $decrypted = mdecrypt_generic($td, $code);

                  mcrypt_generic_deinit($td);
                  mcrypt_module_close($td);

                  return utf8_encode(trim($decrypted));
                }

                protected function hex2bin($hexdata) {
                  $bindata = '';

                  for ($i = 0; $i < strlen($hexdata); $i += 2) {
                        $bindata .= chr(hexdec(substr($hexdata, $i, 2)));
                  }

                  return $bindata;
                }

        }

$servername = "localhost";
$username = "d02566f2";
$password = "leoApp_2017";
$dbname = "d02566f2";


$id = "2SnDS7";

$id2 = substr($auth, 0,6);

if(strcmp($id2, $id) !== 0) {
	echo "+0";
	return;
}

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$idD = $conn->real_escape_string($_GET['id']);
$auth = $conn->real_escape_string($_GET['auth']);

$sql = "SELECT b.user_id as id, b.datum as dateU, s.beschreibung as descr, b.nummer as menu FROM bestellungen b JOIN speisekarte s ON b.ger_id = s.ger_id WHERE b.user_id = '".$idD."'";
$result = $conn->query($sql); 

$resstring = "";

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $resstring .= $row["dateU"]. "_seperator_" . $row["menu"]. "_seperator_" . $row["descr"]. "_next_";
    }
} else {
    echo "-0";
}

$mcrypt = new MCrypt();
$encrypted = $mcrypt->encrypt($resstring);

echo $encrypted;

$conn->close();




?> 
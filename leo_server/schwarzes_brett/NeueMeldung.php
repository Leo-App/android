<?php
?>
<html>
	<head >
		<h1><font face="Showcard Gothic">Schwarzes Brett</font></h1>
	</head>
	<body style="background-image:url(header.jpg)">
		<form action="sendeMeldung.php" method="get">
		<p><font face="Comic Sans Ms" color="#000080">Überschrift: </font><br><input type="text" name="titel" size = "80" style="background-color:transparent; border:2px solid #000000"> </p>
		<p><font face="Comic Sans Ms" color="#000080">Adressat: </font><br>
			<select name="adressat" size="8"> 
				<option>Q2</option> 
				<option>Q1</option> 
				<option>EF</option> 
				<option>9</option> 
				<option>8</option> 
				<option>7</option>
				<option>6</option>
				<option>5</option>
			</select> </p>
		<p></p>
		<p><font face="Comic Sans Ms" color="#000080">Inhalt: </font></p>
		<textarea name = "inhalt" style = "background-color:transparent; border:2px solid #000000"cols="60" rows="12">
		</textarea>
			<p></p>
			<script type="text/javascript" src="http://code.jquery.com/jquery-2.1.4.min.js"></script> 
            <script src="//cdn.jsdelivr.net/webshim/1.14.5/polyfiller.js"></script>
            <script>
            webshims.setOptions('forms-ext', {types: 'date'});
            webshims.polyfill('forms forms-ext');
            $.webshims.formcfg = {
            en: {
            dFormat: '-',
            dateSigns: '-',
            patterns: {
            d: "yyyy-mm-dd"
            }
            }
            };
            </script>
			<p><font face="Comic Sans Ms" color="#000080">Ablaufdatum: </font><br> <input type="date" name="ablaufdatum" style="background-color:transparent"></p>
		<input type ="submit" value ="absenden"/>
		</form>
	</body>
</html>
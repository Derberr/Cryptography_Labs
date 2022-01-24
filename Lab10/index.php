<html>
<head>
<title>Encryption and Decryption</title>
</head>
<body>
<?php
$cipher = 'AES-128-CBC';
$plaintext = 'John Patrick';
$key = 'thebestsecretkey';
$iv = 'onepossiblevalue';
$ciphertext = openssl_encrypt($plaintext, $cipher, $key,
OPENSSL_RAW_DATA, $iv);
$plaintext2 = openssl_decrypt($ciphertext, $cipher, $key,
OPENSSL_RAW_DATA, $iv);
echo '<p><b>Plaintext:</b> ' . $plaintext . '</p>';
echo '<p><b>Key:</b> ' . $key . '</p>';
echo '<p><b>Ciphertext:</b> ' . bin2hex($ciphertext) . '</p>';
echo '<p><b>Plaintext (decrypted):</b> ' . $plaintext2 . '</p>';
?>
</body>
</html>
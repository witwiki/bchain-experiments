/*
  This block of code generates a private key to the testnet
*/

/*
// Requiring the bitcore library
var bitcore = require('bitcore');

// Inside the library we are calling the function PrivateKey to use the testnet network
var privateKey = new bitcore.PrivateKey(bitcore.Networks.testnet);

// Take the privateKey variable (above) and send it to the testnet using 'toWIF'
var exported = privateKey.toWIF(bitcore.Networks.testnet);

// Show Private Key (in reality this is only seen by the true owner)
console.log(privateKey)

// Show on the console
console.log(exported);


// This block of code, generates a public key using the private key above and then generates a default Bitcoin Testnet address from that public key
var publicKey = bitcore.PublicKey(privateKey);
var address = new bitcore.Address(publicKey, bitcore.Networks.testnet);

// console log the public key and the address
console.log(publicKey);
console.log(address);

*/


/* 
    Pseudocode to improving the above code:
    (1) Create a new Private Key in the various formats
    (2) Create from this a Public Key in the various formats
    (3) Create a Bitcoin Address
    (4) Validate everything

     Ref: bitcoin.io is an awesome resource that helped with the videos

*/


// Requiring the bitcore library
var bitcore = require('bitcore');

for(count = 1; count < 11; count++){

	// Inside the library we are calling the function PrivateKey to use the testnet network
	var privateKey = new bitcore.PrivateKey(bitcore.Networks.testnet);

	// Take the privateKey variable (above) and send it to the testnet using 'toWIF'
	var exported = privateKey.toWIF(bitcore.Networks.testnet);

	// This block of code, generates a public key using the private key above and then generates a default Bitcoin Testnet address from that public key
	var publicKey = bitcore.PublicKey(privateKey);
	var address = new bitcore.Address(publicKey, bitcore.Networks.testnet);

	console.log("Run Number " + count);
	console.log("Encrypted Private Key (WIF): " + exported);
	console.log("Public Key: " + publicKey);
	console.log("Wallet Address: " + address + "\n");

      }


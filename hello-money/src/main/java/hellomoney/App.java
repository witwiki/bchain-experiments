/**
 * @author: witwiki
 * @date: 16-09-2016
 * @summary: Simple Bitcoin receive code
 * Reference: http://bitcoinnewschannel.com/2016/01/19/bitcoin-programming-with-bitcoinj-part-i/
 */
package hellomoney;

// Core libraries to access basic Bitcoin classes (such as classes for wallets and keys)
import com.google.bitcoin.core.*;
// Classes for storing the blockchain (called a block store in BitcoinJ lingo)
import com.google.bitcoin.store.*;
// The DnsDiscovery class, which helps us find other nodes participating in the Bitcoin network
import com.google.bitcoin.discovery.DnsDiscovery;
// The java.io.File class because we’ll be writing our block store to a file
import java.io.File;
// The java.math.BigInteger class to work with big integers
import java.math.BigInteger;


public class App 
{
    public static void main( String[] args ) throws BlockStoreException
    {
//        System.out.println( "Hello World!" );

        /**
         *  Initializes the Java object we need from the bitcoinJ library
         */
        //  Fetch the network parameters for the TestNet Bitcoin network
        NetworkParameters params = NetworkParameters.testNet();
        //  Create a new, empty wallet that we’ll set up to receive our coins
        Wallet wallet = new Wallet(params);
        //  Generates a new Private key (using EC encryption)
        ECKey key = new ECKey();

        System.out.println("Public address: " + key.toAddress(params).toString());
        System.out.println("Private key: " + key.getPrivateKeyEncoded(params).toString());
        //  Add the private key to the wallet
        wallet.addKey(key);

        /**
         *  Initializes a new blockchain
         */
        //  Blockchains consume lots of space, we’ll write it to a file named my-blockchain
        File file = new File("my-blockchain");
        //  Create a block store, which is an object that manages the data for our copious blockchain data
        //  Simplified Payment Verification (SPV)
        SPVBlockStore store = new SPVBlockStore(params, file);
        BlockChain chain = new BlockChain(params, wallet, store);

        /**
         *  Connecting to the Bitcoin Network
         */
        //  Create a PeerGroup object that manages these connections
        PeerGroup peerGroup = new PeerGroup(params, chain);
        /**
         * Choose some random peers to connect to
         * (We do this by adding a peer discovery algorithm to the PeerGroup.
         * The DnsDiscovery class basically uses the URLs of some well-established and
         * trusted nodes as a starting point to discover peers that are willing to accept new connections)
         */
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
        //  Add our wallet to the PeerGroup object
        peerGroup.addWallet(wallet);
        //  Will find and connect to some nodes and perform the appropriate handshake operations via network sockets.
        peerGroup.start();
        //  We request that the peers send us the blockchain so we can become a fully functional node.
        peerGroup.downloadBlockChain();

        /**
         *  Listening for New Money
         */
        wallet.addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, BigInteger prevBalance, BigInteger newBalance) {
                System.out.println("Hello Money! Balance: " + newBalance + " satoshis");
            }
        });

        //  We put the program into an infinite loop, so the program continues running as we wait for money to arrive
        while (true){}

    }
}

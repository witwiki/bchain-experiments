/**
 * @author: witwiki
 * @date: 17-09-2016
 * @summary: Simple Bitcoin send (Tx) code
 * Reference: http://bitcoinnewschannel.com/2016/01/19/bitcoin-programming-with-bitcoinj-part-2/
 */
package byebyemoney;

import com.google.bitcoin.core.*;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.MemoryBlockStore;

import java.util.concurrent.ExecutionException;
import java.math.BigInteger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    /**
     * The main function simply rethrows a variety of different exceptions for operations that can go wrong
     * as we attempt to send our CC. These include the following exceptions:
     *
     * BlockStoreException: This is thrown when the block store cannot be created (most commonly, this
     *                      happens with block store types that write to a file when something corrupts the file).
     * AddressFormatException: This is thrown when the format of the address is incorrect.
     * InterruptedException: This is thrown when network connection problems occur.
     * ExecutionException: This is thrown when we’re using a future object and an exception occurs in the
     * other thread (as happens when we check for completion of the transaction broadcast).
     *
     * In a more sophisticated Bitcoin app, you should catch all of these exception types separately and add
     * more descriptive error messages for your app’s users.
     */
            throws BlockStoreException, AddressFormatException, InterruptedException, ExecutionException
    {
//        System.out.println( "Hello World!" );
        NetworkParameters params = NetworkParameters.testNet();
        Wallet wallet = new Wallet(params);
        /**
         * This private address is from the results of the hello-money project.
         *
         * In a 'real' application, this would be the private key within the wallet sending the
         * crypto-currency (cc).
         */
        DumpedPrivateKey key = new DumpedPrivateKey(params, "cVvchpokHedZjTjPB4aBrdXFJi3C46TQw1Yx4siQSVoSEkhJU8kh");
        //  In this case we're adding the private key that is sending the cc
        wallet.addKey(key.getKey());

        /**
         * The block store {@link MemoryBlockStore} (instead of {@link SPVBlockStore} won’t create a file,
         * but by using it, our program will need to re-download the blockchain every time the program runs.
         * (This also guarantees that bitcoinj will assign the correct balance to the wallet.
         */
        BlockChain chain = new BlockChain(params, wallet, new MemoryBlockStore(params));

        /**
         * We initialize the PeerGroup object, we call {@link addWallet} to add our wallet to the peer group.
         * By doing so, bitcoinj keeps the wallet balance in sync with any new transactions that appear in the
         * Bitcoin network as the program is running
         */
        PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
        peerGroup.addWallet(wallet);
        peerGroup.start();
        peerGroup.downloadBlockChain();

        //  Get Current balance and display it
        BigInteger balance = wallet.getBalance();
        System.out.println("Wallet balance " + balance);

        /**
         * The @destinationAddress in this case is the testNet address but in a 'real' app this would be coming from
         * the QR code or the sender would type it into his/her input box when sending the cc.
         */
        Address destinationAddress = new Address(params, "2N3gseZp2bBz68zgPkgkwWUdW1b31h2ntnm");
        //  Tx fee sent to the network
        BigInteger fee = BigInteger.valueOf(10000);
        /**
         * Create a {@link SendRequest to the network.
         * The {@link com.google.bitcoin.core.Wallet.SendRequest} object is structure containing the
         * {@link destinationAddress} and the {@link balance} minus the {@link fee}
         */
        Wallet.SendRequest req = Wallet.SendRequest.to(destinationAddress, balance.subtract(fee));
        req.fee = fee;
        Wallet.SendResult result = wallet.sendCoins(peerGroup, req);

        /**
         * Ensuring the Crypto-currency Transmission occurs
         *
         * Trying to send more CCs than we have (or) if the fee is inadequate (or) if the Internet connection drops
         * out at the wrong moment, the CC might never be accepted by the network. Therefore, we need to
         * wait and ensure that the CC we send is transmitted to the network.
         */
        if (result != null) {
            /**
             *   Retrieves a Java future object, which indicates that the send transaction
             *   has been properly broadcast to the network.
             *   (A standard in Java, futures retrieve information about a separate execution thread in this
             *   case the thread that monitors communication with the Bitcoin network.)
             */
            result.broadcastComplete.get();
            System.out.println("Bitcoins sent");
        } else {
            System.out.println("Bitcoin were not sent");
        }
    }
}

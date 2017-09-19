package ru.iteco.ip.blockchain.documentissuer.servlets;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Created by Scorpio on 23.07.2017.
 */
@WebServlet(
        name = "сервлет посылающий",
        description = "хрень каккая-то",
        urlPatterns = "/sendDocument")
public class SendDocumentHttpServlet extends javax.servlet.http.HttpServlet {
    private static final String XML_TEXT = "какая-то чушь про паровозы";
    private static final String ISSUER_ETH_ADDR = "0x5772dd922b359d5fc4fcdb9a3807eebbd94c493e";
    private static final String RECIVER_ETH_ADDR = "0xfeaDBd9FbC5989e27de6484983C25042952d1e0d";
    static final BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);
    static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder buffer = new StringBuilder();
        String sReq;
        Map<String, String[]> parMap = request.getParameterMap();
        try( BufferedReader br = request.getReader()){
            while ((sReq = br.readLine()) != null){
                buffer.append(sReq);
            }
        }
        String form  = request.getParameter("form");
        Web3j web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        //Web3j web3 = Web3j.build(new HttpService("http://172.26.34.189:8545/"));
        //Web3j web3 = Web3j.build(new HttpService("http://172.26.34.189:8545/"));
        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        Credentials credentials;
        try {
            credentials = WalletUtils.loadCredentials(
                    "cit",
                    "C:\\etherium\\masterEthNode\\keystore\\UTC--2017-07-22T15-48-20.952028300Z--5772dd922b359d5fc4fcdb9a3807eebbd94c493e");
            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                    ISSUER_ETH_ADDR, DefaultBlockParameterName.LATEST).send();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger value = Convert.toWei("0.5", Convert.Unit.ETHER).toBigInteger();
            /* Это кодирование в BASE64*/
            String encoded = Base64.getEncoder().encodeToString(form.getBytes(StandardCharsets.UTF_8));
            System.out.println("BASE64 XML IS:" +encoded);
            String data = asciiToHex(encoded);
            System.out.println("HEXED XML IS:" +data);
            RawTransaction rtx = RawTransaction.createTransaction(
                    nonce, GAS_PRICE, GAS_LIMIT, RECIVER_ETH_ADDR, value, data);

            byte[] signedMessage = TransactionEncoder.signMessage(rtx, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
            String transactionHash = ethSendTransaction.getTransactionHash();
            System.out.println("TRNHASH::" + transactionHash);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("TRNHASH::" + transactionHash);
        } catch (CipherException ce) {
            ce.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error is: " +ce );
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error is: " +e );

        }
        System.out.println("sended");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private static String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

}

package ru.iteco.ip.blockchain.documentissuer.servlets;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by Administrator on 27.07.2017.
 */
@WebServlet(
        name = "сервлет искающий",
        description = "хрень каккая-то",
        urlPatterns = "/searchDocument")
public class SearchDocHttpServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String txtRes ="";
        try {
            String trnId = request.getParameter("trnId");
            Web3j web3 = Web3j.build(new HttpService());
            txtRes = "<form id=\"successForm\">\n" +
                    "        номер транзакции: " + trnId + "\n" +
                    "        <input id=\"skipBttn\" type=\"submit\" value=\"забить\"> </input>\n" +
                    "    </form>";
            Transaction trn = web3.ethGetTransactionByHash(trnId).send().getTransaction().get();
            String inputData = hexToAscii(trn.getInput());
            byte[] a = Base64.getDecoder().decode(inputData);
            String data = new String(a, "UTF-8");

            System.out.print("");
            txtRes =  "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Поиск документа по номеру транзакции</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    data +
                    "\n" +
                    "</body>\n" +
                    "</html>";

        } catch (Exception e) {
            StringWriter err = new StringWriter();
            e.printStackTrace(new PrintWriter(err));
            String errs = err.toString();
            txtRes  = "<form id=\"errform\">\n" +
                    "        ошибка в ГЕТ запросе:\n" + errs +
                    "        <input id=\"skipBttn\" type=\"submit\" value=\"забить\"> </input>\n" +
                    "    </form>";

        }finally {
            response.setContentType( "text/html" );
            response.setCharacterEncoding( "UTF-8" );
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(txtRes);
            response.getWriter().flush();
            response.getWriter().close();
        }

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

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        if(hexStr.startsWith("0x")){
            hexStr = hexStr.replace("0x", "");
        }

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}

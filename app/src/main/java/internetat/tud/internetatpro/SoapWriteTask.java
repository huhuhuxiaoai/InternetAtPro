package internetat.tud.internetatpro;

import android.util.Log;
import android.view.View;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


public class SoapWriteTask extends SoapTask {

    private static final String SOAP_ACTION =
            "\"http://opcfoundation.org/webservices/XMLDA/1.0/Write\"";
    private static final String STOP_PUMP_REQUEST =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" \n" +
            "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" \n" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
            "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "    <SOAP-ENV:Body>\n" +
            "        <m:Write xmlns:m=\"http://opcfoundation.org/webservices/XMLDA/1.0/\">\n" +
            "            <m:Options ReturnErrorText=\"true\" ReturnDiagnosticInfo=\"true\" \n" +
            "                ReturnItemTime=\"true\" ReturnItemPath=\"true\" \n" +
            "                ReturnItemName=\"true\"/>\n" +
            "            <m:ItemList>\n" +
            "                <m:Items ItemName=\"Schneider/Start_Umpumpen_FL\">\n" +
            "		             <m:Value xsi:type=\"xsd:boolean\">false</m:Value>\n" +
            "		         </m:Items>\n" +
            "            </m:ItemList>\n" +
            "        </m:Write>\n" +
            "    </SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>";
    private static final String START_PUMP_REQUEST =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" \n" +
            "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" \n" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
            "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "    <SOAP-ENV:Body>\n" +
            "        <m:Write xmlns:m=\"http://opcfoundation.org/webservices/XMLDA/1.0/\">\n" +
            "            <m:Options ReturnErrorText=\"true\" ReturnDiagnosticInfo=\"true\" \n" +
            "                ReturnItemTime=\"true\" ReturnItemPath=\"true\" \n" +
            "                ReturnItemName=\"true\"/>\n" +
            "            <m:ItemList>\n" +
            "                <m:Items ItemName=\"Schneider/Behaelter_A_FL\">\n" +
            "		             <m:Value xsi:type=\"xsd:int\">%1$d</m:Value>\n" +
            "		         </m:Items>\n" +
            "                <m:Items ItemName=\"Schneider/Behaelter_B_FL\">\n" +
            "		             <m:Value xsi:type=\"xsd:int\">%2$d</m:Value>\n" +
            "		         </m:Items>\n" +
            "                <m:Items ItemName=\"Schneider/Start_Umpumpen_FL\">\n" +
            "		             <m:Value xsi:type=\"xsd:boolean\">true</m:Value>\n" +
            "		         </m:Items>\n" +
            "            </m:ItemList>\n" +
            "        </m:Write>\n" +
            "    </SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>";
    private static final String XPATH_START_PUMPING =
            "/Envelope/Body/WriteResponse/RItemList/Items[@ItemName='Schneider/Start_Umpumpen_FL']";

    private boolean startPumping, success;

    SoapWriteTask(boolean startPumping, int tankA, int tankB) {
        super(SOAP_ACTION, (startPumping ?
                String.format(START_PUMP_REQUEST, tankA, tankB) : STOP_PUMP_REQUEST));
        this.startPumping = startPumping;
        Log.d("WRITE_REQUEST", (startPumping ?
                String.format(START_PUMP_REQUEST, tankA, tankB) : STOP_PUMP_REQUEST));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (startPumping) {
            MainActivity.getAvailableListener()
                    .setLoadingOverlayVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (startPumping && !success) {
            MainActivity.getAvailableListener()
                    .setLoadingOverlayVisibility(View.GONE);
        }
    }

    @Override
    protected void readSoapResponse(String soapResponse) {
        Log.i("WRITE_RESPONSE", soapResponse);

        // read the SOAP response via Xpath
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream inputStream =
                    new ByteArrayInputStream(soapResponse.getBytes(StandardCharsets.UTF_8.name()));
            Document doc = builder.parse(inputStream);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            XPathExpression expr = xpath.compile(XPATH_START_PUMPING);
            expr.evaluate(doc, XPathConstants.STRING);

            // no exception -> success
            success = true;
        }
        catch (Exception e) {
            // exception -> no success
            success = false;
            Log.e("XML_PARSE", e.toString());
        }
    }

    @Override
    protected void readErrorResponse(String errorResponse) {
        success = false;
        super.readErrorResponse(errorResponse);
    }
}

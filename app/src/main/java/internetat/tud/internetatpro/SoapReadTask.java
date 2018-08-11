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

public class SoapReadTask extends SoapTask {

    private static final String SOAP_ACTION =
            "\"http://opcfoundation.org/webservices/XMLDA/1.0/Read\"";
    private static final String SOAP_REQUEST =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<SOAP-ENV:Envelope\n" +
            "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "    <SOAP-ENV:Body>\n" +
            "        <m:Read xmlns:m=\"http://opcfoundation.org/webservices/XMLDA/1.0/\">\n" +
            "            <m:Options ReturnErrorText=\"false\" ReturnDiagnosticInfo=\"false\" \n" +
            "                ReturnItemTime=\"false\" ReturnItemPath=\"false\" \n" +
            "                ReturnItemName=\"true\"/>\n" +
            "            <m:ItemList>\n" +
            "                <m:Items ItemName=\"Schneider/Fuellstand1_Ist\"/>\n" +
            "                <m:Items ItemName=\"Schneider/Fuellstand2_Ist\"/>\n" +
            "                <m:Items ItemName=\"Schneider/Fuellstand3_Ist\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LH1\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LH2\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LH3\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LL1\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LL2\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LL3\"/>\n" +
            "                <m:Items ItemName=\"Schneider/Start_Umpumpen_FL\"/>\n" +
            "                <m:Items ItemName=\"Schneider/Behaelter_A_FL\"/>\n" +
            "                <m:Items ItemName=\"Schneider/Behaelter_B_FL\"/>\n" +
            "            </m:ItemList>\n" +
            "        </m:Read>\n" +
            "    </SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>";
    private static final String XPATH_LEVEL1 =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/Fuellstand1_Ist']/Value";
    private static final String XPATH_LEVEL2 =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/Fuellstand2_Ist']/Value";
    private static final String XPATH_LEVEL3 =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/Fuellstand3_Ist']/Value";
    private static final String XPATH_LL1 =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LL1']/Value";
    private static final String XPATH_LL2 =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LL2']/Value";
    private static final String XPATH_LL3 =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LL3']/Value";
    private static final String XPATH_LH1 =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LH1']/Value";
    private static final String XPATH_LH2 =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LH2']/Value";
    private static final String XPATH_LH3 =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LH3']/Value";
    private static final String XPATH_PUMPING =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/Start_Umpumpen_FL']/Value";
    private static final String XPATH_TANK_A =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/Behaelter_A_FL']/Value";
    private static final String XPATH_TANK_B =
            "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/Behaelter_B_FL']/Value";

    private float level1, level2, level3;
    private boolean ll1, ll2, ll3, lh1, lh2, lh3;
    private boolean pumping;
    private int tankA, tankB;
    private boolean lostConnection;

    SoapReadTask() {
        super(SOAP_ACTION, SOAP_REQUEST);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // update activity
        Available availableListener = MainActivity.getAvailableListener();
        availableListener.hideStartOverlay();
        if (lostConnection) availableListener.setNoConnectionOverlayVisibility(View.VISIBLE);
        else {
            availableListener.setNoConnectionOverlayVisibility(View.GONE);
            availableListener.updateTankLevels(level1, level2, level3);
            availableListener.updateCapacitiveSensorStates(ll1, ll2, ll3, lh1, lh2, lh3);
            availableListener.updatePumpingState(pumping, tankA, tankB);
            if (pumping) availableListener.setLoadingOverlayVisibility(View.GONE);
        }
    }

    @Override
    protected void readSoapResponse(String soapResponse) {
        Log.d("SOAP_READ_RESPONSE", soapResponse);
        lostConnection = false;

        // read the SOAP response via Xpath
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream inputStream =
                    new ByteArrayInputStream(soapResponse.getBytes(StandardCharsets.UTF_8.name()));
            Document doc = builder.parse(inputStream);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            // get the tank levels
            XPathExpression expr = xpath.compile(XPATH_LEVEL1);
            level1 = (float) ((double) expr.evaluate(doc, XPathConstants.NUMBER));
            expr = xpath.compile(XPATH_LEVEL2);
            level2 = (float) ((double) expr.evaluate(doc, XPathConstants.NUMBER));
            expr = xpath.compile(XPATH_LEVEL3);
            level3 = (float) ((double) expr.evaluate(doc, XPathConstants.NUMBER));

            // get the capacitive sensor states
            expr = xpath.compile(XPATH_LL1);
            ll1 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LL2);
            ll2 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LL3);
            ll3 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LH1);
            lh1 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LH2);
            lh2 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LH3);
            lh3 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));

            // get pumping state
            expr = xpath.compile(XPATH_PUMPING);
            pumping = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_TANK_A);
            tankA = (int) ((double) expr.evaluate(doc, XPathConstants.NUMBER));
            expr = xpath.compile(XPATH_TANK_B);
            tankB = (int) ((double) expr.evaluate(doc, XPathConstants.NUMBER));
        }
        catch (Exception e) {
            Log.e("XML_PARSE", e.toString());
        }
    }

    @Override
    protected void readErrorResponse(String errorResponse) {
        super.readErrorResponse(errorResponse);
        lostConnection = true;
    }
}

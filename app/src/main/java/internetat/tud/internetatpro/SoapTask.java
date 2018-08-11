package internetat.tud.internetatpro;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class SoapTask extends AsyncTask<Void, Void, Void> {

    private static final String SOAP_URL = "http://141.30.154.211:8087/OPC/DA";
    private final String SOAP_ACTION;
    private final String SOAP_REQUEST;

    private boolean isSoapResponse;

    SoapTask(String soapAction, String soapRequest) {
        SOAP_ACTION = soapAction;
        SOAP_REQUEST = soapRequest;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String response = sendSoapRequest();

        if (isSoapResponse) {
            readSoapResponse(response);
        }
        else {
            readErrorResponse(response);
        }

        return null;
    }

    private String sendSoapRequest() {
        URL url;
        HttpURLConnection connection = null;
        String response = "";
        isSoapResponse = false;

        // send the SOAP request via HTTP
        try {
            url = new URL(SOAP_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("SOAPAction", SOAP_ACTION);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(SOAP_REQUEST);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream;
            if (connection.getResponseCode() <= 400) {
                inputStream = connection.getInputStream();
                isSoapResponse = true;
            }
            else {
                inputStream = connection.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder responseBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
                responseBuilder.append('\n');
            }
            reader.close();
            response = responseBuilder.toString();
        }
        catch (Exception e) {
            Log.e("CONNECTION", e.toString());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    protected void readErrorResponse(String errorResponse) {
        Log.i("ERROR_RESPONSE", errorResponse);
    }

    protected abstract void readSoapResponse(String soapResponse);
}

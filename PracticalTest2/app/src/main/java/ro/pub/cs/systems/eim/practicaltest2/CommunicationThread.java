package ro.pub.cs.systems.eim.practicaltest2;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    // Constructor of the thread, which takes a ServerThread and a Socket as parameters
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }
    // run() method: The run method is the entry point for the thread when it starts executing.
    // It's responsible for reading data from the client, interacting with the server,
    // and sending a response back to the client.
    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client");

            //todo: Read other information from Server
            // Read the informationType values sent by the client
            String informationType = bufferedReader.readLine();
            if (informationType == null || informationType.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client!");
                return;
            }

            // It checks whether the serverThread has already received the information.
            //todo: Delete HashMap if no need for caching AND change KEY read from Server
            HashMap<String, Information> data = serverThread.getData();
            Information information = null;
            String key = "";
            if (data.containsKey(key)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                information = data.get(key);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";

                // make the HTTP request to the web service
                //todo: Change if there is no need for API Key
                //HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + "?q=" + key + "&APPID=" + Constants.WEB_SERVICE_API_KEY + "&units=" + Constants.UNITS);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else Log.i(Constants.TAG, pageSourceCode);

                // Parse the page source code into a JSONObject and extract the needed information
                JSONObject content = new JSONObject(pageSourceCode);
                //todo: Add JSON data from website to caching data

//                JSONArray weatherArray = content.getJSONArray(Constants.WEATHER);
//                JSONObject weather;
//                StringBuilder condition = new StringBuilder();
//                for (int i = 0; i < weatherArray.length(); i++) {
//                    weather = weatherArray.getJSONObject(i);
//                    condition.append(weather.getString(Constants.MAIN)).append(" : ").append(weather.getString(Constants.DESCRIPTION));
//
//                    if (i < weatherArray.length() - 1) {
//                        condition.append(";");
//                    }
//                }
//                JSONObject main = content.getJSONObject(Constants.MAIN);
//                String temperature = main.getString(Constants.TEMP);
//                String pressure = main.getString(Constants.PRESSURE);
//                String humidity = main.getString(Constants.HUMIDITY);
//                JSONObject wind = content.getJSONObject(Constants.WIND);
//                String windSpeed = wind.getString(Constants.SPEED);
                //todo: Change constructor and use setData() from below
                // Create a Information object with the information extracted from the JSONObject
                //Information information_new = new Information(temperature, windSpeed, condition.toString(), pressure, humidity);

                // Cache the information for the given city
                //serverThread.setData(key, information_new);
            }

            if (information == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }

            //todo:Change result and send the information back to the client
            String result = "";
//            switch (informationType) {
//                case Constants.ALL:
//                    result = weatherForecastInformation.toString();
//                    break;
//                case Constants.TEMPERATURE:
//                    result = weatherForecastInformation.getTemperature();
//                    break;
//                case Constants.WIND_SPEED:
//                    result = weatherForecastInformation.getWindSpeed();
//                    break;
//                case Constants.CONDITION:
//                    result = weatherForecastInformation.getCondition();
//                    break;
//                case Constants.HUMIDITY:
//                    result = weatherForecastInformation.getHumidity();
//                    break;
//                case Constants.PRESSURE:
//                    result = weatherForecastInformation.getPressure();
//                    break;
//                default:
//                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
//            }

            //todo: Send the result back to the client + FLUSH
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}

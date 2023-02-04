package com.example.playernk;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetRequest extends AsyncTask<Void, Void, String> {

    static final String REQUEST_METHOD = "GET";
    static final int READ_TIMEOUT = 15000;
    static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(Void... params){
        String result;
        String inputLine;

        try {
            // connect to the server
            URL myUrl = new URL("http://188.120.243.243:3001/");
            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();

            // get the string from the input stream
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();

        } catch(IOException e) {
            e.printStackTrace();
            result = "error";
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
        }

        return result;
    }

    protected void onPostExecute(String result){
        super.onPostExecute(result);
//        tvServerResponse.setText(result);
    }
}

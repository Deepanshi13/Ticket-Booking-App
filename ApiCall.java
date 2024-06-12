package com.getepay.smartcitycheckin;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by aber on 18/05/15.
 */
public class ApiCall {

    private String url="";
    private ApiCallListener callback;

    private boolean success = false;
    private String result = "";

    private int timeout = 0;

    private int id;
    private String lk="";

    public ApiCall(String url, ApiCallListener callback, int id, int tout){
        this.callback = callback;
        this.url = url;
        this.id = id;
        this.timeout = tout;
        SendData sd = new SendData();
        sd.execute();
    }

    public ApiCall(String url, ApiCallListener callback, int id){
        this.callback = callback;
        this.url = url;
        this.id = id;
        this.timeout = 0;
        SendData sd = new SendData();
        sd.execute();
    }

    public ApiCall(String url, ApiCallListener callback, int id, int tout, String license_key) {
        this.callback = callback;
        this.url = url;
        this.id = id;
        this.timeout = 0;
        this.lk = license_key;
        SendData sd = new SendData();
        sd.execute();
    }

    public String convertStreamToString(InputStream is)
            throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try
            {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1)
                {
                    writer.write(buffer, 0, n);
                }
            }
            finally
            {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    private class SendData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            if (url.equals("")) return null;

            success = false;

            URL urll = null;
            InputStream in = null;
            HttpURLConnection urlConnection = null;
            try {
                urll = new URL(url);
                Log.d("ApiCall", "URL: " + urll.toString());

                urlConnection = (HttpURLConnection) urll.openConnection();
                if (timeout > 0)
                urlConnection.setConnectTimeout(timeout);

                if (!ApiCall.this.lk.equals("")) {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    String postParameters = "license_key=" + URLEncoder.encode(ApiCall.this.lk, "utf-8");
                    Log.d("ApiCall", "API Request: " + postParameters);

                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(postParameters);

                    writer.flush();
                    writer.close();
                    os.close();
                }
                in = new BufferedInputStream(urlConnection.getInputStream());
                result = convertStreamToString(in);
                Log.d("ApiCall", "API Response: " + result);
                success = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException) {
                    result = "timeout";
                }
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callback.call(result, success, id);
        }

        private String convertStreamToString(InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

}

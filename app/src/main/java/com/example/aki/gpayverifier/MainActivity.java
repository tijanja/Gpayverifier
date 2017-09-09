package com.example.aki.gpayverifier;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText transRef;
    private String current = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transRef = (EditText) findViewById(R.id.tran_ref);
        transRef.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(!s.toString().equals(current)){
                    transRef.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                    current = formatted.replace("$"," ");
                    transRef.setText(current);
                    transRef.setSelection(current.length());

                    transRef.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

    }

    public void verifyTransRef(View v)
    {
        new VerifyAsyn().execute();

    }

    private class VerifyAsyn extends AsyncTask<String,String,String>
    {

        /*@Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"not connected",Toast.LENGTH_LONG).show();
        }*/

        @Override
        protected String doInBackground(String... params)
        {
            return verifyAction();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            callbackUI(s);

        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);
        }

    }

    public String verifyAction()
    {

        URL url = null;
        String result ="";

        try
        {
            String urlParameters ="controller=" + URLEncoder.encode("Merchant", "UTF-8") +"&action=" + URLEncoder.encode("PayBills", "UTF-8");
            url = new URL("http://payelectricitybills.com/verifyTrans/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

           // urlConnection.setRequestProperty("Content-Length", "" +Integer.toString(urlParameters.getBytes().length));
            //urlConnection.setRequestProperty("Content-Language", "en-US");

            //urlConnection.setUseCaches(false);


            urlConnection.setDoInput(true);
            //urlConnection.setDoOutput(true);

           // urlConnection.connect();




           //Uri.Builder builder = new Uri.Builder() .appendQueryParameter("transRef", transRef.getText().toString());
            //String query = builder.build().getEncodedQuery();

            OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
            //OutputStream os = urlConnection.getOutputStream();
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            osw.write(urlParameters);
            osw.close();

          /*  PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(urlParameters);
            out.close();*/





            int responseCode=urlConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null)
                {
                    result+=line;
                }

                br.close();
            }
            else
            {
                //Toast.makeText(this,"not connected",Toast.LENGTH_LONG).show();
                result = responseCode+"----";
            }




            //InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        }
        catch (MalformedURLException e)
        {
            Log.e("malformed Error", e.getMessage());
        }
        catch (IOException e)
        {
           // Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();

            Log.e("IO Error", e.getMessage());
        }

        return result;
    }


    public void callbackUI(String s)
    {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
    }
}

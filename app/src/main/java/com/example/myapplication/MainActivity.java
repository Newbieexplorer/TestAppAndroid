package com.example.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TEXT_VIEW = "textView";
    TextView mTextView;
    EditText mEditText;
    Button mButton;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text_view);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mButton = (Button) findViewById(R.id.button);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        assert mButton != null;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUrlBodyTask getUrlBodyTask = new getUrlBodyTask();
                getUrlBodyTask.execute(mEditText.getText().toString());
            }
        });
        if (savedInstanceState != null) {
            CharSequence savedText = savedInstanceState.getCharSequence(TEXT_VIEW);
            mTextView.setText(savedText);
        }

    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(TEXT_VIEW, mTextView.getText());
    }

    class getUrlBodyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            mButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(urls[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            mTextView.setText(result);
            mProgressBar.setVisibility(View.GONE);
            mButton.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setProgress(values[0]);
        }
    }
}

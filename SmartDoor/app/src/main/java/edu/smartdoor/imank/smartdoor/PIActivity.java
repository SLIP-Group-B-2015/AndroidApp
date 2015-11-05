package edu.smartdoor.imank.smartdoor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PIActivity extends AppCompatActivity {

    private static final String LOG_TAG = PIActivity.class.getSimpleName();

    private Button mScanView;
    private EditText mRaspberryPiView;
    private Button mRegisterPIView;
    private String userid = "12";

    private PiRegisterTask mPiReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi);
        mScanView = (Button) findViewById(R.id.bt_scanqr);
        mScanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptScan();
            }
        });

        mRaspberryPiView = (EditText) findViewById(R.id.etRaspberryPi);

        mRegisterPIView = (Button) findViewById(R.id.bt_add_pi);
        mRegisterPIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegisterPI();
            }
        });

    }

    public void attemptScan() {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Bundle received_bundle = intent.getExtras();
            String id = received_bundle.getString("SCAN_RESULT");
            mRaspberryPiView.setText(id);
        }
    }

    public void attemptRegisterPI()
    {
        mPiReg = new PiRegisterTask(mRaspberryPiView.getText().toString());
        mPiReg.execute((Void) null);
    }

    public class PiRegisterTask extends AsyncTask<Void, Void, Boolean>
    {
        private String mRaspberry_pi;

        public PiRegisterTask(String raspberry_pi)
        {
            mRaspberry_pi = raspberry_pi;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return postRequest(mRaspberry_pi);
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            //TODO: go to main page
        }

        @Override
        protected void onCancelled()
        {
            //TODO: go back to Register screen
        }

        public boolean postRequest(String raspberry_pi)
        {

            try {
                SyncHttpClient client = new SyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("userid", userid);
                params.put("raspberryid", raspberry_pi);
                client.post("http://193.62.81.88:5000", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject res) {
                        // called when response HTTP status is "200 OK"
                        //TODO: user registered
                        Log.d(LOG_TAG, res.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        // TODO: Error
                        Log.d(LOG_TAG, res.toString());
                    }
                });
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error in http connection" + e.toString());
            }

            return true;

        }

    }

}

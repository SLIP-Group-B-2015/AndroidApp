package edu.smartdoor.imank.smartdoor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.*;
import cz.msebera.android.httpclient.*;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.*;

public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();

    /*
     * UI References
     */
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mScanView;
    private EditText mRaspberryPiView;
    private Button mRegisterView;

    private UserRegisterTask mRegTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstNameView = (EditText) findViewById(R.id.etFirstName);
        mLastNameView = (EditText) findViewById(R.id.etLastName);
        mUsernameView = (EditText) findViewById(R.id.etUsernameEntry);
        mPasswordView = (EditText) findViewById(R.id.etPasswordEntry);
        mScanView = (Button) findViewById(R.id.bt_scan_qr);
        mScanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptScan();
            }
        });

        mRaspberryPiView = (EditText) findViewById(R.id.etRaspberryPi);

        mRegisterView = (Button) findViewById(R.id.bt_register);
        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
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

    /*
     * attemptRegister
     * Tries to register the user using the user entered details
     * @param none
     * @return void
     */
    public void attemptRegister()
    {
        String first_name;
        String last_name;
        String username;
        String password;
        String raspberry_pi;

        first_name = mFirstNameView.getText().toString();
        last_name =  mLastNameView.getText().toString();
        username = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();
        raspberry_pi = mRaspberryPiView.getText().toString();

        mRegTask = new UserRegisterTask(first_name, last_name, username, password, raspberry_pi);
        mRegTask.execute((Void) null);

    }

    /*
     * UserRegisterTask
     * AsyncTask to show progress bar while registering the user
     * @param firstname, lastname, raspberry pi
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean>
    {
        private String mFirstName;
        private String mLastName;
        private String mUsername;
        private String mPassword;
        private String mRaspberryPi;

        public UserRegisterTask(String first_name, String last_name, String username, String password, String raspberry_pi)
        {
            mFirstName = first_name;
            mLastName = last_name;
            mRaspberryPi = raspberry_pi;
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return postRequest(mFirstName, mLastName, mUsername, mPassword, mRaspberryPi);
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

        public boolean postRequest(String first_name, String last_name, String username, String password, String raspberry_pi)
        {

            try {
                SyncHttpClient client = new SyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("username", username);
                params.put("password", password);
                params.put("raspberry_pi", raspberry_pi);
                client.post("http://www.test.com", params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject res)
                    {
                        // called when response HTTP status is "200 OK"
                        //TODO: user registered
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t)
                    {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        // TODO: Error
                        System.out.println(res);
                    }
                });
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error in http connection" + e.toString());
            }

            return true;

        }
    }

}

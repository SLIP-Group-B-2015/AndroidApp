package edu.smartdoor.imank.smartdoor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.HttpGet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;

import javax.json.Json;
import javax.json.stream.JsonParser;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class LoginActivity extends AppCompatActivity {

    public static final String LOG_TAG = LoginActivity.class.getSimpleName();

    /*
     * UI Reference
     */
    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mSignInView;
    private Button mRegisterView;
    private Button mTimelineView;
    SmartDoor app;

    /*
     * Keep track of Login process
     */
    private UserLoginTask mLoginTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //app = (SmartDoor) getApplicationContext();

        mUsernameView = (EditText) findViewById(R.id.etUsername);
        mPasswordView = (EditText) findViewById(R.id.etPassword);

        mSignInView = (Button) findViewById(R.id.bt_sign_in);
        mSignInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mRegisterView = (Button) findViewById(R.id.bt_scan_qr);
        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: go to new activity register
                startActivity(new Intent(v.getContext(), RegisterActivity.class));
            }
        });
        mTimelineView = (Button) findViewById(R.id.bt_timeline);
        mTimelineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), TimelineActivity.class));
            }
        });
    }

    /*
     * attemptLogin
     * Tries to login with user entered details
     * @param none
     * @return void
     */
    public void attemptLogin()
    {
        String username;
        String password;

        username = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();

        //TODO: Check input for correct format

        mLoginTask = new UserLoginTask(username, password);
        mLoginTask.execute((Void) null);

    }

    /*
     * UserLoginTask
     * AsyncTask to show progress bar while attempting log in
     * @param username, password
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
    {

        private String mUsername;
        private String mPassword;

        public UserLoginTask(String username, String password)
        {
            mUsername = username;
            mPassword = password;

        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return postRequest(mUsername, mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            //TODO: go to main page
        }

        @Override
        protected void onCancelled()
        {
            //TODO: go back to login screen
        }

        public boolean postRequest(String username, String password)
        {
            String json = "";

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet("http://193.62.81.88:5000");

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("event", "LOGIN");
                jsonObject.accumulate("username", username);
                jsonObject.accumulate("password", password);

                json = jsonObject.toString();

                Log.d(LOG_TAG, json);

                StringEntity se = new StringEntity(json);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                get.setEntity(se);

                HttpResponse httpResponse = client.execute(get);

                String response = EntityUtils.toString(httpResponse.getEntity());

                JSONObject obj = new JSONObject(response);
                Log.d(LOG_TAG, obj.getString("userid"));
            }
            catch (Exception e)
            {
                Log.d(LOG_TAG, e.getMessage());
            }

            return true;
        }
    }

}

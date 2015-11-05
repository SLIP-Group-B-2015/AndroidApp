package edu.smartdoor.imank.smartdoor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.*;
import cz.msebera.android.httpclient.*;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

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
    private EditText mEmailView;
    private Button mRegisterView;
    SmartDoor app;
    String uuid;

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
        mEmailView = (EditText) findViewById(R.id.etEmail);

        mRegisterView = (Button) findViewById(R.id.bt_register);
        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

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
        String email;

        first_name = mFirstNameView.getText().toString();
        last_name =  mLastNameView.getText().toString();
        username = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();
        email = mEmailView.getText().toString();

        mRegTask = new UserRegisterTask(first_name, last_name, username, password, email);
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
        private String mEmail;

        public UserRegisterTask(String first_name, String last_name, String username, String password, String email)
        {
            mFirstName = first_name;
            mLastName = last_name;
            mUsername = username;
            mPassword = password;
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return postRequest(mFirstName, mLastName, mUsername, mPassword, mEmail);
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

        public boolean postRequest(String first_name, String last_name, String username, String password, String email)
        {
            String json = "";

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://193.62.81.88:5000");

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("event", "REGISTER");
                jsonObject.accumulate("firstName", first_name);
                jsonObject.accumulate("lastName", last_name);
                jsonObject.accumulate("username", username);
                jsonObject.accumulate("password", password);
                jsonObject.accumulate("email", email);

                json = jsonObject.toString();

                Log.d(LOG_TAG, json);

                StringEntity se = new StringEntity(json);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);

                HttpResponse httpResponse = client.execute(post);

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

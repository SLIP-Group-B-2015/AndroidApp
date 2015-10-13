package edu.smartdoor.imank.smartdoor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    /*
     * UI Reference
     */
    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mSignInView;
    private Button mRegisterView;

    /*
     * Keep track of Login process
     */
    private UserLoginTask mLoginTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            return null;
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
    }

}

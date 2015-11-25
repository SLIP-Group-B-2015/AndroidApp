package edu.smartdoor.imank.smartdoor;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Login Activity
 * @author Iman Majumdar
 *
 * Allows the user to login to the app
 */
public class LoginActivity extends BaseActivity {

    public static final String LOG_TAG = LoginActivity.class.getSimpleName();

    /*
     * UI Reference
     */
    private TextView mTitleView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mSignInView;
    private Button mRegisterView;

    /*
     * Keep track of Login process
     */
    private Tasks.UserLoginTask mLoginTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/Montserrat-Regular.otf");

        mTitleView = (TextView) findViewById(R.id.login_title);
        mTitleView.setTypeface(typeface);

        mUsernameView = (EditText) findViewById(R.id.etUsername);
        mPasswordView = (EditText) findViewById(R.id.etPassword);

        mUsernameView.setTypeface(typeface);
        mPasswordView.setTypeface(typeface);

        mSignInView = (Button) findViewById(R.id.bt_sign_in);
        mSignInView.setTypeface(typeface);
        mSignInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mRegisterView = (Button) findViewById(R.id.bt_register_page);
        mRegisterView.setTypeface(typeface);
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
        Log.d(LOG_TAG, "Attempting Login");

        String username;
        String password;

        username = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();

        //TODO: Check input for correct format

        mLoginTask = new Tasks.UserLoginTask(LoginActivity.this, username, password);
        mLoginTask.execute((Void) null);

        Log.d(LOG_TAG, "Logged In");

    }

}

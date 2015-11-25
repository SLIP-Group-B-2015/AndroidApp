package edu.smartdoor.imank.smartdoor;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Register Activity
 * @author Iman Kalyan Majumdar
 *
 * Allows the user to register with the service
 * */
public class RegisterActivity extends BaseActivity {

    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();

    /*
     * UI References
     */
    private TextView mRegisterTitle;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mEmailView;
    private Button mRegisterView;

    private Tasks.UserRegisterTask mRegTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/Montserrat-Regular.otf");

        mRegisterTitle = (TextView) findViewById(R.id.register_title);
        mFirstNameView = (EditText) findViewById(R.id.etFirstName);
        mLastNameView = (EditText) findViewById(R.id.etLastName);
        mUsernameView = (EditText) findViewById(R.id.etUsernameEntry);
        mPasswordView = (EditText) findViewById(R.id.etPasswordEntry);
        mEmailView = (EditText) findViewById(R.id.etEmail);

        mRegisterView = (Button) findViewById(R.id.bt_register);

        mRegisterTitle.setTypeface(typeface);
        mFirstNameView.setTypeface(typeface);
        mLastNameView.setTypeface(typeface);
        mUsernameView.setTypeface(typeface);
        mPasswordView.setTypeface(typeface);
        mEmailView.setTypeface(typeface);
        mRegisterView.setTypeface(typeface);

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
        Log.d(LOG_TAG, "Attempting Register");

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

        mRegTask = new Tasks.UserRegisterTask(this, first_name, last_name, username, password, email);
        mRegTask.execute((Void) null);

        Log.d(LOG_TAG, "Registered!");

    }

}

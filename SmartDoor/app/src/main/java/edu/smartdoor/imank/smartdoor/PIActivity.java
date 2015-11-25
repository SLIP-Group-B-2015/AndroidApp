package edu.smartdoor.imank.smartdoor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

public class PIActivity extends BaseActivity {

    private static final String LOG_TAG = PIActivity.class.getSimpleName();

    private Button mScanView;
    private EditText mRaspberryPiView;
    private EditText mRaspberryPiNameView;
    private Button mRegisterPIView;
    private String uuid;
    private ListView pi_list;

    private Tasks.PiRegisterTask mPiReg;
    private Tasks.GetAllPI mGetPi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi);

        Log.d(LOG_TAG, "PI Activity Started!");

        uuid = getIntent().getExtras().getString("userid");

        pi_list = (ListView) findViewById(R.id.lv_pi);
        getALLPI(uuid);

        mScanView = (Button) findViewById(R.id.bt_scanqr);
        mScanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptScan();
            }
        });

        mRaspberryPiNameView = (EditText) findViewById(R.id.et_piname);
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
        mPiReg = new Tasks.PiRegisterTask(this, mRaspberryPiView.getText().toString(), mRaspberryPiNameView.getText().toString(), uuid);
        boolean success = false;
        try {
            success = mPiReg.execute((Void) null).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (success)
        {
            Toast.makeText(this, "Registered PI!", Toast.LENGTH_LONG);
            getALLPI(uuid);
        }
        else
        {
            Toast.makeText(this, "Could not register PI, please try again!", Toast.LENGTH_LONG);
        }
    }

    public void getALLPI(String uuid)
    {
        mGetPi = new Tasks.GetAllPI(uuid);
        try {
            ArrayList<PI> pi_s = mGetPi.execute((Void) null).get();
            updateList(pi_s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void updateList(ArrayList<PI> items)
    {
        PiListViewAdapter adapter = new PiListViewAdapter(this, items);
        pi_list.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, TimelineActivity.class).putExtra("userid", uuid));
    }
}

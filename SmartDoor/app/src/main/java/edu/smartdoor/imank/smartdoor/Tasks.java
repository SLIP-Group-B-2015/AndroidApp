package edu.smartdoor.imank.smartdoor;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Tasks
 * @author Iman Kalyan Majumdar
 *
 * This class executes all tasks related to post and get requests to the server in an asynctask
 */
public class Tasks {

    private static final String LOG_TAG = Tasks.class.getSimpleName();

    /*
     * Store Events
     * AsyncTask to show load events into adapter and store in database
     * @param uuid
     */
    public static class StoreEvents extends AsyncTask<Void, Void, Boolean>
    {
        private Context context;
        private String uuid;
        private String option;
        private ArrayList<TimelineItem> items;

        public StoreEvents(String uuid, Context context, String option)
        {
            this.uuid = uuid;
            this.context = context;
            this.option = option;
        }

        @Override
        protected void onPostExecute(Boolean success)
        {
            if (success)
                Log.d(LOG_TAG, "Retrieved events from server: Successful and added new items");
            else
                Log.d(LOG_TAG, "Retrieved events from server: Successful and no new items to be added");
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            items = getRequest(uuid, option);
            boolean db_updated = updateDB(items);
            return db_updated;
        }

        public ArrayList<TimelineItem> getRequest(String uuid, String option)
        {

            ArrayList<TimelineItem> list = new ArrayList<TimelineItem>();

            String json = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet("http://193.62.81.88:5000");

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("event", option);
                jsonObject.accumulate("userid", uuid);
                jsonObject.accumulate("option", 1);

                json = jsonObject.toString();

                Log.d(LOG_TAG, json);

                StringEntity se = new StringEntity(json);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                get.setEntity(se);

                HttpResponse httpResponse = client.execute(get);

                String response = EntityUtils.toString(httpResponse.getEntity());

                JSONObject obj = new JSONObject(response);
                JSONArray events = new JSONArray(obj.getString("eventList"));

                for (int i = 0; i < events.length(); i++) {

                    JSONObject event = new JSONObject(events.getJSONObject(i).toString());

                    String raspberry_id = event.getString("raspberryID");
                    String event_type = event.getString("eventType");
                    String event_time = event.getString("eventTime");

                    String note = "NONE";

                    if (!event.isNull("note"))
                        note = event.getString("note");

                    String name = "NONE";

                    if (!event.isNull("name")) {
                        name = event.getString("name");
                    }

                    Integer num = null;

                    if (event_type.equals("KNOCK"))
                        num = 1;
                    if (event_type.equals("OPEN"))
                        num = 2;
                    if (event_type.equals("CLOSE"))
                        num = 3;
                    if (event_type.equals("MAIL"))
                        num = 0;
                    if (event_type.equals("ID_SCAN"))
                        num = 4;

                    TimelineItem item = new TimelineItem(raspberry_id, num.toString(), event_type, event_time, note, name);
                    Log.d(LOG_TAG, event.toString() + " NT: " + note + " NM: " + name);
                    list.add(item);
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error in http connection" + e.toString());
            }

            return list;

        }

        public boolean updateDB(ArrayList<TimelineItem> items)
        {
            DBHelper helper = new DBHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues values = new ContentValues();
            for (TimelineItem item : items)
            {
                values.put(helper.RASPBERRY_ID, item.getRaspberryID());
                values.put(helper.EVENT_ID, item.getEventID());
                values.put(helper.EVENT_TYPE, item.getEventName());
                values.put(helper.TIME, item.getTime());
                values.put(helper.NOTE, item.getDescription());
                values.put(helper.NAME, item.getSenderName());

                db.insert(helper.EVENT_TABLE, null, values);
            }
            db.close();
            helper.close();

            Log.d(LOG_TAG, "Inserted " + items.size() + " items into DB");

            if (items.size() > 0)
                return true;
            else
                return false;

        }

    }

    /*
     * UserRegisterTask
     * AsyncTask to show progress bar while registering the user
     * @param firstname, lastname, raspberry pi
     */
    public static class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String mFirstName;
        private String mLastName;
        private String mUsername;
        private String mPassword;
        private String mEmail;
        private String uuid;

        public UserRegisterTask(Context context, String first_name, String last_name, String username, String password, String email) {
            mFirstName = first_name;
            mLastName = last_name;
            mUsername = username;
            mPassword = password;
            mEmail = email;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return postRequest(mFirstName, mLastName, mUsername, mPassword, mEmail);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Intent intent = new Intent(context, TimelineActivity.class);
                intent.putExtra("userid", uuid);
                Log.d(LOG_TAG, "Logged in user: " + uuid);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Could not Register!", Toast.LENGTH_LONG);
            }
        }

        @Override
        protected void onCancelled() {
            //TODO: go back to Register screen
        }

        public boolean postRequest(String first_name, String last_name, String username, String password, String email) {
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
                uuid = obj.getString("userid");
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getMessage());
            }

            return true;

        }
    }

   /*
    * UserLoginTask
    * AsyncTask to show progress bar while attempting log in
    * @param username, password
    */
    public static class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

       private Context context;
       private String mUsername;
       private String mPassword;
       private String uuid;
       private Boolean completed;

       public UserLoginTask(Context context, String username, String password) {
           mUsername = username;
           mPassword = password;
           this.context = context;
           completed = false;
       }

       @Override
       protected Boolean doInBackground(Void... params) {
           completed = getRequest(mUsername, mPassword);
           return completed;
       }

       @Override
       protected void onPostExecute(Boolean success)
       {
           if (success)
           {
               Intent intent = new Intent(context, TimelineActivity.class);
               intent.putExtra("userid", uuid);
               context.startActivity(intent);
           }
           else
           {
               Toast.makeText(context, "Invalid login details, Please try again!", Toast.LENGTH_LONG).show();
           }
       }

       @Override
       protected void onCancelled() {
           //TODO: go back to login screen
       }

       public boolean getRequest(String username, String password) {
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

               Log.d(LOG_TAG, response);

               if (response != null)
               {
                   JSONObject obj = new JSONObject(response);
                   Log.d(LOG_TAG, obj.getString("userid"));
                   uuid = obj.getString("userid");
                   if (uuid.equals("DNE"))
                       return false;
                   return true;
               }
               else
               {
                   return false;
               }
           } catch (Exception e) {
               Log.d(LOG_TAG, e.getMessage());
               return false;
           }

       }

    }

    /**
     * PI Register Task
     *
     * This tasks registers a raspberry pi with the service
     * */
    public static class PiRegisterTask extends AsyncTask<Void, Void, Boolean>
    {
        private String mRaspberry_pi;
        private String mRaspberry_pi_name;
        private String mUUID;
        private Context mContext;

        public PiRegisterTask(Context context, String raspberry_pi, String raspberry_pi_name, String uuid)
        {
            mRaspberry_pi = raspberry_pi;
            mRaspberry_pi_name = raspberry_pi_name;
            mUUID = uuid;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return postRequest(mRaspberry_pi);
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            //Do Nothing
        }

        @Override
        protected void onCancelled()
        {
            //TODO: go back to Register screen
        }

        public boolean postRequest(String raspberry_pi)
        {

            String json = "";

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://193.62.81.88:5000");

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("event", "ADDPI");
                jsonObject.accumulate("userid", mUUID);
                jsonObject.accumulate("raspberryid", mRaspberry_pi);
                jsonObject.accumulate("raspberryname", mRaspberry_pi_name);

                json = jsonObject.toString();

                Log.d(LOG_TAG, json);

                StringEntity se = new StringEntity(json);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);

                HttpResponse httpResponse = client.execute(post);

                String response = EntityUtils.toString(httpResponse.getEntity());

                Log.d(LOG_TAG, response);

                String pi_added;

                if (response != null)
                {
                    JSONObject obj = new JSONObject(response);
                    Log.d(LOG_TAG, obj.getString("added"));
                    pi_added = obj.getString("added");
                    if (pi_added.equals("false"))
                        return false;
                    return true;
                }
                else
                {
                    return false;
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getMessage());
                return false;
            }

        }

    }

    /**
     * Get All PI's
     *
     * This task gets all the PI's associated with the user
     * */
    public static class GetAllPI extends AsyncTask<Void, Void, ArrayList<PI>>
    {

        String mUUID;
        ArrayList<PI> pi_list;

        public GetAllPI(String uuid)
        {
            mUUID = uuid;
        }

        @Override
        protected ArrayList<PI> doInBackground(Void... params) {
            pi_list = getRequest(mUUID);
            return pi_list;
        }

        public ArrayList<PI> getRequest(String uuid)
        {

            ArrayList<PI> pi_list = new ArrayList<PI>();
            String json = "";

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet("http://193.62.81.88:5000");

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("event", "GETPI");
                jsonObject.accumulate("userid", mUUID);

                json = jsonObject.toString();

                Log.d(LOG_TAG, json);

                StringEntity se = new StringEntity(json);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                get.setEntity(se);

                HttpResponse httpResponse = client.execute(get);

                String response = EntityUtils.toString(httpResponse.getEntity());

                Log.d(LOG_TAG, response);

                if (response != null)
                {
                    JSONObject obj = new JSONObject(response);
                    JSONArray pi_s = new JSONArray(obj.getString("raspberryList"));

                    for (int i = 0; i < pi_s.length(); i++)
                    {
                        JSONObject pi = new JSONObject(pi_s.getJSONObject(i).toString());

                        PI pi_item = new PI(pi.getString("raspberryID"), pi.getString("raspberryName"));
                        pi_list.add(pi_item);
                    }

                    return pi_list;

                }
                else
                {
                    return null;
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getMessage());
            }

            return  null;
        }

    }
}
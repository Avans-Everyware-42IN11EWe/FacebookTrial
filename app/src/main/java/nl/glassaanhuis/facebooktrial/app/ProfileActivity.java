package nl.glassaanhuis.facebooktrial.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.NonCachingTokenCachingStrategy;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity {
    private String url;
    void draw(Session session) {
        final TextView name = (TextView) findViewById(R.id.userName);
        final ProfilePictureView pic = (ProfilePictureView) findViewById(R.id.photo);
        final String str = session.getAccessToken();

        Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                name.setText(user.getName());
                pic.setProfileId(user.getId());
                ConnectionTask task = new ConnectionTask();
                String[] params = new String[2];

                params[0] = "http://glas.mycel.nl/facebook?accesstoken=" + str;
                task.execute(params);


            }
        }).executeAsync();





    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            draw(session);

        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class ConnectionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String aString = "";
            try {
                URL aURL = new URL(urls[0]);

                final HttpURLConnection aHttpURLConnection = (HttpURLConnection) aURL.openConnection();

                InputStream aInputStream = aHttpURLConnection.getInputStream();
                BufferedInputStream aBufferedInputStream = new BufferedInputStream(
                        aInputStream);

                ByteArrayBuffer aByteArrayBuffer = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = aBufferedInputStream.read()) != -1) {
                    aByteArrayBuffer.append((byte) current);
                }

                aString = new String(aByteArrayBuffer.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                // HAHAA, HOEZO ERROR HANDLING??
            }
            return aString;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jObject = new JSONObject(result);
                JSONArray plaatjes;
                JSONArray probeer = jObject.getJSONArray("photos");
                plaatjes = jObject.getJSONArray("plaatjes");

                // doe iets met data
                url = probeer.getJSONObject(0).get("source").toString();

                GridView gridview = (GridView) findViewById(R.id.gridLayout);
                gridview.setAdapter(new ImageAdapter(plaatjes, ProfileActivity.this));
                RelativeLayout viewtje = (RelativeLayout) findViewById(R.id.background);
                String[] params = new String[2];

                params[0] = url;
                DownloadImageTask task =  new DownloadImageTask(viewtje);
                task.execute(params);




            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        RelativeLayout viewtje;

        public DownloadImageTask(RelativeLayout bmImage) {
            this.viewtje = bmImage;
        }


        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Drawable d = new BitmapDrawable(getResources(),result);
            viewtje.setBackground(d);
            viewtje.getBackground().setAlpha(55);
        }
    }

        public void likePage(View v) {


            //final String urlFb = "fb://page/glasvezelpaleiskwartier";
            final String urlFb = "fb://page/1450066045229608";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(urlFb));

            final PackageManager packageManager = getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(
                    intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() == 0) {
                //final String urlBrowser = "https://www.facebook.com/glasvezelpaleiskwartier";
                final String urlBrowser = "https://www.facebook.com/pages/1450066045229608";

                intent.setData(Uri.parse(urlBrowser));

            }

            startActivity(intent);

        }



    }

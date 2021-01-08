package com.nascenia.albarakahhajj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.ConnectException;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    EditText edtUserId, edtMobile, edtName;
    Button btnSearch;
    String userId, mobile, name;
    private SharedPref mSharedPref;
    private OkHttpClient client;
    ProgressDialog progressBar;
    private View iv_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        btnSearch = findViewById(R.id.btn_submit);
        edtUserId = findViewById(R.id.edt_user_id);
        edtMobile = findViewById(R.id.edt_mobile);
        edtName = findViewById(R.id.edt_name);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = edtUserId.getText().toString();
                mobile = edtMobile.getText().toString();
                name = edtName.getText().toString();
                if(userId.isEmpty() && mobile.isEmpty() && name.isEmpty()){
                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.req_atleast_one_info), Toast.LENGTH_LONG).show();
                }else{
                    if(Utils.isOnline(SearchActivity.this)) {
                        Utils.hideSoftKey(SearchActivity.this, edtMobile);
                        new RequestSearchUser().execute(userId, mobile, name);
                    }else{
                        Toast.makeText(SearchActivity.this, getString(R.string.no_internet_connection),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        iv_logout = findViewById(R.id.iv_logout);
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline(SearchActivity.this))
                    new Utils.Logout(SearchActivity.this, "id").execute();
//                mSharedPref.set_data("token", "");
                startActivity(new Intent(SearchActivity.this, LoginActivity.class));
                finish();
            }
        });
        mSharedPref = new SharedPref(SearchActivity.this);
        client = new OkHttpClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(data.getBooleanExtra("logout", false))
                startActivity(new Intent(SearchActivity.this, LoginActivity.class));
                finish();
        }
    }

    private class RequestSearchUser extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("applicant_no", strings[0] != null? strings[0]: "")
                    .addFormDataPart("mobile_number", strings[1] != null? strings[1]: "")
                    .addFormDataPart("name", strings[2] != null? strings[2]: "")
                    .build();

            String strParam = "applicant_no=" + (!strings[0].isEmpty()? strings[0]: "") + "&"
                    + "mobile_number=" + (!strings[1].isEmpty()? strings[1]: "") + "&"
                    + "applicant_name=" + (!strings[2].isEmpty()? strings[2]: "");

            Request request = new Request.Builder()
                    .url(Utils.BASE_URL + Utils.search_user + strParam)
                    .addHeader("Authorization", "Token token=" + mSharedPref.get_data("token"))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                System.out.println("Users list data: " + responseString);
                response.body().close();
                return responseString;
            } catch (ConnectException e) {
                e.printStackTrace();
                return null;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null; //getResources().getString(R.string.no_server_message);
            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            if (s == null) {
                Toast.makeText(SearchActivity.this, getString(R.string.no_server_message),
                        Toast.LENGTH_LONG).show();
                progressBar.dismiss();
            } else {
                try {
//                    progressBar.dismiss();
                    JSONArray jsonArray = new JSONArray(s);
                    if(jsonArray.length() > 0) {
                        if(jsonArray.length() > 1){
                            progressBar.dismiss();
                            Intent intent = new Intent(SearchActivity.this, SearchListActivity.class);
                            intent.putExtra("users_json", s);
                            startActivityForResult(intent, Utils.STATE_ON);
                        }else {
                            JSONObject jobj = (JSONObject) jsonArray.get(0);
                            if(Utils.isOnline(SearchActivity.this)) {
                                new RequestUserDetails().execute(jobj.get("id").toString());
                            }else{
                                Toast.makeText(SearchActivity.this, getString(R.string.no_internet_connection),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }else{
                        progressBar.dismiss();
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.no_subscriber_msg), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ex) {
                    progressBar.dismiss();
                    try{
                        JSONObject jobj = new JSONObject(s);
                        if (jobj.has("error")) {
                            progressBar.dismiss();
                            Toast.makeText(SearchActivity.this, (jobj.getInt("status") == 500)?getString(R.string.no_server_message) : jobj.getString("error"),
                                    Toast.LENGTH_LONG).show();
                        } else if(jobj.has("message")){
                            Toast.makeText(SearchActivity.this, jobj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }catch(JSONException ex1){
                        Toast.makeText(SearchActivity.this, getString(R.string.no_server_message),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    progressBar.dismiss();
                    Toast.makeText(SearchActivity.this, getString(R.string.no_server_message),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(SearchActivity.this);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getResources().getString(R.string.progress_dialog_message));
            progressBar.setCancelable(false);
            if(!progressBar.isShowing())
                progressBar.show();
        }
    }

    private class RequestUserDetails extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            Request request = new Request.Builder()
                    .url(Utils.BASE_URL + Utils.user_details + strings[0])
                    .addHeader("Authorization", "Token token=" + mSharedPref.get_data("token"))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                System.out.println("Users details: " + responseString);
                response.body().close();
                return responseString;
            } catch (ConnectException e) {
                e.printStackTrace();
                return null;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null; //getResources().getString(R.string.no_server_message);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
            if (s == null) {
                Toast.makeText(SearchActivity.this, getString(R.string.no_server_message),
                        Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    if (jsonObj.has("error")) {
                        progressBar.dismiss();
                        Toast.makeText(SearchActivity.this, (jsonObj.getInt("status") == 500)?getString(R.string.no_server_message) : jsonObj.getString("error"),
                                Toast.LENGTH_LONG).show();
                    } else if (jsonObj.has("id")) {
                        startActivityForResult(new Intent(SearchActivity.this,
                                        UserDetailsActivity.class).putExtra("user_json", s),
                                Utils.STATE_ON);
                    } else if (jsonObj.has("message")) {
                        Toast.makeText(SearchActivity.this, jsonObj.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SearchActivity.this,
                                getResources().getString(R.string.no_subscriber_msg),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException ex) {
                    try {
                        JSONObject jobj = new JSONObject(s);
                        if (jobj.has("message")) {
                            Toast.makeText(SearchActivity.this, jobj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException ex1) {
                        Toast.makeText(SearchActivity.this,
                                getString(R.string.no_server_message),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(SearchActivity.this, getString(R.string.no_server_message),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {
//            progressBar = new ProgressDialog(SearchActivity.this);
//            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressBar.setMessage(getResources().getString(R.string.progress_dialog_message));
//            progressBar.setCancelable(false);
//            progressBar.show();
        }
    }
}

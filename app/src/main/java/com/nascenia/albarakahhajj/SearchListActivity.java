package com.nascenia.albarakahhajj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.nascenia.albarakahhajj.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchListActivity extends AppCompatActivity {

    ListView list;
    private SharedPref mSharedPref;
    private View iv_logout;
    TextView tv_user_number;
    ProgressDialog progressBar;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        getSupportActionBar().hide();
        mSharedPref = new SharedPref(SearchListActivity.this);
        iv_logout = findViewById(R.id.iv_logout);
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPref.set_data("token", "");
                Intent intent = getIntent();
                intent.putExtra("logout", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        tv_user_number = findViewById(R.id.tv_user_number);

        final List<List> search_list = new ArrayList<>();
        ArrayList<String> item;

        String user_json = getIntent().getStringExtra("users_json");
        System.out.println(user_json);
        JSONArray jsonArray;
        User user;
        try {
            jsonArray = new JSONArray(user_json);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Gson gson = new Gson();
                    InputStream is = new ByteArrayInputStream(jsonArray.get(
                            i).toString().getBytes());
                    InputStreamReader isr = new InputStreamReader(is);
                    user = gson.fromJson(isr, User.class);
                    item = new ArrayList<>();
                    item.add(Utils.convertEnglishDigittoBangla(user.getApplicantNo()));
                    item.add(user.getApplicantName());
                    item.add(Utils.convertEnglishDigittoBangla(user.getMobile_number()));
                    item.add(user.getId() + "");
                    search_list.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tv_user_number.setText(getResources().getString(R.string.subscriber_list) + "("
                + Utils.convertEnglishDigittoBangla(search_list.size() + ")"));
        SearchListAdapter adapter = new SearchListAdapter(this, R.layout.list_search_item,
                search_list);
        list = findViewById(R.id.search_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Utils.isOnline(SearchListActivity.this)) {
                    ArrayList<String> user = (ArrayList<String>) search_list.get(position);
                    new RequestUserDetails().execute(user.get(3));
                }else{
                    Toast.makeText(SearchListActivity.this, getString(R.string.no_internet_connection),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        client = new OkHttpClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            new Utils.Logout(SearchListActivity.this, "id").execute();
//            if (data.getBooleanExtra("logout", false)) {
//                mSharedPref.set_data("token", "");
//            }
            Intent intent = getIntent();
            intent.putExtra("logout", true);
            setResult(RESULT_OK, intent);
            finish();
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
                Toast.makeText(SearchListActivity.this, getString(R.string.no_server_message),
                        Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    if (jsonObj.has("error")) {
                        progressBar.dismiss();
                        Toast.makeText(SearchListActivity.this, (jsonObj.getInt("status") == 500)?getString(R.string.no_server_message) : jsonObj.getString("error"),
                                Toast.LENGTH_LONG).show();
                    } else if (jsonObj.has("id")) {
                        startActivityForResult(new Intent(SearchListActivity.this,
                                        UserDetailsActivity.class).putExtra("user_json", s),
                                Utils.STATE_ON);
                    } else if (jsonObj.has("message")) {
                        Toast.makeText(SearchListActivity.this, jsonObj.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SearchListActivity.this,
                                getResources().getString(R.string.no_subscriber_msg),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException ex) {
                    try {
                        JSONObject jobj = new JSONObject(s);
                        if (jobj.has("message")) {
                            Toast.makeText(SearchListActivity.this, jobj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException ex1) {
                        Toast.makeText(SearchListActivity.this,
                                getString(R.string.no_server_message),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(SearchListActivity.this, getString(R.string.no_server_message),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(SearchListActivity.this);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getResources().getString(R.string.progress_dialog_message));
            progressBar.setCancelable(false);
            progressBar.show();
        }
    }
}

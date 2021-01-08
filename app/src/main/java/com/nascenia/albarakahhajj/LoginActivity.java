package com.nascenia.albarakahhajj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    Button btnSubmit;
    LinearLayout ln_otp, ln_pin;
    OkHttpClient client = null;
    ProgressDialog progressBar;
    EditText edtPhone, edtOTP, edtPin;
    String phone, otp, pin;
    SharedPref mSharedPref;
    TextView tv_otp_resend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        ln_otp = findViewById(R.id.ln_otp);
        ln_pin = findViewById(R.id.ln_pin);
        edtPhone = findViewById(R.id.edt_mobile);
        edtOTP = findViewById(R.id.edt_otp);
        tv_otp_resend = findViewById(R.id.tv_otp_resend);
        edtPin = findViewById(R.id.edt_pin);
        btnSubmit = findViewById(R.id.btn_submit);
//        new RequestUserDetails().execute("3");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline(LoginActivity.this)) {
                    if (ln_otp.getVisibility() == View.GONE
                            && ln_pin.getVisibility() == View.GONE) {
                        requestOTP();
                    } else if (ln_pin.getVisibility() == View.VISIBLE) {
                        requestSignIn();
                    } else if (ln_otp.getVisibility() == View.VISIBLE) {
                        requestOTPVerification();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            getResources().getString(R.string.no_internet_connection),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        tv_otp_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOTP();
            }
        });
//        client = new OkHttpClient();
        client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).build();
        mSharedPref = new SharedPref(LoginActivity.this);
        if (mSharedPref.get_data("mobile").isEmpty()) {
            edtPhone.setEnabled(true);
        } else {
            edtPhone.setText(mSharedPref.get_data("mobile"));
            edtPhone.setEnabled(false);
//            edtPhone.setTextColor(Integer.parseInt("#a3a3a3"));
            if (mSharedPref.get_data("isOTPVerified").isEmpty()) {
                ln_otp.setVisibility(View.VISIBLE);
                edtOTP.requestFocus();
            } else {
                ln_otp.setVisibility(View.GONE);
                tv_otp_resend.setVisibility(View.GONE);
                ln_pin.setVisibility(View.VISIBLE);
                edtPin.requestFocus();
                btnSubmit.setText(getResources().getString(R.string.sign_in));
            }
        }
//        Toast.makeText(this, getResources().getDisplayMetrics().density + "", Toast.LENGTH_LONG).show();
    }

    private void requestOTP() {
        phone = edtPhone.getText().toString();
        if (phone == null || phone.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.mobile_number_ask),
                    Toast.LENGTH_LONG).show();
            edtPhone.requestFocus();
        } else {
            if(Utils.isOnline(LoginActivity.this)) {
                Utils.hideSoftKey(LoginActivity.this, edtPhone);
                new RequestOTP().execute(phone);
            }else{
                Toast.makeText(LoginActivity.this, getString(R.string.no_internet_connection),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestOTPVerification() {
        otp = edtOTP.getText().toString();
        phone = edtPhone.getText().toString();
        if (otp == null || otp.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.OTP_ask),
                    Toast.LENGTH_LONG).show();
            edtOTP.requestFocus();
        } else {
            if(Utils.isOnline(LoginActivity.this)) {
                Utils.hideSoftKey(LoginActivity.this, edtOTP);
                new RequestOTPVerification().execute(phone, otp);
            }else{
                Toast.makeText(LoginActivity.this, getString(R.string.no_internet_connection),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestSignIn() {
        pin = edtPin.getText().toString();
        phone = edtPhone.getText().toString();
        if (pin == null || pin.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.PIN_ask),
                    Toast.LENGTH_LONG).show();
            edtPin.requestFocus();
        } else {
            if(Utils.isOnline(LoginActivity.this)) {
                Utils.hideSoftKey(LoginActivity.this, edtPin);
                new RequestSignIn().execute(phone, pin);
            }else{
                Toast.makeText(LoginActivity.this, getString(R.string.no_internet_connection),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class RequestOTP extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            Request request = new Request.Builder()
                    .url(Utils.BASE_URL + Utils.otp_req + "mobile=" + strings[0])
                    .get()
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                System.out.println("Req OTP data: " + responseString);
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
            if (s == null) {
                Toast.makeText(LoginActivity.this, getString(R.string.no_server_message),
                        Toast.LENGTH_LONG).show();
                progressBar.dismiss();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Log.e("response: ", s);
                    if (jsonObject.has("error")) {
                        progressBar.dismiss();
                        Toast.makeText(LoginActivity.this, (jsonObject.getInt("status") == 500)?getString(R.string.no_server_message) : jsonObject.getString("error"),
                                Toast.LENGTH_LONG).show();
                    } else if (jsonObject.has("message")) {
                        progressBar.dismiss();
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"),
                                Toast.LENGTH_LONG).show();
                        if (jsonObject.has("mobile")) {
//                            Toast.makeText(LoginActivity.this, jsonObject.getString("otp"),
//                                    Toast.LENGTH_LONG).show();
                            progressBar.dismiss();
                            mSharedPref.set_data("mobile", phone);
                            edtPhone.setEnabled(false);
                            ln_otp.setVisibility(View.VISIBLE);
                            edtOTP.requestFocus();
                        }
                    } else if (jsonObject.has("success")) {
                        progressBar.dismiss();
                        mSharedPref.set_data("mobile", phone);
                        edtPhone.setEnabled(false);
                        ln_otp.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    progressBar.dismiss();
                    Toast.makeText(LoginActivity.this, getString(R.string.no_server_message),
                            Toast.LENGTH_LONG).show();
                }

            }
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(LoginActivity.this);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getResources().getString(R.string.progress_dialog_message));
            progressBar.setCancelable(false);
            progressBar.show();
        }
    }

    private class RequestOTPVerification extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mobile", strings[0])
                    .addFormDataPart("otp", strings[1])
                    .build();

            Request request = new Request.Builder()
                    .url(Utils.BASE_URL + Utils.verify_otp)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                System.out.println("OTP verification: " + responseString);
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
            if (s == null) {
                Toast.makeText(LoginActivity.this, getString(R.string.no_server_message),
                        Toast.LENGTH_LONG).show();
                progressBar.dismiss();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Log.e("response: ", s);
                    if (jsonObject.has("error")) {
                        progressBar.dismiss();
                        Toast.makeText(LoginActivity.this, (jsonObject.getInt("status") == 500)?getString(R.string.no_server_message) : jsonObject.getString("error"),
                                Toast.LENGTH_LONG).show();
                    } else if (jsonObject.has("message")) {
                        progressBar.dismiss();
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"),
                                Toast.LENGTH_LONG).show();
                        if (jsonObject.getString("message").equalsIgnoreCase(
                                Utils.otp_success_msg)) {
                            mSharedPref.set_data("isOTPVerified", "true");
                            tv_otp_resend.setVisibility(View.GONE);
                            ln_pin.setVisibility(View.VISIBLE);
                            edtPin.requestFocus();
                            edtPhone.setEnabled(false);
                        }
                    } else {
                        progressBar.dismiss();
                    }
                } catch (Exception ex) {
                    progressBar.dismiss();
                    Toast.makeText(LoginActivity.this, getString(R.string.no_server_message),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(LoginActivity.this);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getResources().getString(R.string.progress_dialog_message));
            progressBar.setCancelable(false);
            progressBar.show();
        }
    }

    private class RequestSignIn extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mobile", strings[0])
                    .addFormDataPart("pin_code", strings[1])
                    .build();

            Request request = new Request.Builder()
                    .url(Utils.BASE_URL + Utils.sign_in)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                System.out.println("Sign in data: " + responseString);
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
            if (s == null) {
                Toast.makeText(LoginActivity.this, getString(R.string.no_server_message),
                        Toast.LENGTH_LONG).show();
                progressBar.dismiss();
            } else {
                try {
                    progressBar.dismiss();
                    JSONObject jsonObject = new JSONObject(s);
                    Log.e("response: ", s);
                    if (jsonObject.has("error")) {
                        progressBar.dismiss();
                        Toast.makeText(LoginActivity.this, (jsonObject.getInt("status") == 500)?getString(R.string.no_server_message) : jsonObject.getString("error"),
                                Toast.LENGTH_LONG).show();
                    } else if (jsonObject.has("message")) {
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } else if (jsonObject.has("auth_token")) {
                        mSharedPref.set_data("token", jsonObject.getString("auth_token"));
                        mSharedPref.set_data("id", jsonObject.getString("id"));
                        startActivity(new Intent(LoginActivity.this, SearchActivity.class));
                        finish();
                    }
                } catch (Exception ex) {
                    progressBar.dismiss();
                    Toast.makeText(LoginActivity.this, getString(R.string.no_server_message),
                            Toast.LENGTH_LONG).show();
                }

            }
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(LoginActivity.this);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getResources().getString(R.string.progress_dialog_message));
            progressBar.setCancelable(false);
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
            if (s == null) {
                progressBar.dismiss();
                Toast.makeText(LoginActivity.this, getString(R.string.no_server_message),
                        Toast.LENGTH_LONG).show();
            } else {
                try {
                    progressBar.dismiss();
                    JSONObject jobj = new JSONObject(s);
                    if (s != null) {
                        startActivity(
                                new Intent(LoginActivity.this, UserDetailsActivity.class).putExtra(
                                        "user_json", s));
                    } else {
                        Toast.makeText(LoginActivity.this,
                                getResources().getString(R.string.no_subscriber_msg),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ex) {
                    try {
                        JSONObject jobj = new JSONObject(s);
                        if (jobj.has("message")) {
                            Toast.makeText(LoginActivity.this, jobj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException ex1) {
                        Toast.makeText(LoginActivity.this, getString(R.string.no_server_message),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(LoginActivity.this, getString(R.string.no_server_message),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(LoginActivity.this);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getResources().getString(R.string.progress_dialog_message));
            progressBar.setCancelable(false);
            progressBar.show();
        }
    }
}
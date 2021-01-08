package com.nascenia.albarakahhajj;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.nascenia.albarakahhajj.model.UserDetails.UserDetails;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserDetailsActivity extends AppCompatActivity {

    RelativeLayout mRelativeLayoutTitle;
    LinearLayout ln_list, ln_chque;
    RadioButton rdCash, rdCheque;
    TextView tv_id_data, tv_name, tv_user_mobile_data, tv_installment_data,
            tv_paid_data, tv_last_paid_data, tv_due_data, tv_date;
    EditText edt_amount, edt_cheque, edt_pin;
    Spinner sp_bank_name;
    Button btnSubmit;
    private View iv_logout;
    private SharedPref mSharedPref;
    private OkHttpClient client;
    ProgressDialog progressBar;
    Calendar calendar;
    private int year, month, day;
    ImageView iv_drop_arrow, iv_calendar;
    boolean bank_list_requested = false;
    List<String> list = new ArrayList<String>();
    UserDetails ud = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        getSupportActionBar().hide();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mRelativeLayoutTitle = findViewById(R.id.rl_list_title);
        ln_list = findViewById(R.id.ln_list);
        tv_id_data = findViewById(R.id.tv_id_data);
        tv_name = findViewById(R.id.tv_name_data);
        tv_user_mobile_data = findViewById(R.id.tv_user_mobile_data);
        tv_installment_data = findViewById(R.id.tv_installment_data);
        tv_due_data = findViewById(R.id.tv_due_data);
        tv_last_paid_data = findViewById(R.id.tv_last_paid_data);
        tv_due_data = findViewById(R.id.tv_due_data);
        tv_paid_data = findViewById(R.id.tv_paid_data);
        rdCash = findViewById(R.id.radioCash);
        rdCheque = findViewById(R.id.radioCheque);
        ln_chque = findViewById(R.id.ln_cheque);
        edt_amount = findViewById(R.id.edt_amount);
        edt_cheque = findViewById(R.id.edt_cheque);
        sp_bank_name = findViewById(R.id.sp_bank_name);
        iv_drop_arrow = findViewById(R.id.iv_drop_arrow);
        iv_calendar = findViewById(R.id.iv_calendar);
        iv_drop_arrow.setColorFilter(Color.parseColor("#01483B"));
        iv_calendar.setColorFilter(Color.parseColor("#01483B"));
        tv_date = findViewById(R.id.tv_date);
        edt_pin = findViewById(R.id.edt_pin);
        btnSubmit = findViewById(R.id.btn_submit);
        mSharedPref = new SharedPref(UserDetailsActivity.this);
        client = new OkHttpClient();
        mRelativeLayoutTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ln_list.getVisibility() == View.VISIBLE) {
                    ln_list.setVisibility(View.GONE);
                } else {
                    ln_list.setVisibility(View.VISIBLE);
                }
            }
        });

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(tv_date);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        rdCash.setChecked(true);

        rdCheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ln_chque.setVisibility(View.VISIBLE);
            }
        });

        rdCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ln_chque.setVisibility(View.GONE);
            }
        });

        iv_logout = findViewById(R.id.iv_logout);
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Utils.Logout(UserDetailsActivity.this, "id").execute();
//                mSharedPref.set_data("token", "");
                Intent intent = getIntent();
                intent.putExtra("logout", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        initializeDatePicker();

        String user_json = getIntent().getStringExtra("user_json");
        Gson gson = new Gson();
        InputStream is = new ByteArrayInputStream(user_json.getBytes());
        InputStreamReader isr = new InputStreamReader(is);
        ud = gson.fromJson(isr, UserDetails.class);
        tv_id_data.setText(Utils.convertEnglishDigittoBangla((ud.getApplicantNo())));
        tv_name.setText(ud.getApplicantName());
        tv_user_mobile_data.setText(Utils.convertEnglishDigittoBangla((ud.getMobileNumber())));
        tv_installment_data.setText(Utils.convertEnglishDigittoBangla(ud.getInstallment().getAmount() + "/" + ud.getInstallment().getType()));
        tv_last_paid_data.setText(Utils.convertEnglishDigittoBangla(ud.getLastPayment().getAmount() + "/" + ud.getLastPayment().getDate()));
        tv_due_data.setText(Utils.convertEnglishDigittoBangla(ud.getDue().getAmount() + "/" + ud.getDue().getDate()));
        tv_paid_data.setText(Utils.convertEnglishDigittoBangla(ud.getTotalDiposit().getAmount() + "/" + ud.getTotalDiposit().getDate()));
        System.out.println(user_json);
        sp_bank_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!bank_list_requested) {
//                    if(Utils.isOnline(UserDetailsActivity.this)) {
//                        Utils.hideSoftKey(UserDetailsActivity.this, edt_amount);
                        new RequestBanksList().execute();
                        bank_list_requested = true;
                    }else if(list.size() > 0){
//                        Toast.makeText(UserDetailsActivity.this, getString(R.string.no_internet_connection),
//                                Toast.LENGTH_LONG).show();
                    }
//
//                }
                return false;
            }
        });
    }

    private void initializeDatePicker() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog dpd = new DatePickerDialog(this,
                    myDateListener, year, month, day);
//            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dpd;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                        int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        tv_date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    private void validateData() {
        if(edt_amount.getText().toString().isEmpty()){
            Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.amount_ask), Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject jsonParam = new JSONObject();
        JSONObject jsonPayment = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        if(rdCash.isChecked()){
            if(edt_pin.getText().toString().isEmpty()){
                Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.PIN_ask), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                jsonObject.put("subscriber_id", ud.getId());
                jsonObject.put("agent_id", mSharedPref.get_data("id"));
                jsonObject.put("pay_type", "cash");
                jsonObject.put("amount", edt_amount.getText().toString());
                jsonObject.put("cheque_number", "");
                jsonObject.put("bank_name", "");
                jsonObject.put("cheque_date", "");
//                jsonObject.put("pin_code", edt_pin.getText().toString());
                jsonPayment.put("payment", jsonObject);
                jsonPayment.put("pin_code", edt_pin.getText().toString());
                jsonParam = new JSONObject(jsonPayment.toString());
//                jsonParam.put("pin_code", "123456");
                System.out.println(jsonParam.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(Utils.isOnline(UserDetailsActivity.this)) {
                Utils.hideSoftKey(UserDetailsActivity.this, edt_amount);
//                showPinInputAlert(jsonParam);
                new RequestDeposit().execute(jsonParam.toString());
            }else{
                Toast.makeText(UserDetailsActivity.this, getString(R.string.no_internet_connection),
                        Toast.LENGTH_LONG).show();
            }
        }else{
            if(edt_cheque.getText().toString().isEmpty()){
                Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.cheque_no_ask), Toast.LENGTH_LONG).show();
                return;
            }else if(list.size() == 0){
                Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.bank_ask), Toast.LENGTH_LONG).show();
                return;
            } else if(tv_date.getText().toString().isEmpty()){
                Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.date_ask), Toast.LENGTH_LONG).show();
                return;
            } else if(edt_pin.getText().toString().isEmpty()){
                Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.PIN_ask), Toast.LENGTH_LONG).show();
                return;
            } else{
                if(Utils.isOnline(UserDetailsActivity.this)) {
                    try {
                        jsonObject.put("subscriber_id", ud.getId());
                        jsonObject.put("agent_id", mSharedPref.get_data("id"));
                        jsonObject.put("pay_type", "cheque");
                        jsonObject.put("amount", edt_amount.getText().toString());
                        jsonObject.put("cheque_number", edt_cheque.getText().toString());
                        jsonObject.put("bank_name", sp_bank_name.getSelectedItem());
                        jsonObject.put("cheque_date", tv_date.getText().toString());

                        jsonPayment.put("payment", jsonObject);
                        jsonPayment.put("pin_code", edt_pin.getText().toString());
                        jsonParam = new JSONObject(jsonPayment.toString());
//                        jsonParam.put("pin_code", "123456");
                        System.out.println(jsonParam.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utils.hideSoftKey(UserDetailsActivity.this, edt_amount);
//                    showPinInputAlert(jsonParam);
                    new RequestDeposit().execute(jsonParam.toString());
                }else{
                    Toast.makeText(UserDetailsActivity.this, getString(R.string.no_internet_connection),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void showPinInputAlert(final JSONObject jsonParam){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.pin));
        final EditText edtPin = new EditText(this);
        edtPin.setHint(getResources().getString(R.string.pin));
        edtPin.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(edtPin);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    if(edtPin.getText().toString().isEmpty())
                    {
                        Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.PIN_ask),
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    jsonParam.put("pin_code", edtPin.getText().toString());
                    new RequestDeposit().execute(jsonParam.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    private class RequestDeposit extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, strings[0]);
            Request request = new Request.Builder()
                    .url(Utils.BASE_URL + Utils.update_amount)
                    .addHeader("Authorization", "Token token=" + mSharedPref.get_data("token"))
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                System.out.println("Deposit response: " + responseString);
                response.body().close();
                return responseString;
            } catch (ConnectException e) {
                e.printStackTrace();
                return  null;
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
            progressBar.dismiss();
            if (s == null) {
                Toast.makeText(UserDetailsActivity.this, getString(R.string.no_server_message),
                        Toast.LENGTH_LONG).show();

            } else {
                try{
                    JSONObject jobj = new JSONObject(s);
                    if (jobj.has("error")) {
                        progressBar.dismiss();
                        Toast.makeText(UserDetailsActivity.this, (jobj.getInt("status") == 500)?getString(R.string.no_server_message) : jobj.getString("error"),
                                Toast.LENGTH_LONG).show();
                    } else if(jobj.has("message")){
                        Toast.makeText(UserDetailsActivity.this, jobj.getString("message"), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }catch(JSONException ex1){
                    Toast.makeText(UserDetailsActivity.this, getString(R.string.no_server_message),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(UserDetailsActivity.this);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getResources().getString(R.string.progress_dialog_message));
            progressBar.setCancelable(false);
            if(!progressBar.isShowing())
                progressBar.show();
        }
    }

    private class RequestBanksList extends AsyncTask<String, String, String> {
        @Override

        protected String doInBackground(String... strings) {
            Request request = new Request.Builder()
                    .url(Utils.BASE_URL + Utils.banks_list)
                    .addHeader("Authorization", "Token token=" + mSharedPref.get_data("token"))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                System.out.println("Banks list data: " + responseString);
                response.body().close();
                return responseString;
            } catch (ConnectException e) {
                e.printStackTrace();
                return "";
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
            progressBar.dismiss();
            if (s == null) {
                bank_list_requested = false;
                Toast.makeText(UserDetailsActivity.this, getString(R.string.no_server_message),
                        Toast.LENGTH_LONG).show();
            }else if(s.isEmpty()) {
                Toast.makeText(UserDetailsActivity.this, getString(R.string.no_internet_connection),
                        Toast.LENGTH_LONG).show();
                bank_list_requested = false;
            }else{
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    if(jsonArray.length() > 0) {
                        for(int i = 0; i < jsonArray.length(); i++) {
                            list.add(((JSONObject)(jsonArray.get(i))).getString("name"));
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(UserDetailsActivity.this,
                                android.R.layout.simple_spinner_item, list);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_bank_name.setAdapter(dataAdapter);
                    }else{
                        bank_list_requested = false;
                        Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.no_bank_msg), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ex) {
                    try{
                        bank_list_requested = false;
                        JSONObject jobj = new JSONObject(s);
                        if (jobj.has("error")) {
                            progressBar.dismiss();
                            Toast.makeText(UserDetailsActivity.this, (jobj.getInt("status") == 500)?getString(R.string.no_server_message) : jobj.getString("error"),
                                    Toast.LENGTH_LONG).show();
                        } else if(jobj.has("message")){
                            Toast.makeText(UserDetailsActivity.this, jobj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }catch(JSONException ex1){
                        Toast.makeText(UserDetailsActivity.this, getString(R.string.no_server_message),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    bank_list_requested = false;
                    Toast.makeText(UserDetailsActivity.this, getString(R.string.no_server_message),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(UserDetailsActivity.this);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getResources().getString(R.string.progress_dialog_message));
            progressBar.setCancelable(false);
            if(!progressBar.isShowing())
                progressBar.show();
        }
    }
}

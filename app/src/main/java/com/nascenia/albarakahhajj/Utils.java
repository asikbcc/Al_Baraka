package com.nascenia.albarakahhajj;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {

//    public static String BASE_URL = "http://18.222.241.36";
//    public static String BASE_URL = "http://18.222.241.36:8080";
    public static String BASE_URL = "http://192.168.1.56:5000";
//    public static String BASE_URL = "http://192.168.1.69:5000";
    public static String otp_req = "/api/v1/agents/send_otp?";
    public static String verify_otp = "/api/v1/agents/verify_otp"; //?mobile_number=01711070219&otp=24567";
    public static String sign_in = "/api/v1/agents/login"; //?mobile_number=01711070219";
    public static String search_user = "/api/v1/subscribers?"; //?subscriber_id=36&subscriber_name='lamia'";
    public static String user_details = "/api/v1/subscribers/"; //[id]
    public static String banks_list = "/api/v1/banks"; //[id]
    public static String update_amount = "/api/v1/payments"; //?subscriber_id=36&amount=2000'";
    public static String logout = "/api/v1/agents/";//7/logout";


    public static String otp_success_msg = "ওটিপি সফলভাবে যাচাই করা হয়েছে।"; //?subscriber_id=36&amount=2000'";

    public static final int STATE_ON = 1001;
    public static final int STATE_LOGOUT = 1002;


    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String convertEnglishDigittoBangla(String itemvalue) {

        if(itemvalue == null || itemvalue.isEmpty())
            return "";
        String[] strItemValue = (itemvalue).split("");
        String number = "";
        for(int i = 0; i<strItemValue.length; i++){
            if(strItemValue[i].length()>0)
                number+=getBanglaDigit(strItemValue[i]);
        }
        return number.length()>0?number:"0";
    }

    public static String getBanglaDigit(String digit) {
        switch (digit) {
            case "0":
                return "০";
            case "1":
                return "১";
            case "2":
                return "২";
            case "3":
                return "৩";
            case "4":
                return "৪";
            case "5":
                return "৫";
            case "6":
                return "৬";
            case "7":
                return "৭";
            case "8":
                return "৮";
            case "9":
                return "৯";
            default:
                return digit;
        }
    }

    public static void hideSoftKey(Context context, View view){
        InputMethodManager imm = (InputMethodManager)
        context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    public static class Logout extends AsyncTask<String, String, String> {
        private final SharedPref mSharedPref;
        private Context mContext;
        private OkHttpClient client;
        String agentId;

        public Logout(Context context, String agentId){
            mContext = context;
            mSharedPref = new SharedPref(mContext);
            this.agentId = agentId;
            client = new OkHttpClient();
        }

        @Override
        protected String doInBackground(String... strings) {
            Request request = new Request.Builder()
                    .url(Utils.BASE_URL + Utils.logout + agentId + "/" + "logout")
                    .addHeader("Authorization", "Token token=" + mSharedPref.get_data("token"))
                    .build();

            mSharedPref.set_data("token", "");

            try {
                client.newCall(request).execute();
                return "";
            } catch (ConnectException e) {
                e.printStackTrace();
                return null;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {

        }
    }
}

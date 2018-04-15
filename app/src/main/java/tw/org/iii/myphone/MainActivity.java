package tw.org.iii.myphone;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private TelephonyManager tmgr;
    private AccountManager amgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tmgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        amgr = (AccountManager)getSystemService(ACCOUNT_SERVICE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_PHONE_NUMBERS,
                            Manifest.permission.GET_ACCOUNTS,
                    },
                    0);

        }else {


            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){
        String lineNum = tmgr.getLine1Number();
        //Log.v("brad", lineNum);

        String imei = tmgr.getDeviceId();
        //Log.v("brad", imei);    // 姬瑪
        String imei2 = tmgr.getImei();
        //Log.v("brad", imei2);

        String imsi = tmgr.getSubscriberId();
        //Log.v("brad", imsi);    // sim

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                    null, false, null, null, null, null);
            startActivityForResult(intent, 0);
        }else {
           Account[] accounts = amgr.getAccounts();
            Log.v("brad", "count: " + accounts.length);
            for (Account account : accounts) {
                Log.v("brad", account.name + ":" + account.type);
            }
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        tmgr.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class MyPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            if (state == TelephonyManager.CALL_STATE_IDLE){
                Log.v("brad", "normal");
            }else if (state == TelephonyManager.CALL_STATE_RINGING){
                Log.v("brad", "ring:" + incomingNumber);
            }else if (state == TelephonyManager.CALL_STATE_OFFHOOK){
                Log.v("brad", "offhook");
            }


        }
    }


}

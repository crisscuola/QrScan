package com.roadway.capslabs.roadway_chat.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.zxing.Result;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.drawer.DrawerFactory;
import com.roadway.capslabs.roadway_chat.network.EventRequestHandler;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by konstantin on 14.10.16
 */
public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private Toolbar toolbar;
    private final static DrawerFactory drawerFactory = new DrawerFactory();
    private Activity context = this;
    private AlertDialog.Builder ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        initToolbar(getString(R.string.qr_activity_title));
        drawerFactory.getDrawerBuilder(this, toolbar).build();
    }

    public void QrScanner(View view) {

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        mScannerView.stopCamera();
//    }


    private void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_qr_scan);
        toolbar.setTitle(title);
    }

    @Override
    public void handleResult(Result rawResult) {

        Log.e("handler", rawResult.getText());
        Log.e("handler", rawResult.getBarcodeFormat().toString());
        String link = rawResult.getText();
        Log.d("check_url", link);
        String[] path = link.split("/");
        Log.d("check_url", path[4]);
        new CheckLink().execute(path[4]);

        mScannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        moveTaskToBack(true);
//
//        super.onDestroy();
//
//        System.runFinalizersOnExit(true);
//        System.exit(0);
    }

    public void alertShow (String mes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QrScannerActivity.this);
        builder.setTitle("Важное сообщение!")
                .setMessage(mes)
                .setIcon(R.drawable.logo2)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(context, QrScannerActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private final class CheckLink extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String link = String.valueOf(params[0]);
            return new EventRequestHandler().getCheck(context, link);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);

            alertShow("Wrong !!!");
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response.contains("[37]")) {
                alertShow("Код уже зарегестрирован");
            }

            if (response.contains("[38]")) {
                alertShow("Вы не создатель акции");
            }

            if (response.contains("object")) {
                alertShow("Код успешно зарегестрирован");
            }
        }
    }



}

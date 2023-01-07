package com.example.qrcodescanner_21c2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //view Objects
    private Button buttonScanning;
    private TextView textViewName, textViewClass, textViewId;
    //qr code scanner
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // View Object
        buttonScanning = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewNama);
        textViewClass = (TextView) findViewById(R.id.textViewKelas);
        textViewId = (TextView) findViewById(R.id.textViewNIM);

        //insialisasi scan object
        qrScan = new IntentIntegrator(this);

        //implementasi onclick listener
        buttonScanning.setOnClickListener(this);
    }

    //untuk hasil scanning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //jika qrcode tidak ada sama sekali
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil SCANNING Tidak Ada", Toast.LENGTH_LONG).show();
            }if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            }if(result.getContents().contains("tel:")) {
                //Mendapat data kode telpon
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(result.getContents()));
                startActivity(intent);
            }

            String alamat = result.getContents();
            String at = "@gmail";

            if(alamat.contains(at)) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {alamat.replace("http://", "")};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Email");
                intent.putExtra(Intent.EXTRA_TEXT, "Type Here");
                intent.putExtra(Intent.EXTRA_CC, "");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
            }
            String uriMaps = result.getContents();
            String maps = "http://maps.google.com/maps?q=loc:" + uriMaps;
            String testDoubleData1 = ",";
            String testDoubleData2 = ",";

            boolean b = uriMaps.contains(testDoubleData1) && uriMaps.contains(testDoubleData2);
            if(b){
                Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(maps));
                mapsIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapsIntent);
            }else{
                //jika qrcode ada/ditemukan datanya
                try {
                    //Konversi datanya ke json
                    JSONObject obj = new JSONObject(result.getContents());
                    //di set nilai datanya ke textview
                    textViewName.setText(obj.getString("nama"));
                    textViewClass.setText(obj.getString("kelas"));
                    textViewId.setText(obj.getString("nim"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        qrScan.initiateScan();
    }
}
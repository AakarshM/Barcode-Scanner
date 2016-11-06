package aakarsh.barcodefinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.*;

public class MainActivity extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(buttonListener);
    }

    public View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View view) {

            IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
            scanIntegrator.initiateScan();
        }

    };


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanResult != null){

            String scanContent = scanResult.getContents();
            Intent goToAnalyzer = new Intent(this.getApplicationContext(), AnalyzeActivity.class);
            goToAnalyzer.putExtra("Barcode", scanContent);
            startActivity(goToAnalyzer);
            Toast.makeText(getApplication(), scanContent.toString(), Toast.LENGTH_SHORT).show();

        } else{

            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }
}

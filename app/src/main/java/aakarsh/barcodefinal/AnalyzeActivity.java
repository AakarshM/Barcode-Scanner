package aakarsh.barcodefinal;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class AnalyzeActivity extends AppCompatActivity {

    String barcode;
    String dataReceived;
    TextView kcal, protein, fat, carbs, product, serving, brand, errorView, health;
    ProgressBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        kcal = (TextView) findViewById(R.id.kcal);
        protein = (TextView) findViewById(R.id.protein);
        fat = (TextView) findViewById(R.id.fat);
        carbs = (TextView) findViewById(R.id.carbs);
        product = (TextView) findViewById(R.id.product);
        serving = (TextView)findViewById(R.id.serving);
        brand = (TextView) findViewById(R.id.brand);
        health = (TextView)findViewById(R.id.health);
        bar = (ProgressBar)findViewById(R.id.bar);
        errorView = (TextView)findViewById(R.id.errorView);
        errorView.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.INVISIBLE);
        Bundle extras = getIntent().getExtras();
        barcode = extras.getString("Barcode");
      //  new getDataJSON().execute();
        VolleyMethod();
    }


    public void VolleyMethod(){
        bar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.nutritionix.com/v1_1/item?upc=" + barcode +"&appId=7cc6160a&appKey=3ca3a32981aeedf4eae952fbd6895acc";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dataReceived = response;
                        setInfo(response);
                        bar.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                    //If item doesnt exist in database.
                    Toast.makeText(getApplicationContext(),  "The item does not exist in the database", Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.INVISIBLE);
                }
                    else{
                    //not bad request
                    Toast.makeText(getApplicationContext(),  "There was an error", Toast.LENGTH_SHORT).show();
                    errorView.setVisibility(View.VISIBLE);
                    bar.setVisibility(View.INVISIBLE);
                }

            }
        });


// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //AsyncTask used to POST through REST to the Nutritionix API.
    public class getDataJSON extends AsyncTask<String, Void, String> {

        public void onPreExecute(){
            bar.setVisibility(View.VISIBLE);

        }
        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("https://api.nutritionix.com/v1_1/item?upc=" + barcode +"&appId=7cc6160a&appKey=3ca3a32981aeedf4eae952fbd6895acc");
            try{
                HttpResponse responseGiven = client.execute(get);
                StatusLine statusLine = responseGiven.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if(statusCode == 200){
                    HttpEntity ent = responseGiven.getEntity();
                    dataReceived = EntityUtils.toString(ent);
                }
                else if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Item not found on database", Toast.LENGTH_SHORT).show();
                    showError();
                }
            } catch(Exception e){
                Log.i("Exception was:", e.toString());
            }
            return null;
        }
        //PostExecute used to call setter method.
        public void onPostExecute( String s){
            super.onPostExecute(s);
            bar.setVisibility(View.INVISIBLE);
           setInfo(dataReceived);
        }

    }

    public void showError(){

        errorView.setVisibility(View.VISIBLE);
    }

    public void setInfo(String dataReceived) { //Parse JSON and attach to textviews.
        try {
            JSONObject obj = new JSONObject(dataReceived);
            String productName = obj.getString("item_name");
            String calories = obj.getString("nf_calories");
            String proteinNum = obj.getString("nf_protein");
            String fatNum = obj.getString("nf_total_fat");
            String carbNum = obj.getString("nf_total_carbohydrate");
            String servingSize = obj.getString("nf_serving_weight_grams");
            String company = obj.getString("brand_name");
           // System.out.println(productName);
            if(Double.parseDouble(carbNum)/Double.parseDouble(proteinNum)/Double.parseDouble(fatNum) >= 1.5){
                health.setText("Healthy");

            } else{

                health.setText("Unhealthy");
            }
            serving.setText(servingSize);
            carbs.setText(carbNum);
            fat.setText(fatNum);
            protein.setText(proteinNum);
            kcal.setText(calories);
            product.setText(productName);
            brand.setText(company);

        } catch (Exception e) {


        }

    }
}

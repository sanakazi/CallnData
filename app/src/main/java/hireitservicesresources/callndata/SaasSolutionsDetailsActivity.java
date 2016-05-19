package hireitservicesresources.callndata;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Config;

public class SaasSolutionsDetailsActivity extends AppCompatActivity {

    TextView amt,txt_desc;
    Button btn_hire;
    EditText bs_name,bs_email,bs_phone;
    Spinner spinner_technology,spinner_subscription_options;
    List<String> categories_exp,categories_timeUnit;
    ArrayList<String> arr_productNum,arr_productName,arr_productCph,arr_productYc,arr_productMc,arr_productBrief;
    static String catID;
    private static String TAG = SaasSolutionsDetailsActivity.class.getSimpleName();
    public static Double amt_total,amt_cph,amt_time_unit = 1.0;
    int product_pos;
    String message;
    static String orderno;
    String jsonObjectResponsepaypal1,jsonObjectResponsepaypal2 ;
    String paypal_id,pay_pal_state;

    private String urlJsonObj1 = "http://webservice.projectsclique.com/BuyServices.asmx/BuyServices";
    private String urlJsonObj_Updatestatus = "http://webservice.projectsclique.com/BuyServices.asmx/updateOrderPaymentStatus";
    private ProgressDialog pDialog;
    private static Dialog dialog ; // for popup dialog
    //paypal
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;



    // PayPal configuration
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID).merchantName("callndata Technologies")
            .merchantPrivacyPolicyUri(
                    Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(
                    Uri.parse("https://www.example.com/legal"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_saas_solutions_details);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        amt= (TextView)findViewById(R.id.amt);
        bs_name=(EditText)findViewById(R.id.bs_name);
        bs_email=(EditText)findViewById(R.id.bs_email);
        bs_phone=(EditText)findViewById(R.id.bs_phone);

        amt_total =0.0;


        amt_time_unit=0.0;
        Log.d(TAG, "amt_total = " + amt_total);


        btn_hire=(Button)findViewById(R.id.btn_hire);
        spinner_technology = (Spinner)findViewById(R.id.spinner_technology);
        spinner_technology.setFocusable(true);
        spinner_subscription_options = (Spinner)findViewById(R.id.spinner_subscription_options);
        txt_desc = (TextView)findViewById(R.id.txt_desc);

        Bundle b = getIntent().getExtras();
        catID = getIntent().getStringExtra("catId");
        arr_productNum=(ArrayList<String>)b.getStringArrayList("array_prodNum");
        Log.d(TAG, "product num = " +arr_productNum.toString());
        arr_productName= (ArrayList<String>)b.getStringArrayList("array_prodName");
        Log.d(TAG, "product name = " +arr_productName.toString());
        arr_productCph= (ArrayList<String>)b.getStringArrayList("array_prodCph");
        arr_productYc= (ArrayList<String>)b.getStringArrayList("array_prodYc");
        arr_productMc= (ArrayList<String>)b.getStringArrayList("array_prodMc");
        arr_productBrief= (ArrayList<String>)b.getStringArrayList("array_prodBrief");
        Log.d(TAG, "product brief = " +arr_productBrief.toString());

        spin_tech();
        spin_subscription_options();



        spinner_technology.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                product_pos = position;
                amt_cph= Double.parseDouble(arr_productCph.get(product_pos));
                calculation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        spinner_subscription_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:    //time is hourly
                        amt_time_unit = 1.0;

                        break;
                    case 1:         //time is daily
                        amt_time_unit = Double.parseDouble(arr_productYc.get(product_pos));
                        break;

                }

                calculation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btn_hire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bs_name.getText().toString().matches("")||bs_email.getText().toString().matches("")||bs_phone.getText().toString().matches(""))
                {
                    Toast.makeText(SaasSolutionsDetailsActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    makeJsonObjectRequestForHire();
                    launchPayPalPayment();
                }
            }
        });
    }


    private  void spin_tech()
    { // Spinner Drop down elements


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr_productName);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_technology.setAdapter(dataAdapter);

    }


    private  void spin_subscription_options()
    {// Spinner Drop down elements
        categories_timeUnit = new ArrayList<String>();
        categories_timeUnit.add("Monthly");
        categories_timeUnit.add("Yearly");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories_timeUnit);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_subscription_options.setAdapter(dataAdapter);}




    private void calculation()
    {
        Log.d(TAG, "amt_cph = " + amt_cph +  " ,amt_time_unit= " + amt_time_unit);
        amt_total = amt_cph *amt_time_unit;
        Log.d(TAG, "amt_total 1 = " + amt_total);
        amt_total= Math.round(amt_total * 100.0) / 100.0;
        Log.d(TAG, "amt_total 2= " + amt_total);
        amt.setText(String.valueOf(amt_total));
        txt_desc.setText(Html.fromHtml(arr_productBrief.get(product_pos)));
        Log.d(TAG, "product brief = " + arr_productBrief.get(product_pos));
    }

    private void payHire()
    {

    }

    /**
     * Method to make json object request where json response starts wtih {
     * */
    private void makeJsonObjectRequestForHire() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlJsonObj1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response.toString());
                        try {
                            JSONObject jsonRootObject = new JSONObject(response);
                            orderno = jsonRootObject.optString("status").toString();
                            message = jsonRootObject.optString("Message").toString();
                            Log.d(TAG,message);

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        hidepDialog();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, error.toString());
                        Toast.makeText(SaasSolutionsDetailsActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        hidepDialog();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("orderSource", "android");
                params.put("productCatID",catID);
                params.put("productID",arr_productNum.get(product_pos));
                params.put("productUnit",spinner_subscription_options.getSelectedItem().toString());
                params.put("noOfUnit","1");
                params.put("unitCost",arr_productCph.get(product_pos));
                params.put("experience"," ");
                params.put("totalCost",String.valueOf(amt_total));
                params.put("orderCurrency","USD");
                params.put("fullName",bs_name.getText().toString());
                params.put("contactNo",bs_phone.getText().toString());
                params.put("emailID",bs_email.getText().toString());

                Log.d(TAG,"orderSource= android"+ "  productCatID= "+ catID+ "  productID= "+arr_productNum.get(product_pos)+ "  productUnit= "
                        +spinner_subscription_options.getSelectedItem().toString()+"  noOfUnit= "+ ""
                        +"  unitCost= "+ arr_productCph.get(product_pos) + "  experience="+ " "
                        + "  totalCost= "+ String.valueOf(amt_total)+ "  fullName= "+ bs_name.getText().toString()
                        + "  contactNo= "+ bs_phone.getText().toString()+ "  emailID= "+ bs_email.getText().toString());
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    private void makeJsonObjectRequestForUpdate() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlJsonObj_Updatestatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response.toString());
                        try {
                            JSONObject jsonRootObject = new JSONObject(response);
                            orderno = jsonRootObject.optString("status").toString();
                            message = jsonRootObject.optString("Message").toString();
                            Log.d(TAG, " update: " + message );

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        hidepDialog();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, error.toString());
                        Toast.makeText(SaasSolutionsDetailsActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        hidepDialog();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("orderID", orderno);
                params.put("transactionCode",paypal_id);
                params.put("paymentStatus",pay_pal_state);
                params.put("paymentMode","paypal");

                Log.d(TAG,"orderID= "+ orderno +  "  transactionCode= "+ paypal_id+ "  paymentStatus= "
                        +pay_pal_state+  "  paymentMode = paypal ");
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void launchPayPalPayment() {

        PayPalPayment thingsToBuy = new PayPalPayment(new BigDecimal(amt_total), "USD",
                "Program", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(SaasSolutionsDetailsActivity.this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));

                        jsonObjectResponsepaypal1 = confirm.toJSONObject().toString(4);
                        jsonObjectResponsepaypal2 =  confirm.getPayment().toJSONObject()
                                .toString(4);
                        Log.d(TAG, "JSON OBJECT 1" + jsonObjectResponsepaypal1);
                        Log.d(TAG, "JSON OBJECT 2" +jsonObjectResponsepaypal2);

                        Log.d(TAG, "order placed");

                        JSONObject jsonObject = new JSONObject(jsonObjectResponsepaypal1);
                        JSONObject jsonObject1 = jsonObject.optJSONObject("response");
                        paypal_id = jsonObject1.optString("id").toString();
                        pay_pal_state = jsonObject1.optString("state").toString();

                        Log.d(TAG, "paypal id =  " + paypal_id + " , pay_pal_state =  " + pay_pal_state);
                        makeJsonObjectRequestForUpdate();

                        showDialogToOpen();
                        autoclose();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out
                        .println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject()
                                .toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(getApplicationContext(),
                                "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample",
                                "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }


    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

    }


    //popup dialog
    private void showDialogToOpen() {

        dialog = new Dialog(SaasSolutionsDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.thanks_screen);
        dialog.setCancelable(false);
        final TextView thanks_text = (TextView)dialog.findViewById(R.id.thanks_text);

        thanks_text.setText(message);

        dialog.show();

    }


    private void autoclose()
    {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SaasSolutionsDetailsActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // close this activity
                finish();
            }
        }, 3000);

    }
}

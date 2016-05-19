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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Config;


public class QuickPay_Activity extends AppCompatActivity {
    Button submit_quickpay;
    EditText quickpay_name,quickpay_email,quickpay_phone,quickpay_amt;
    double amt;
    private ProgressDialog pDialog;
    String message;
    static String orderno;
    String jsonObjectResponsepaypal1,jsonObjectResponsepaypal2 ;
    String paypal_id,pay_pal_state;

    private static Dialog dialog ; // for popup dialog
    private String urlJsonObj1 = "http://webservice.projectsclique.com/BuyServices.asmx/BuyServices";
    private String urlJsonObj_Updatestatus = "http://webservice.projectsclique.com/BuyServices.asmx/updateOrderPaymentStatus";
    private static String TAG = QuickPay_Activity.class.getSimpleName();

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_quick_pay_);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        submit_quickpay = (Button)findViewById(R.id.submit_quickpay);
        quickpay_name = (EditText)findViewById(R.id.quickpay_name);
        quickpay_email = (EditText)findViewById(R.id.quickpay_email);
        quickpay_phone = (EditText)findViewById(R.id.quickpay_phone);
        quickpay_amt = (EditText)findViewById(R.id.quickpay_amt);

        submit_quickpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quickpay_name.getText().toString().matches("")||quickpay_email.getText().toString().matches("")||
                quickpay_phone.getText().toString().matches("")||quickpay_amt.getText().toString().matches(""))
                        Toast.makeText(getApplicationContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();

                else {
                    amt = Double.parseDouble(quickpay_amt.getText().toString());
                    makeJsonObjectRequestForHire();
                    launchPayPalPayment();
                }
            }
        });


    }

    private void launchPayPalPayment() {
        PayPalPayment thingsToBuy = new PayPalPayment(new BigDecimal(amt), "USD",
                "Program", PayPalPayment.PAYMENT_INTENT_SALE);

    /*          PayPalPayment thingsToBuy = new PayPalPayment(new BigDecimal(0.01), "USD",
                "Program", PayPalPayment.PAYMENT_INTENT_SALE);
*/
        Intent intent = new Intent(QuickPay_Activity.this, PaymentActivity.class);

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


    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Application Correlation ID from the SDK
        String correlationId = PayPalConfiguration
                .getApplicationCorrelationId(this);

        Log.i("FuturePaymentExample", "Application Correlation ID: "
                + correlationId);

        // TODO: Send correlationId and transaction details to your server for
        // processing with
        // PayPal...
        Toast.makeText(getApplicationContext(),
                "App Correlation ID received from SDK", Toast.LENGTH_LONG)
                .show();
    }


    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(QuickPay_Activity.this,
                PayPalFuturePaymentActivity.class);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
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
                        Toast.makeText(QuickPay_Activity.this,error.toString(),Toast.LENGTH_LONG).show();
                        hidepDialog();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
               params.put("orderSource", "android");
                params.put("productCatID","0");
                params.put("productID","0");
                params.put("productUnit","");
                params.put("noOfUnit","1");
                params.put("unitCost",quickpay_amt.getText().toString());
                params.put("experience","");
                params.put("totalCost",quickpay_amt.getText().toString());
                params.put("orderCurrency","USD");
                params.put("fullName",quickpay_name.getText().toString());
                params.put("contactNo", quickpay_phone.getText().toString());
                params.put("emailID",quickpay_email.getText().toString());




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
                        Toast.makeText(QuickPay_Activity.this,error.toString(),Toast.LENGTH_LONG).show();
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

                Log.d(TAG, "orderID= " + orderno + "  transactionCode= " + paypal_id + "  paymentStatus= "
                        + pay_pal_state + "  paymentMode = paypal ");
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


    //popup dialog
    private void showDialogToOpen() {

        dialog = new Dialog(QuickPay_Activity.this);
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
                Intent i = new Intent(QuickPay_Activity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // close this activity
                finish();
            }
        }, 3000);

    }
}

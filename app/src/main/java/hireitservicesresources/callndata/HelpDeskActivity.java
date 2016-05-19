package hireitservicesresources.callndata;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HelpDeskActivity extends AppCompatActivity {
    EditText hd_name,hd_email,hd_subject,hd_msg;
    Button hd_submit;
    private ProgressDialog pDialog;
    private String urlJsonObj1 = "http://webservice.projectsclique.com/supportTicket.asmx/RequestSupport";
    private static String TAG = QuickPay_Activity.class.getSimpleName();

    private static Dialog dialog ;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_desk);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        hd_name = (EditText)findViewById(R.id.hd_name);
        hd_email = (EditText)findViewById(R.id.hd_email);
        hd_subject = (EditText)findViewById(R.id.hd_subject);
        hd_msg = (EditText)findViewById(R.id.hd_msg);

        hd_submit = (Button)findViewById(R.id.hd_submit);
        hd_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hd_name.getText().toString().matches("")||hd_email.getText().toString().matches("")
                        || hd_subject.getText().toString().matches("")||hd_msg.getText().toString().matches(""))
                Toast.makeText(HelpDeskActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else
                makeJsonObjectRequestForHelp();
            }
        });

    }


    /**
     * Method to make json object request where json response starts wtih {
     * */
    private void makeJsonObjectRequestForHelp() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlJsonObj1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response.toString());
                        try {
                            JSONObject jsonRootObject = new JSONObject(response);
                            String orderno = jsonRootObject.optString("status").toString();
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
                        showDialogToOpen();
                        autoclose();

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, error.toString());
                        Toast.makeText(HelpDeskActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        hidepDialog();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("TicketSource", "android");
                params.put("companyBranchID","1");
                params.put("fullName",hd_name.getText().toString());
                params.put("contactNo", "");
                params.put("emailID",hd_email.getText().toString());
                params.put("subject",hd_subject.getText().toString());
                params.put("message",hd_msg.getText().toString());



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

        dialog = new Dialog(HelpDeskActivity.this);
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
                Intent i = new Intent(HelpDeskActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // close this activity
                finish();
            }
        }, 3000);

    }
}

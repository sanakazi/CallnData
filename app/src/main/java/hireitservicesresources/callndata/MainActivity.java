package hireitservicesresources.callndata;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
 Button btn_buyservice,btn_quickpay,btn_contactus,btn_helpdesk,btn_saas,btn_custom_solutions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_main);
        btn_buyservice = (Button)findViewById(R.id.btn_buyservice);
        btn_quickpay = (Button)findViewById(R.id.btn_quickpay);
        btn_contactus = (Button)findViewById(R.id.btn_contactus);
        btn_helpdesk = (Button)findViewById(R.id.btn_helpdesk);
        btn_saas = (Button)findViewById(R.id.btn_saas);
        btn_custom_solutions = (Button)findViewById(R.id.btn_custom_solutions);

        btn_buyservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_buyservice = new Intent(MainActivity.this,BuyServiceActivity.class);
                startActivity(intent_buyservice);
            }
        });


        btn_saas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_saas = new Intent(MainActivity.this,SaasSolutionsActivity.class);
                startActivity(intent_saas);
            }
        });

        btn_custom_solutions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_custom_solutions = new Intent(MainActivity.this,CustomSolutionsActivity.class);
                startActivity(intent_custom_solutions);
            }
        });


        btn_quickpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_quickpay = new Intent(MainActivity.this,QuickPay_Activity.class);
                startActivity(intent_quickpay);
            }
        });

        btn_contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_contact_info = new Intent(MainActivity.this,ContactInfo_Activity.class);
                startActivity(intent_contact_info);
            }
        });

        btn_helpdesk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_helpdesk= new Intent(MainActivity.this,HelpDeskActivity.class);
                startActivity(intent_helpdesk);
            }
        });
    }
}

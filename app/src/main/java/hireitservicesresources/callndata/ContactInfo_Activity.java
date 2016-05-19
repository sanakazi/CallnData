package hireitservicesresources.callndata;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ContactInfo_Activity extends AppCompatActivity {
    TextView ph1,ph2,ph3,email1,email2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_contact_info_);
        ph1= (TextView)findViewById(R.id.ph1);
        ph2= (TextView)findViewById(R.id.ph2);
        ph3= (TextView)findViewById(R.id.ph3);
        email1= (TextView)findViewById(R.id.email1);
        email2= (TextView)findViewById(R.id.email2);

        ph1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:+91-876 747 7454"));
                try{startActivity(phoneIntent);}
                catch (Exception e) {
                    Log.w("Exception error is" , e.toString());
                }
            }
        });

        ph2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:+91-22 3222 4450"));
                try{startActivity(phoneIntent);}
                catch (Exception e) {
                    Log.w("Exception error is" , e.toString());
                }
            }
        });

        ph3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:+1 321 248 6126"));
                try{startActivity(phoneIntent);}
                catch (Exception e) {
                    Log.w("Exception error is" , e.toString());
                }

            }
        });

        email1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{">info@CallnData.com"});
                emailIntent.setType("message/rfc822");
                startActivity(emailIntent);
            }
        });


        email2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"na@CallnData.com"});
                emailIntent.setType("message/rfc822");
                startActivity(emailIntent);
            }
        });

    }
}

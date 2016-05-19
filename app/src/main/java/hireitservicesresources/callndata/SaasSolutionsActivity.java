package hireitservicesresources.callndata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import adapters.CategoryAdapter;
import models.CategoryModel;

public class SaasSolutionsActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    ListView list_category;
    CategoryAdapter adapter;
    ArrayList<CategoryModel> arr;
    CategoryModel sched;
    private static String TAG = SaasSolutionsActivity.class.getSimpleName();
    private String urlJsonObj1 = "http://webservice.projectsclique.com/ProductsCategory.asmx/ProductCategoryListProductTypeWise";

    List<String> values;
    List<String> list_productNum,list_productName,list_product_cph,list_product_mc,list_product_yc,list_product_brief;
    String productCategoryName;
    int categoryId;

    int productNum;
    String productName,costPerHr,monthlyCostMultiply,yearlyCostMultiply,productBrief;;

    Map<String, List<String>> hm_prod_num = new TreeMap<String, List<String>>();
    Map<String, List<String>> hm_prod_name = new TreeMap<String, List<String>>();
    Map<String, List<String>> hm_prod_cph = new TreeMap<String, List<String>>();
    Map<String, List<String>> hm_prod_mc = new TreeMap<String, List<String>>();
    Map<String, List<String>> hm_prod_yc = new TreeMap<String, List<String>>();
    Map<String, List<String>> hm_prod_brief = new TreeMap<String, List<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saas_solutions);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        list_category = (ListView)findViewById(R.id.list_category);
        list_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CategoryModel csm = arr.get(position);
                String pass_catId= String.valueOf(csm.getCategoryId());
                Log.d(TAG, "catId = " + pass_catId);
                Log.d(TAG,   "TreeMap is " +(ArrayList)hm_prod_name.get(pass_catId));

                if(hm_prod_name.get(pass_catId)==null)
                {
                    Log.d(TAG,"SIZE IS ZERO");
                    Toast.makeText(SaasSolutionsActivity.this,"No Items Present",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(SaasSolutionsActivity.this, SaasSolutionsDetailsActivity.class);

                    intent.putExtra("catId", String.valueOf(pass_catId));
                    intent.putStringArrayListExtra("array_prodNum", (ArrayList) hm_prod_num.get(pass_catId));
                    intent.putStringArrayListExtra("array_prodName", (ArrayList) hm_prod_name.get(pass_catId));
                    intent.putStringArrayListExtra("array_prodCph", (ArrayList) hm_prod_cph.get(pass_catId));
                    intent.putStringArrayListExtra("array_prodMc", (ArrayList) hm_prod_mc.get(pass_catId));
                    intent.putStringArrayListExtra("array_prodYc", (ArrayList) hm_prod_yc.get(pass_catId));
                    intent.putStringArrayListExtra("array_prodBrief", (ArrayList) hm_prod_brief.get(pass_catId));


                    startActivity(intent);
                }

            }
        });

        makeJsonObjectRequestForCategory();
    }




    /**
     * Method to make json object request where json response starts wtih {
     * */
    private void makeJsonObjectRequestForCategory() {
        showpDialog();
        arr = new ArrayList<CategoryModel>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlJsonObj1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response.toString());
                        try {
                            JSONObject jsonRootObject = new JSONObject(response);
                            JSONArray jsonArray = jsonRootObject.optJSONArray("Message");
                            for(int i=0; i < jsonArray.length(); i++) {
                                list_productNum = new ArrayList<String>();
                                list_productName = new ArrayList<String>();
                                list_product_cph = new ArrayList<String>();
                                list_product_mc = new ArrayList<String>();
                                list_product_yc = new ArrayList<String>();
                                list_product_brief = new ArrayList<String>();


                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Log.d(TAG, "category = " + jsonObject.toString());

                                categoryId = Integer.parseInt(jsonObject.optString("categoryID").toString());
                                productCategoryName = jsonObject.optString("fullName").toString();


                                sched = new CategoryModel();
                                sched.setCat_fullName(productCategoryName);
                                sched.setCategoryId(categoryId);
                                arr.add(sched);

                                JSONArray jsonArray1 = jsonObject.optJSONArray("ProductList");
                                for(int j=0; j < jsonArray1.length(); j++) {

                                    JSONObject jsonObjectchild = jsonArray1.getJSONObject(j);
                                    Log.d(TAG, "Items = " + jsonObjectchild.toString());
                                    productNum = Integer.parseInt(jsonObjectchild.optString("num").toString());
                                    productName = jsonObjectchild.optString("fullName").toString();
                                    costPerHr = jsonObjectchild.optString("costPerHr").toString();
                                    monthlyCostMultiply =  jsonObjectchild.optString("monthlyCostMultiply").toString();
                                    yearlyCostMultiply = jsonObjectchild.optString("yearlyCostMultiply").toString();
                                    productBrief = jsonObjectchild.optString("productBrief").toString();


                                    list_productNum.add(String.valueOf(productNum));
                                    list_productName.add(productName);
                                    list_product_cph.add(costPerHr);
                                    list_product_mc.add(monthlyCostMultiply);
                                    list_product_yc.add(yearlyCostMultiply);
                                    list_product_brief.add(productBrief);

                                    hm_prod_num.put(String.valueOf(categoryId), list_productNum);
                                    hm_prod_name.put(String.valueOf(categoryId), list_productName);
                                    hm_prod_cph.put(String.valueOf(categoryId), list_product_cph);
                                    hm_prod_mc.put(String.valueOf(categoryId), list_product_mc);
                                    hm_prod_yc.put(String.valueOf(categoryId), list_product_yc);
                                    hm_prod_brief.put(String.valueOf(categoryId), list_product_brief);


                                }


                            }

                            for (Map.Entry<String, List<String>> entry : hm_prod_name.entrySet()) {
                                String key = entry.getKey();
                                values = entry.getValue();
                                Log.d(TAG, key + "=" + values.toString() );


                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        adapter=new CategoryAdapter( SaasSolutionsActivity.this, arr );
                        list_category.setAdapter(adapter);
                        hidepDialog();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, error.toString());
                        Toast.makeText(SaasSolutionsActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        hidepDialog();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("companyBranchID", "1");
                params.put("productCatType", "SaaS");

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
}

package com.ram.ram.bustrack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

public class login extends AppCompatActivity {
    EditText lusername, lpassword;
    Button lbutton;
    ProgressDialog pg;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lusername = findViewById(R.id.lusername);
        lpassword = findViewById(R.id.lpassword);
        lbutton = findViewById(R.id.login);
        pg = new ProgressDialog(login.this);

        lbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = lusername.getText().toString();
                password = lpassword.getText().toString();
                loginn();
            }
        });
    }

    public void loginn() {
        RequestQueue rq = Volley.newRequestQueue(login.this);
        pg.setMessage("Loading...");
        pg.show();
        String url = "https://electrosoftbustrack.000webhostapp.com/login.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("json exeption on login response", response.toString());
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    if (status.contentEquals("success"))
                    {
                        JSONObject data = js.getJSONObject("data");
                        Log.e("json reposne",data.toString());
                        String role = data.getString("role");
                        SharedPreferences sh = getSharedPreferences("user",MODE_PRIVATE);
                        SharedPreferences.Editor edt = sh.edit();
                        edt.putString("busname",data.getString("busname"));
                        edt.commit();
                        if (role.equalsIgnoreCase("admin"))
                        {
                            Intent i = new Intent(login.this,adminmain.class);
                            startActivity(i);
                        }else if (role.equalsIgnoreCase("user"))
                        {
                            Intent i = new Intent(login.this,usermain.class);
                            startActivity(i);
                        }

                    }
                }catch (JSONException je)
                {
                    Log.e("json exeption on login response", je.toString());
                }

                pg.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley exeption on login response", error.toString());
                pg.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("username", username);
                params.put("Password", password);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(strReq);
    }

}

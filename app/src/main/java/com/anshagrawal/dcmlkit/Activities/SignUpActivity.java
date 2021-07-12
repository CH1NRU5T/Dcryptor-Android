package com.anshagrawal.dcmlkit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anshagrawal.dcmlkit.UtilsService.SharedPreferencesClass;
import com.anshagrawal.dcmlkit.UtilsService.UtilService;
import com.anshagrawal.dcmlkit.databinding.ActivitySignUpBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private String username, email, password, cnfPassword;
    UtilService utilService;
    SharedPreferencesClass sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utilService = new UtilService();
        sharedPreferences = new SharedPreferencesClass(this);


        binding.loginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilService.hideKeyboard(v, SignUpActivity.this);
                username = binding.username.getText().toString();
                email = binding.email.getText().toString();
                password = binding.password.getText().toString();
                cnfPassword = binding.cnfPassword.getText().toString();


                if (validate(v)) {
                    registerUser();
                }
            }

        });


    }

    private void registerUser() {
        //final HashMap<String, String> params = new HashMap<>();
        JSONObject params = new JSONObject();
        try {
            params.put("username", username);
            params.put("email", email);
            params.put("password", password);
            params.put("confirmPassword", cnfPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url = "https://acm-dcryptor.herokuapp.com/api/v1/signup";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("Status")) {
                        String token = response.getString("token");
                        sharedPreferences.setValueString("token", token);
                        Toast.makeText(SignUpActivity.this, token, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                        JSONObject jsonObject = new JSONObject(res);
                        Toast.makeText(SignUpActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        //setting of retry policy
        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        //adding request
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    public boolean validate(View v1) {
        boolean isValid;

        if (!TextUtils.isEmpty(username)) {
            if (!TextUtils.isEmpty(email)) {
                if (!TextUtils.isEmpty(password)) {
                    if (!TextUtils.isEmpty(cnfPassword)) {
                        isValid = true;
                    } else {
                        utilService.showSnackBar(v1, "Please enter Confirm Password...");
                        isValid = false;

                    }
                } else {
                    utilService.showSnackBar(v1, "Please enter password...");
                    isValid = false;

                }
            } else {
                utilService.showSnackBar(v1, "Please enter email...");
                isValid = false;

            }
        } else {
            utilService.showSnackBar(v1, "Please enter username...");
            isValid = false;

        }
        return isValid;
    }


    @Override
    protected void onStart() {
        super.onStart();
        android.content.SharedPreferences preferences = getSharedPreferences("user_cypher", MODE_PRIVATE);
        if (preferences.contains("token")) {
            startActivity(new Intent(SignUpActivity.this, DashboardActivity.class));
            finish();
        }
    }
}
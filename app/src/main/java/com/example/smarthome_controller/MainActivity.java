package com.example.smarthome_controller;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.smarthome_controller.data.User_Data;
import com.google.gson.Gson;
import com.example.smarthome_controller.Database_Connection.VolleySingleton;


public class MainActivity extends AppCompatActivity {
    EditText user_email, user_password;
    Button Login;
    CheckBox checkBox;
    String Email, Password;
    HashMap<String, String> postinguserdata;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_email = findViewById(R.id.Email_txt);
        user_password = findViewById(R.id.Password_txt);
        Login = findViewById(R.id.Login_btn);
        checkBox = findViewById(R.id.checkBox);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        preferences = getApplicationContext().getSharedPreferences("checkbox", 0);
        editor = preferences.edit();

        boolean checker = preferences.getBoolean("remember", false);

        if (checker == true) {
            user_email.setVisibility(View.GONE);
            user_password.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
            Login.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            checkBox.setChecked(true);
            postinguserdata = new HashMap<>();
            postinguserdata.put("Email", preferences.getString("user_email", null));
            postinguserdata.put("Password", preferences.getString("user_password", null));
            selectuser("Users", postinguserdata);
        }
            Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Email = user_email.getText().toString();
                    Password = user_password.getText().toString();
                    //Checking If the fileds are empty or not.......
                    checkBox = findViewById(R.id.checkBox);


                    if (Email.isEmpty() && Password.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Fill the blank", Toast.LENGTH_LONG).show();
                    } else if (Email.isEmpty()) {

                        user_email.setError("Enter Email");
                        user_email.requestFocus();

                    } else if (Password.isEmpty()) {
                        user_password.setError("Enter Password");
                        user_password.requestFocus();
                    } else if (!Email.isEmpty() && !Password.isEmpty()){


                        progressBar.setVisibility(View.VISIBLE);

                        postinguserdata = new HashMap<>();

                        postinguserdata.put("Email", user_email.getText().toString());
                        postinguserdata.put("Password", user_password.getText().toString());

                        selectuser("Users", postinguserdata);
                    }



                }
            });
        }


    public void selectuser(final String tableName, HashMap<String, String> conditions) {
        Gson gson = new Gson();
        final String con = gson.toJson(conditions);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://andrinosmarthome.000webhostapp.com/selectuser.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getBoolean("error")) {
                        progressBar.setVisibility(View.INVISIBLE);
                        System.out.println(obj.getString("message"));
                        Toast.makeText(getApplicationContext(), "Faild :" + obj.getString("message"), Toast.LENGTH_LONG).show();

                    } else {

                        JSONArray arr = obj.getJSONArray("Users");
                        final JSONObject userObject = (JSONObject) arr.get(0);
                        final User_Data userData = new User_Data();
                        userData.setID(userObject.getInt("ID"));
                        userData.setName(userObject.getString("Name"));
                        userData.setEmail(userObject.getString("Email"));
                        userData.setPassword(userObject.getString("Password"));
                        userData.setImgUrl(userObject.getString("Image_URL"));

                                if (checkBox.isChecked())
                                {
                                    editor.putString("user_email",userData.getEmail());
                                    editor.putString("user_password",userData.getPassword());
                                    editor.putBoolean("remember",true);
                                    editor.commit();

                                }
                                else if (!checkBox.isChecked())
                                {
                                    editor.putString("user_email",null);
                                    editor.putString("user_password",null);
                                    editor.putBoolean("remember",false);
                                    editor.commit();
                                }


                        Intent intent = new Intent(getApplicationContext(), Home_Page.class);
                        intent.putExtra("User_Data", (Serializable) userData);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        user_email.setVisibility(View.VISIBLE);
                        user_password.setVisibility(View.VISIBLE);
                        checkBox.setVisibility(View.VISIBLE);
                        Login.setVisibility(View.VISIBLE);
                        Login.setClickable(true);

                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Incorrect Login -> check your internet connection ", Toast.LENGTH_LONG).show();
                    }
                }

        ) {
            @Override
            //Those are the parameters that we sent in Hashmap to the Php file to Query on it and give data back to mobile
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("table", tableName);
                param.put("conditions", con);
                return param;
            }
        };
        //This is a design pattern we created only to check if there is already an existing connectiong be
        VolleySingleton.getnInstance(this).addRequestQue(stringRequest);
    }
}
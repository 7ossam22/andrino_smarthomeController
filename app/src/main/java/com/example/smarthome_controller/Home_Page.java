package com.example.smarthome_controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.smarthome_controller.Database_Connection.VolleySingleton;
import com.example.smarthome_controller.data.Devices_Adapter;
import com.example.smarthome_controller.data.Devices_Data;
import com.example.smarthome_controller.data.User_Data;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class Home_Page extends AppCompatActivity {
    HashMap<String, String> hashMap;
    RecyclerView recyclerView;
    User_Data userData;
    CircleImageView imageView;

    String Id;
    String url;
    AlertDialog alertDialog;
    AlertDialog.Builder alertdialogbuilder;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__page);

        imageView = findViewById(R.id.user_img);
        recyclerView = findViewById(R.id.device_rv);

        preferences = getApplicationContext().getSharedPreferences("checkbox", 0);
        editor = preferences.edit();

        hashMap = new HashMap<>();
        Intent intent = getIntent();
        userData = (User_Data) intent.getSerializableExtra("User_Data");
        url = userData.getImgUrl();
        Integer ID = userData.getID();
        Id = Integer.toString(ID);

        getimageurl();

        hashMap.put("User_ID", Id);
        selectContent("Devices", hashMap);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(Home_Page.this, imageView);
                popupMenu.getMenuInflater().inflate(R.menu.logout_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        alertdialogbuilder = new AlertDialog.Builder(Home_Page.this);
                        alertdialogbuilder.setTitle("Logout Confirmation...");
                        alertdialogbuilder.setIcon(R.drawable.ic_baseline_exit);
                        alertdialogbuilder.setMessage("Are you sure you want to logout?!");
                        alertdialogbuilder.setCancelable(false);
                        alertdialogbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                editor.clear();
                                editor.commit();
                                startActivity(new Intent(Home_Page.this, MainActivity.class));
                                finish();
                            }
                        });
                        alertdialogbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertdialogbuilder.setCancelable(true);
                            }
                        });
                        alertDialog = alertdialogbuilder.create();
                        alertDialog.show();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }


    public void selectContent(final String tableName, HashMap<String, String> conditions) {
        Gson gson = new Gson();
        final String con = gson.toJson(conditions);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://andrinosmarthome.000webhostapp.com/selectDevice.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getBoolean("error")) {
                        System.out.println(obj.getString("message"));
                    } else {
                        final ArrayList<Devices_Data> devices = new ArrayList<>();
                        JSONArray arr = obj.getJSONArray("Devices");
                        for (int i = 0; i < arr.length(); i++) {
                            final JSONObject jsonObject = (JSONObject) arr.get(i);
                            final Devices_Data devices_data = new Devices_Data();
                            devices_data.setIP(jsonObject.getString("IP"));
                            devices_data.setName(jsonObject.getString("Name"));
                            devices_data.setDevice_img(jsonObject.getString("Image_URL"));
                            devices.add(devices_data);
                        }
                        Devices_Adapter adapter = new Devices_Adapter(getApplicationContext(), devices, Home_Page.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("table", tableName);
                param.put("conditions", con);
                return param;
            }
        };

        VolleySingleton.getnInstance(this).addRequestQue(stringRequest);
    }


    public void getimageurl() {
        Picasso.get().load(url).placeholder(R.drawable.ic_baseline_lock).error(R.drawable.ic_baseline_lock).into(imageView, new com.squareup.picasso.Callback() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

}
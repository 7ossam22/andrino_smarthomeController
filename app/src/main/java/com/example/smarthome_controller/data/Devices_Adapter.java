package com.example.smarthome_controller.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.example.smarthome_controller.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Devices_Adapter extends RecyclerView.Adapter<Devices_Adapter.MyViewHolder> {
    ArrayList<Devices_Data> arrayList;
    Context context;
    Activity activity;
    public Devices_Adapter(Context ct,ArrayList<Devices_Data> arr,Activity activity) {
        this.arrayList = arr;
        this.context = ct;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Devices_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.device_rv_row_design,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Devices_Adapter.MyViewHolder holder, final int position) {
        holder.DeviceName.setText(arrayList.get(position).getName());
        holder.DeviceIP.setText(arrayList.get(position).getIP());
        getimageurl(arrayList.get(position).getDevice_img(),holder.Device_img);
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if (holder.aSwitch.isPressed() && b==true)
                {holder.aSwitch.setText("ON");
                    final RequestQueue requestQueue;
                    Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
                    Network network = new BasicNetwork(new HurlStack());
                    requestQueue = new RequestQueue(cache, network);
                    requestQueue.start();
                    String url =("http://"+arrayList.get(position).getIP()+"/on");
                    System.out.println(url);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Toast.makeText(context,"Successfully Device On",Toast.LENGTH_LONG).show();
                                    Log.v("TAG", "Successfully Led on/off");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle error
                                    Toast.makeText(context,"Failed To Contact the Device",Toast.LENGTH_LONG).show();
                                    Log.v("TAG", "FAILD");
                                    holder.aSwitch.setChecked(false);
                                    holder.aSwitch.setText("OFF");

                                }
                            });
                    requestQueue.add(stringRequest);
                }
                else if(holder.aSwitch.isPressed() && b==false)
                {holder.aSwitch.setText("OFF");
                    RequestQueue requestQueue;
                    Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
                    Network network = new BasicNetwork(new HurlStack());
                    requestQueue = new RequestQueue(cache, network);
                    requestQueue.start();
                    String url =("http://"+arrayList.get(position).getIP()+"/off");
                    System.out.println(url);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(context,"Successfully Device Off",Toast.LENGTH_LONG).show();
                                    Log.v("TAG", "Successfully Led on/off");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle error
                                    Toast.makeText(context,"Failed To Contact the Device",Toast.LENGTH_LONG).show();
                                    Log.v("TAG", "FAILD");
                                    holder.aSwitch.setChecked(true);
                                    holder.aSwitch.setText("ON");
                                }
                            });
                    requestQueue.add(stringRequest);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView DeviceName,DeviceIP;
        Switch aSwitch;
        ImageView Device_img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DeviceName = itemView.findViewById(R.id.device_name);
            DeviceIP = itemView.findViewById(R.id.device_IP);
            aSwitch = itemView.findViewById(R.id.switch1);
            Device_img = itemView.findViewById(R.id.Device_img);
        }
    }
    public void getimageurl(String url , ImageView img) {
        Picasso.get().load(url).placeholder(R.drawable.ic_baseline_lock).error(R.drawable.ic_baseline_lock).into(img, new com.squareup.picasso.Callback() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context,"Failed to Load Image",Toast.LENGTH_LONG).show();
            }
        });

    }
}

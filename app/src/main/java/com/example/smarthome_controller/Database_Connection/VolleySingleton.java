package com.example.smarthome_controller.Database_Connection;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class VolleySingleton {
    private  static VolleySingleton nInstance;
    private RequestQueue RQ;
    private Context ct;
    public VolleySingleton(Context ct){
        this.ct = ct;
        this.RQ = getRequestQueue();
    }
  //Checking if there is already a RequestQueue or not
    public RequestQueue getRequestQueue() {
        if(this.RQ==null){
            RQ = Volley.newRequestQueue(ct.getApplicationContext());
        }
        return  RQ;
    }
    //Checking if there is already a instance or not
    public static synchronized VolleySingleton getnInstance(Context context)
    {
        if(nInstance==null)
        {
            nInstance=new VolleySingleton(context);
        }
        return nInstance;
    }
    // without <T> -> It will causes Error in adding RequestQueue cause it used to configure it to multiple data types
    public <T> void addRequestQue (Request<T> request) {
        RQ.add(request);
    }
}

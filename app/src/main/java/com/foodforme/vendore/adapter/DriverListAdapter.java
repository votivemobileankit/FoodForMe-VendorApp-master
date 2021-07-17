package com.foodforme.vendore.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.foodforme.vendore.R;
import com.foodforme.vendore.SetterGatter.AddonDatum;
import com.foodforme.vendore.SetterGatter.DriverListModel;
import com.foodforme.vendore.SetterGatter.Example;
import com.foodforme.vendore.SetterGatter.OrderCarditem;
import com.foodforme.vendore.SetterGatter.OrderDetail;
import com.foodforme.vendore.serverintegration.Constant;
import com.foodforme.vendore.utils.ApplicationController;
import com.foodforme.vendore.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.foodforme.vendore.activity.OrderStatusActivity.mOrderStatusActivity;

public class DriverListAdapter extends RecyclerView.Adapter<DriverListAdapter.RecyclerViewViewHolder> {

    List<AddonDatum> mFoodAddonList;
    private View mView;
    Context mContext;
    JSONObject jsonObject;
    String requestBody;
    ArrayList<DriverListModel> driverListModelArrayList;
    String CustomerID, OrderID;
    AlertDialog alertDialog;

    @NonNull
    @Override
    public RecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_list, parent, false);
        RecyclerViewViewHolder viewHolder = new RecyclerViewViewHolder(mView);
        jsonObject = new JSONObject();

        return viewHolder;
    }


    public DriverListAdapter(AlertDialog mDialog, Context m, ArrayList<DriverListModel> mList, String Customer_id, String order_id) {
        this.driverListModelArrayList = mList;
        this.mContext = m;
        this.CustomerID = Customer_id;
        this.OrderID = order_id;
        this.alertDialog = mDialog;


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewViewHolder holder, final int position) {
        holder.driver_name_tv.setText(driverListModelArrayList.get(position).getName());
        holder.driver_email_tv.setText(driverListModelArrayList.get(position).getEmail());
        holder.driver_number_tv.setText(driverListModelArrayList.get(position).getUserMob());

        holder.assign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignOrderMethod(alertDialog, driverListModelArrayList.get(position).getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return driverListModelArrayList.size();
    }

    public class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
        TextView driver_name_tv, driver_email_tv, driver_number_tv;
        Button assign_btn;

        public RecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            driver_name_tv = itemView.findViewById(R.id.driver_name_tv);
            driver_email_tv = itemView.findViewById(R.id.driver_email_tv);
            driver_number_tv = itemView.findViewById(R.id.driver_number_tv);
            assign_btn = itemView.findViewById(R.id.assign_btn);
        }
    }

    private void assignOrderMethod(final AlertDialog alertDialog, String id) {

        try {
            jsonObject.put("userid", id);
            jsonObject.put("orderid", OrderID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestBody = jsonObject.toString();
        Log.e("driver apis >>>", Constant.assignordertodriver);
        Log.e("driver apis >>>", requestBody);
        StringRequest signinRequest = new StringRequest(Request.Method.POST, Constant.assignordertodriver,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("", "assignordertodriver: " + response);
                        try {
                            JSONObject jObject = new JSONObject(response);
                            String message = jObject.optString("message");
                            if (message.equals("Order Confirm!")) {
                                alertDialog.dismiss();
                                mOrderStatusActivity.callAgainMethod();
                            }
                            //{"message":"Order Confirm!","ok":1}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", "onErrorResponse: " + error.getMessage());


                    }
                }) {


            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };
        requestQueue.add(signinRequest);


    }
}


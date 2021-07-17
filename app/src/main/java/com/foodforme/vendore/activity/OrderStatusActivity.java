package com.foodforme.vendore.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.foodforme.vendore.R;
import com.foodforme.vendore.SetterGatter.DriverListModel;
import com.foodforme.vendore.SetterGatter.Example;
import com.foodforme.vendore.SetterGatter.FoodSizeDetail;
import com.foodforme.vendore.SetterGatter.OrderCarditem;
import com.foodforme.vendore.SetterGatter.OrderDetail;
import com.foodforme.vendore.SetterGatter.SetGet;
import com.foodforme.vendore.adapter.DriverListAdapter;
import com.foodforme.vendore.adapter.OrderInformationAdapter;
import com.foodforme.vendore.customwidget.CustomTextView;
import com.foodforme.vendore.fragments.OrderHistoryFragment;
import com.foodforme.vendore.languagechange.LocaleHelper;
import com.foodforme.vendore.serverintegration.Constant;
import com.foodforme.vendore.utils.ApplicationController;
import com.foodforme.vendore.utils.TransparentProgressDialog;
import com.foodforme.vendore.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderStatusActivity extends AppCompatActivity {

    private static final String TAG = OrderStatusActivity.class.getSimpleName();
    Toolbar toolbar;
    Context mContext;
    Fragment fragment = null;

    CustomTextView tv_note, restrorent_name, order_delivery_time, order_id_txt, payment_txt, sub_total_txt, discount_txt,
            delivery_fee_txt, salse_tax_txt, total_txt, grand_total_txt, update_status_btn, toolbar_title, order_status_txt;
    RecyclerView order_infomation_list;
    RelativeLayout order_accept_btn, order_reject_btn, new_order, order_conf, order_ready, order_status_layout, back_relative;
    LinearLayout accept_reject_layout;
    EditText Resion_ext;
    ImageView order_ready_unselect, order_ready_select, order_conf_select, order_conf_unselect, down_arrow, print_image;
    OrderInformationAdapter orderInformationAdapter;
    ArrayList<SetGet.orderdetail> order_carditem = new ArrayList<SetGet.orderdetail>();
    JSONObject jObject;
    TransparentProgressDialog progressDialog;
    AlertDialog alertDialog;
    Date mParsedDate;
    String mOutputDateString, invoice_url = "";
    SharedPreferences preferences;
    String delivery_manage_value = "", Customer_id = "", driver_accept_status = "", Driver_name = "", driver_id = "";
    RelativeLayout update_status_layout, assign_order_layout;
    TextView selecte_driver_txt;
    DriverListAdapter mDriverListAdapter;
    DriverListAdapter mDriverListAdapterFoodForMe;
    ArrayList<DriverListModel> driverListModelArrayList;
    ArrayList<DriverListModel> driverListModelArrayList1;
    static public OrderStatusActivity mOrderStatusActivity;
    String OrderStatus = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        progressDialog = TransparentProgressDialog.getInstance();
        preferences = getSharedPreferences("Vendor", MODE_PRIVATE);
        mContext = this;
        mOrderStatusActivity = this;
        jObject = new JSONObject();
        driverListModelArrayList = new ArrayList<>();
        driverListModelArrayList1 = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        restrorent_name = findViewById(R.id.restrorent_name);
        order_delivery_time = findViewById(R.id.order_delivery_time);
        print_image = findViewById(R.id.print_image);
        order_id_txt = findViewById(R.id.order_id_txt);
        payment_txt = findViewById(R.id.payment_txt);
        sub_total_txt = findViewById(R.id.sub_total_txt);
        discount_txt = findViewById(R.id.discount_txt);
        delivery_fee_txt = findViewById(R.id.delivery_fee_txt);
        salse_tax_txt = findViewById(R.id.salse_tax_txt);
        total_txt = findViewById(R.id.total_txt);
        order_infomation_list = findViewById(R.id.order_infomation_list);
        grand_total_txt = findViewById(R.id.grand_total_txt);
        order_accept_btn = findViewById(R.id.order_accept_btn);
        order_reject_btn = findViewById(R.id.order_reject_btn);
        update_status_btn = findViewById(R.id.update_status_txt);
        accept_reject_layout = findViewById(R.id.accept_reject_layout);
        order_status_layout = findViewById(R.id.order_status_layout);
        back_relative = findViewById(R.id.back_relative);
        toolbar_title = findViewById(R.id.toolbar_title);
        order_status_txt = findViewById(R.id.order_status_txt);
        down_arrow = findViewById(R.id.down_arrow);
        selecte_driver_txt = findViewById(R.id.selecte_driver_txt);
        selecte_driver_txt = findViewById(R.id.selecte_driver_txt);
        assign_order_layout = findViewById(R.id.assign_order_layout);
        tv_note = findViewById(R.id.tv_note);
        toolbar_title.setText(getResources().getString(R.string.order_detail));
        orderDetais();

        selecte_driver_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (driver_accept_status.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Driver Accept Order", Toast.LENGTH_LONG).show();
                } else {
                    VendorDriverList();
                }
            }
        });

        order_accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderAccept("4");
            }
        });

        order_reject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //     Reject_popupresion();
                OrderAccept("6");

            }
        });


        update_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("update_status_btn", update_status_btn.getText().toString());

                if (update_status_btn.getText().toString().equalsIgnoreCase(getResources().getString(R.string.confirm_order))) {
                    UpdateOrderStatus();
                } else if (update_status_btn.getText().toString().equalsIgnoreCase(getResources().getString(R.string.preparing_order))) {
                    UpdateOrderStatus();
                } else if (update_status_btn.getText().toString().equalsIgnoreCase(getResources().getString(R.string.Order_acceped_by_driver))) {
                    UpdateOrderStatus();
                }
            }
        });


        print_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderStatusActivity.this, BillPrintWebView.class).putExtra("url", invoice_url));
            }
        });

        back_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new OrderHistoryFragment();
                finish();
            }
        });


    }

    private void DriverList(ArrayList<DriverListModel> driverListModelArrayList, ArrayList<DriverListModel> driverListModelArrayList1) {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.driver_list_layout, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        Resion_ext = layout.findViewById(R.id.Forgot_pass_ext);
        RecyclerView driver_list_RV = layout.findViewById(R.id.driver_list_RV);
        RecyclerView foodforme_driver_list_RV = layout.findViewById(R.id.foodforme_driver_list_RV);

        driver_list_RV.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        driver_list_RV.setLayoutManager(layoutManager);
        mDriverListAdapter = new DriverListAdapter(alertDialog, mContext, driverListModelArrayList, Customer_id, getIntent().getStringExtra("order_id"));
        driver_list_RV.setAdapter(mDriverListAdapter);
        mDriverListAdapter.notifyDataSetChanged();

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        foodforme_driver_list_RV.setLayoutManager(layoutManager1);
        mDriverListAdapterFoodForMe = new DriverListAdapter(alertDialog, mContext, driverListModelArrayList1, Customer_id, getIntent().getStringExtra("order_id"));
        foodforme_driver_list_RV.setAdapter(mDriverListAdapterFoodForMe);
        mDriverListAdapterFoodForMe.notifyDataSetChanged();


        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
    }


    private void Reject_popupresion() {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.reject_reson, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

        Resion_ext = layout.findViewById(R.id.Forgot_pass_ext);
        CustomTextView submitbtn = layout.findViewById(R.id.submit_btn);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Resion_ext.getText().toString().trim().isEmpty()) {
                    Resion_ext.requestFocus();
                    Resion_ext.setError(getResources().getString(R.string.enter_reason));
                    //Utility.ShowSnakebarMessage(order_status_layout, "Please entesr the reason for rejecting order");
                } else {
                    OrderReject("6", Resion_ext.getText().toString().trim());
                }
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

    }

    private void UpdateOrderStatus() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.update_order_status, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);

        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        new_order = layout.findViewById(R.id.new_order);
        order_conf = layout.findViewById(R.id.order_conf);
        order_ready = layout.findViewById(R.id.order_ready);
        order_ready_unselect = layout.findViewById(R.id.order_ready_unselect);
        order_ready_select = layout.findViewById(R.id.order_ready_select);
        order_conf_select = layout.findViewById(R.id.order_conf_select);
        order_conf_unselect = layout.findViewById(R.id.order_conf_unselect);

        RelativeLayout preparing_order_layout = layout.findViewById(R.id.preparing_order_layout);
        ImageView preparing_order_unselect = layout.findViewById(R.id.preparing_order_unselect);
        ImageView preparing_order_select = layout.findViewById(R.id.preparing_order_select);

        if (OrderStatus.equalsIgnoreCase("12")) {
            order_conf_select.setVisibility(View.GONE);
            order_conf_unselect.setVisibility(View.VISIBLE);
            preparing_order_unselect.setVisibility(View.GONE);
            preparing_order_select.setVisibility(View.VISIBLE);

        }

        order_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                order_conf_select.setVisibility(View.GONE);
                order_ready_unselect.setVisibility(View.GONE);
                order_conf_unselect.setVisibility(View.VISIBLE);
                order_ready_select.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
                OrderAccept("5");

            }
        });

        preparing_order_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                order_conf_select.setVisibility(View.GONE);
                order_ready_unselect.setVisibility(View.GONE);
                order_conf_unselect.setVisibility(View.VISIBLE);
                order_ready_select.setVisibility(View.VISIBLE);
                alertDialog.dismiss();

                if (OrderStatus.equalsIgnoreCase("8")) {
                    OrderAccept("12");
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.order_not_accept_driver), Toast.LENGTH_LONG).show();
                }


            }
        });


        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
    }


    private void orderDetais() {
        try {
            jObject.put("orderid", getIntent().getStringExtra("order_id"));
            jObject.put("lang_id", preferences.getString("lang", ""));
            jObject.put("vendor_id", Utility.getSharedPreferences(mContext, Constant.vendor_id));
            Log.e("requestBody 187", Constant.OrderDetailAPI);
            final String requestBody = jObject.toString();
            Log.e("requestBody", requestBody);
            progressDialog.show(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, Constant.OrderDetailAPI,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.show(mContext);
                            Log.e(TAG, "162 : onResponse: " + response);
                            try {
                                jObject = new JSONObject(response);
                                JSONObject jsonObject = jObject.getJSONObject("order_detail");
                                invoice_url = jObject.optString("invoice_url");
                                delivery_manage_value = jObject.optString("delivery_manage_value");
                                Gson gson = new GsonBuilder().create();

                                JsonParser jsonParser = new JsonParser();
                                JsonObject jsonResp = jsonParser.parse(response).getAsJsonObject();
                                Example mGetCartDetails = gson.fromJson(jsonResp, Example.class);

                                OrderDetail mOrderDetail = mGetCartDetails.getOrderDetail();
                                driver_accept_status = jsonObject.optString("driver_accept");
                                Driver_name = jsonObject.optString("driver_name");
                                driver_id = jsonObject.optString("driver_id");
                                Log.e("rest_name", jsonObject.optString("rest_name"));
                                Log.e("Driver_name>>", Driver_name);
                                if (!Driver_name.equalsIgnoreCase("")) {
                                    selecte_driver_txt.setText(Driver_name);
                                } else {
                                    selecte_driver_txt.setText("Select Driver");
                                }

                                restrorent_name.setText(jsonObject.optString("rest_name"));
                                //    order_delivery_time.setText("Delivery Time "+jsonObject.optString("order_create"));
                                order_id_txt.setText("# " + jsonObject.optString("order_uniqueid"));
                                payment_txt.setText(getResources().getString(R.string.price) + " " + jsonObject.optString("order_total"));
                                sub_total_txt.setText(getResources().getString(R.string.price) + " " + jsonObject.optString("order_subtotal_amt"));
                                discount_txt.setText(getResources().getString(R.string.price) + " " + "0.00");
                                delivery_fee_txt.setText(getResources().getString(R.string.price) + " " + jsonObject.optString("order_deliveryfee"));
                                salse_tax_txt.setText(getResources().getString(R.string.price) + " " + jsonObject.optString("order_service_tax"));
                                total_txt.setText(getResources().getString(R.string.price) + " " + jsonObject.optString("order_total"));
                                grand_total_txt.setText(getResources().getString(R.string.price) + " " + jsonObject.optString("order_total"));
                                tv_note.setText( jsonObject.optString("order_instruction"));
                                String Order_create_date = jsonObject.optString("order_create");
                                Customer_id = jsonObject.optString("user_id");

                                SimpleDateFormat mInputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                                SimpleDateFormat mOutputDateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a", java.util.Locale.getDefault());
                                try {
                                    mParsedDate = mInputDateFormat.parse(Order_create_date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                mOutputDateString = mOutputDateFormat.format(mParsedDate);
                                //      Log.e("new date is", mOutputDateString);
                                order_delivery_time.setText(getResources().getString(R.string.Delivery_Time) + " " + mOutputDateString);
                                OrderStatus = jsonObject.optString("order_status");
                                String test = jsonObject.optString("order_status");
                                switch (test) {
                                    case "1":

                                        order_status_txt.setText(getResources().getString(R.string.update_order_status));
                                        update_status_btn.setText(getResources().getString(R.string.neworder));
                                        down_arrow.setVisibility(View.VISIBLE);
                                        accept_reject_layout.setVisibility(View.VISIBLE);
                                        print_image.setVisibility(View.GONE);
                                        break;

                                    case "4":
                                        if (delivery_manage_value.equals("Admin Assign")) {
                                            assign_order_layout.setVisibility(View.VISIBLE);
                                        } else {
                                            assign_order_layout.setVisibility(View.GONE);
                                        }
                                        order_status_txt.setText(getResources().getString(R.string.update_order_status));
                                        update_status_btn.setText(getResources().getString(R.string.confirm_order));
                                        down_arrow.setVisibility(View.VISIBLE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);
                                        break;

                                    case "5":

                                      /*  if (delivery_manage_value.equals("Admin Assign")) {
                                            assign_order_layout.setVisibility(View.VISIBLE);
                                        }else {
                                            assign_order_layout.setVisibility(View.GONE);
                                        }
*/
                                        order_status_txt.setText(getResources().getString(R.string.order_status));
                                        update_status_btn.setText(getResources().getString(R.string.ready_order));
                                        down_arrow.setVisibility(View.GONE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);
                                        break;

                                    case "12":

                                        order_status_txt.setText(getResources().getString(R.string.preparing_order));
                                        update_status_btn.setText(getResources().getString(R.string.preparing_order));

                                        down_arrow.setVisibility(View.VISIBLE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);
                                        break;

                                    case "7":

                                        order_status_txt.setText(getResources().getString(R.string.order_status));
                                        update_status_btn.setText(getResources().getString(R.string.ready_order));
                                        down_arrow.setVisibility(View.GONE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);
                                        break;


                                    case "8":
                                        order_status_txt.setText(getResources().getString(R.string.order_status));
                                        update_status_btn.setText(getResources().getString(R.string.Order_acceped_by_driver));


                                        down_arrow.setVisibility(View.VISIBLE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);

                                     /*
                                        down_arrow.setVisibility(View.GONE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);*/
                                        break;


                                    case "9":
                                        order_status_txt.setText(getResources().getString(R.string.order_status));
                                        update_status_btn.setText(getResources().getString(R.string.Order_picked_by_driver));
                                        down_arrow.setVisibility(View.GONE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);
                                        break;


                                    case "10":
                                        order_status_txt.setText(getResources().getString(R.string.order_status));
                                        update_status_btn.setText(getResources().getString(R.string.Order_On_the_way));
                                        down_arrow.setVisibility(View.GONE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);
                                        break;

                                    case "6":
                                        order_status_txt.setText(getResources().getString(R.string.order_status));
                                        update_status_btn.setText(getResources().getString(R.string.Order_Cancel));
                                        down_arrow.setVisibility(View.GONE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);
                                        break;


                                    case "11":
                                        order_status_txt.setText(getResources().getString(R.string.order_status));
                                        update_status_btn.setText(getResources().getString(R.string.Order_Delivered));
                                        down_arrow.setVisibility(View.GONE);
                                        accept_reject_layout.setVisibility(View.GONE);
                                        print_image.setVisibility(View.VISIBLE);
                                        break;

                                }
                                List<OrderCarditem> orderCarditem = mGetCartDetails.getOrderDetail().getOrderCarditem();

                                order_infomation_list.setHasFixedSize(true);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                                order_infomation_list.setLayoutManager(layoutManager);
                                orderInformationAdapter = new OrderInformationAdapter(mContext, orderCarditem);
                                order_infomation_list.setAdapter(orderInformationAdapter);
                                orderInformationAdapter.notifyDataSetChanged();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 3000);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "200 : onErrorResponse: " + error.getMessage());

                            progressDialog.dismiss();

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
            // add the request object to the queue to be executed
            ApplicationController.getInstance().addToRequestQueue(sr);
            sr.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT_MIN,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }


    private void OrderAccept(final String status) {
        jObject = new JSONObject();
        try {
            jObject.put("vendor_id", Utility.getSharedPreferences(mContext, Constant.vendor_id));
            jObject.put("userid", Customer_id);
            jObject.put("orderid", getIntent().getStringExtra("order_id"));
            jObject.put("status", status);
            jObject.put("resion", "");
            jObject.put("lang_id", preferences.getString("lang", ""));
            final String requestBody = jObject.toString();
            Log.e(TAG, "jObject: " + requestBody);
            Log.e(TAG, "jObject: " + Constant.OrderAcceptReject);
            progressDialog.show(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, Constant.OrderAcceptReject,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.show(mContext);
                            Log.e(TAG, "onResponse: " + response);
                            try {
                                jObject = new JSONObject(response);
                                String message = jObject.optString("message");
                                message = message.replace("!", "");
                                update_status_btn.setText(message);
                                accept_reject_layout.setVisibility(View.GONE);
                                print_image.setVisibility(View.VISIBLE);
                                order_status_txt.setText(getResources().getString(R.string.update_order_status));
                                down_arrow.setVisibility(View.GONE);


                                orderDetais();

                               /* if (status.equals("5"))
                                {
                                    onBackPressed();
                                    Utility.setSharedPreference(mContext, Constant.Order, getResources().getString(R.string.confirm_order));
                                }else {

                                    Utility.setSharedPreference(mContext, Constant.Order, getResources().getString(R.string.neworder));
                                }*/


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "200 : onErrorResponse: " + error.getMessage());

                            progressDialog.dismiss();

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
            // add the request object to the queue to be executed
            ApplicationController.getInstance().addToRequestQueue(sr);
            sr.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT_MIN,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }


    private void OrderReject(String status, String reason) {
        jObject = new JSONObject();
        try {
            jObject.put("vendor_id", Utility.getSharedPreferences(mContext, Constant.vendor_id));
            jObject.put("userid", Customer_id);
            jObject.put("orderid", getIntent().getStringExtra("order_id"));
            jObject.put("status", status);
            jObject.put("resion", reason);
            jObject.put("lang_id", preferences.getString("lang", ""));
            final String requestBody = jObject.toString();

            Log.e(TAG, "jObject: " + jObject);
            Log.e(TAG, "API>>>>: " + Constant.OrderAcceptReject);
            progressDialog.show(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, Constant.OrderAcceptReject,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.show(mContext);
                            Log.e(TAG, "OrderAcceptReject Response: " + response);
                            try {
                                jObject = new JSONObject(response);
                                String message = jObject.optString("message");
                                String ok = jObject.optString("ok");
                                if (ok.equalsIgnoreCase("1")) {
                                    Utility.setSharedPreference(mContext, Constant.Order, getResources().getString(R.string.neworder));
                                    Intent i = new Intent(OrderStatusActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                                //  onBackPressed();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "200 : onErrorResponse: " + error.getMessage());

                            progressDialog.dismiss();

                        }
                    }) {
                /*   @Override
                   public String getBodyContentType() {
                       return String.format("application/json; charset=utf-8");
                   }
   */
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
                }
            };
            // add the request object to the queue to be executed
            ApplicationController.getInstance().addToRequestQueue(sr);
            sr.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT_MIN,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }


   /* public void showProgressDialog(Context context, String message) {
        if (progressDialog == null)
            progressDialog = new TransparentProgressDialog(context, context.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }*/

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void VendorDriverList() {

        driverListModelArrayList.clear();
        driverListModelArrayList = new ArrayList<>();

        jObject = new JSONObject();
        try {
            jObject.put("vendor_id", Utility.getSharedPreferences(mContext, Constant.vendor_id));
            jObject.put("lang_id", preferences.getString("lang", ""));
            final String requestBody = jObject.toString();
            Log.e(TAG, "jObject: " + requestBody);
            Log.e(TAG, "jObject: " + Constant.vendordriverlist);
            progressDialog.show(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, Constant.vendordriverlist,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.show(mContext);
                            Log.e(TAG, "onResponse: " + response);
                            try {

                                jObject = new JSONObject(response);
                                JSONArray jsonArray = jObject.getJSONArray("driver_list");
                                JSONArray jsonArray1 = jObject.getJSONArray("ffmdriver_list");
                                Log.e(TAG, "jsonObject>>>: " + jsonArray);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    DriverListModel driverListModel = new DriverListModel();
                                    driverListModel.setId(jsonObject1.optString("id"));
                                    driverListModel.setName(jsonObject1.optString("name"));
                                    driverListModel.setLname(jsonObject1.optString("lname"));
                                    driverListModel.setEmail(jsonObject1.optString("email"));
                                    driverListModel.setUserMob(jsonObject1.optString("user_mob"));


                                    driverListModelArrayList.add(driverListModel);

                                }

                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                    DriverListModel driverListModel = new DriverListModel();
                                    driverListModel.setId(jsonObject1.optString("id"));
                                    driverListModel.setName(jsonObject1.optString("name"));
                                    driverListModel.setLname(jsonObject1.optString("lname"));
                                    driverListModel.setEmail(jsonObject1.optString("email"));
                                    driverListModel.setUserMob(jsonObject1.optString("user_mob"));


                                    driverListModelArrayList1.add(driverListModel);

                                }
                                DriverList(driverListModelArrayList, driverListModelArrayList1);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "200 : onErrorResponse: " + error.getMessage());

                            progressDialog.dismiss();

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
            // add the request object to the queue to be executed
            ApplicationController.getInstance().addToRequestQueue(sr);
            sr.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT_MIN,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    public void callAgainMethod() {
        orderDetais();
    }
}

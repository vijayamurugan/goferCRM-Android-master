package com.gofercrm.user.gofercrm.profile;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gofercrm.user.gofercrm.Constants;
import com.gofercrm.user.gofercrm.R;
import com.gofercrm.user.gofercrm.chat.ui.main.ChatData;
import com.gofercrm.user.gofercrm.chat.ui.main.Conversation;
import com.gofercrm.user.gofercrm.chat.ui.main.MessageData;
import com.gofercrm.user.gofercrm.clients.ClientGeneralAdapter;
import com.gofercrm.user.gofercrm.clients.ClientProcessActivity;
import com.gofercrm.user.gofercrm.clients.ClientView;
import com.gofercrm.user.gofercrm.clients.ClientViewPagerAdapter;
import com.gofercrm.user.gofercrm.entity.Address;
import com.gofercrm.user.gofercrm.entity.Email;
import com.gofercrm.user.gofercrm.entity.Phone;
import com.gofercrm.user.gofercrm.entity.User;
import com.gofercrm.user.gofercrm.util.FileUtil;
import com.gofercrm.user.gofercrm.util.Util;
import com.gofercrm.user.gofercrm.volleynetworkutil.NetworkManager;
import com.gofercrm.user.gofercrm.volleynetworkutil.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProfileActivity extends AppCompatActivity {

    private String name, image, USER_ID;

    private RecyclerView recyclerView;
    private ImageView backdrop;
    private ClientGeneralAdapter cGAdapter;

    List<ClientView> userPage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.profile_view_recycle);
        cGAdapter = new ClientGeneralAdapter(userPage, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cGAdapter);

        Intent intent = getIntent();
        USER_ID = intent.getStringExtra("USER_ID");
        image = intent.getStringExtra("IMAGE");
        name = intent.getStringExtra("NAME");

        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backdrop = findViewById(R.id.backdrop);
        if (image != null) {
            RequestOptions options = new RequestOptions();
            Glide.with(getApplicationContext()).load(image).apply(options).into(backdrop);
        }


        if (USER_ID != null) {
            getUserDetails(USER_ID);
        }

        FloatingActionButton fab = findViewById(R.id.profile_message);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Conversation.class);
                intent.putExtra("_ID", USER_ID);
                intent.putExtra("_TYPE", (Serializable) 1);
                intent.putExtra("_NAME", name);
                intent.putExtra("_IMAGE", image);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void onLoaded() {
        cGAdapter = new ClientGeneralAdapter(userPage, getApplicationContext());
        recyclerView = findViewById(R.id.profile_view_recycle);
        cGAdapter = new ClientGeneralAdapter(userPage, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cGAdapter);
        cGAdapter.notifyDataSetChanged();

    }

    private void getUserDetails(String _Id) {

        String url = Constants.GET_USER_DATA + "?user_ids=" + _Id;
        NetworkManager.customJsonObjectRequest(
                getApplicationContext(), url, null,
                new VolleyResponseListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        processData(response);
                    }

                    @Override
                    public void onError(String message) {
                        System.out.println("Error" + message);
                        Log.d("ERRROR=", message);
                    }

                }, true);

    }


    private void processData(JSONObject response) {
        ClientView cv;
        userPage = new ArrayList<>();
        try {
            if (Integer.parseInt(response.getString("result")) == 1) {

                JSONArray user_array = response.getJSONArray("profiles");
                if (user_array != null && user_array.length() > 0) {
                    JSONObject user = user_array.getJSONObject(0);

                    cv = new ClientView("Name", user.has("name") ? user.getString("name") : "", "", R.drawable.ic_account, 0);
                    userPage.add(cv);

                    cv = new ClientView("First Name", user.has("first_name") ? user.getString("first_name") : "", "", R.drawable.ic_account, 0);
                    userPage.add(cv);
                    cv = new ClientView("Last Name", user.has("last_name") ? user.getString("last_name") : "", "", R.drawable.ic_account, 0);
                    userPage.add(cv);
                    cv = new ClientView("Email", user.has("email") ? user.getString("email") : "", "EMAIL", R.drawable.ic_email, 0);
                    userPage.add(cv);
                    cv = new ClientView("Phone", user.has("phone") ? user.getString("phone") : "", "PHONE", R.drawable.ic_call, R.drawable.ic_message);
                    userPage.add(cv);
                    cv = new ClientView("Company", user.has("company_name") ? user.getString("company_name") : "", "", R.drawable.domain, 0);
                    userPage.add(cv);
                }
                onLoaded();


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
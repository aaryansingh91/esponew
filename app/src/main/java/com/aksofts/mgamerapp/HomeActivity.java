package com.aksofts.mgamerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView, withdrawRecyclerView ;
    List<GameModel> gameList;
    GameAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    ScrollView home_scroll_section, game_scroll_section, reward_scroll_section, profile_scroll_section;
    ImageView icon_home, icon_game, icon_reward, icon_profile;
    TextView text_home, text_game, text_reward, text_profile, username_profile, coinsHeader, ticketsHeader, account_page_coins_text_value, account_page_tickets_text_value, coins_rewards_screen;
    MaterialCardView nav_home, nav_game, nav_reward, nav_profile;
    private MaterialCardView btnLogout; // Changed from Button to MaterialCardView
    MaterialCardView home_sec1_layout_game_tab, home_sec1_layout_apptask_tab, home_sec1_layout_survey_tab;
    String app_home_top_sec_1_game, app_home_top_sec_1_game_url, app_home_top_sec_1_apptask, app_home_top_sec_1_apptask_url, app_home_top_sec_1_survey, app_home_top_sec_1_survey_url;

    MaterialCardView home_sec3_layout_game_tab, home_sec3_layout_ffblog_tab, home_sec3_layout_quiz_tab;
    String app_home_top_sec_3_game_onoff, app_home_top_sec_3_game_url, app_home_top_sec_3_ffblog_onoff, app_home_top_sec_3_ffblog_url, app_home_top_sec_3_quiz_onoff, app_home_top_sec_3_quiz_url;

    String withdraw_list_data_setting;

    HorizontalScrollView top_horizontal ;
    WithdrawSelectionItem withdraw_selection_adapter;
    List<WithdrawSelectionItem> withdraw_selection_ItemList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerViewGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        gameList = new ArrayList<>();
        adapter = new GameAdapter(gameList);
        recyclerView.setAdapter(adapter);

        coinsHeader = findViewById(R.id.coins_header);
        coins_rewards_screen = findViewById(R.id.coins_rewards_screen);
        account_page_coins_text_value = findViewById(R.id.account_page_coins_text_value);
        ticketsHeader = findViewById(R.id.tickets_header);
        account_page_tickets_text_value = findViewById(R.id.account_page_tickets_text_value);

        fetchGames();


        icon_home = findViewById(R.id.icon_home);

        username_profile = findViewById(R.id.username_profile);

        icon_game = findViewById(R.id.icon_game);
        icon_profile = findViewById(R.id.icon_profile);
        icon_reward = findViewById(R.id.icon_reward);
        text_home = findViewById(R.id.text_home);
        text_game = findViewById(R.id.text_game);
        text_profile = findViewById(R.id.text_profile);
        text_reward = findViewById(R.id.text_reward);

        nav_home = findViewById(R.id.nav_home);
        nav_game = findViewById(R.id.nav_game);
        nav_profile = findViewById(R.id.nav_profile);
        nav_reward = findViewById(R.id.nav_reward);

        home_scroll_section = findViewById(R.id.home_scroll_section);
        game_scroll_section = findViewById(R.id.game_scroll_section);
        reward_scroll_section = findViewById(R.id.rewards_scroll_section);
        profile_scroll_section = findViewById(R.id.account_scroll_section);

        home_sec1_layout_game_tab = findViewById(R.id.home_sec1_layout_game_tab);
        home_sec1_layout_apptask_tab = findViewById(R.id.home_sec1_layout_apptask_tab);
        home_sec1_layout_survey_tab = findViewById(R.id.home_sec1_layout_survery_tab);

        home_sec3_layout_game_tab = findViewById(R.id.home_sec3_layout_game_tab);
        home_sec3_layout_ffblog_tab = findViewById(R.id.home_sec3_layout_ffblog_tab);
        home_sec3_layout_quiz_tab = findViewById(R.id.home_sec3_layout_quiz_tab);

        btnLogout = findViewById(R.id.btnLogout); // Initialize as MaterialCardView

        top_horizontal = findViewById(R.id.top_horizontal);
        Button bonusBtn = findViewById(R.id.bonus_get);
//        LinearLayout bonusPopup = findViewById(R.id.bonus_popup);
//        TextView popupText = findViewById(R.id.popup_text);

        // Storing Into Shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);

        String storedID = sharedPreferences.getString("userID", "NULL");
        String userName = sharedPreferences.getString("userName", "NULL");
        username_profile.setText(userName);
        get_user_data_thread(storedID);


        String lastClaimDate = sharedPreferences.getString("lastClaimDate", null);
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (todayDate.equals(lastClaimDate)) {
            bonusBtn.setText("CLAIMED");
            bonusBtn.setEnabled(false);
        }
        top_horizontal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        swipeRefreshLayout.setEnabled(false); // disable while touching (scrolling)
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeRefreshLayout.setEnabled(true); // enable again when scrolling ends
                        break;
                }
                return false;  // allow normal scrolling
            }
        });



        int userId = Integer.parseInt(storedID); // pass the actual user ID here
        fetchUserData(userId);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simple activity refresh
//            finish();
            fetchGames();
            get_user_data_thread(storedID);
            fetchUserData(userId);
//            overridePendingTransition(0, 0);
//            startActivity(getIntent());
            swipeRefreshLayout.setRefreshing(false);

            overridePendingTransition(0, 0);
        });
        bonusBtn.setOnClickListener(v -> {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.app_url) +"/amsit-adm/get_daily_bonus.php",
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                            View popupView = inflater.inflate(R.layout.popup_bonus, null);

                            if (status.equals("success")) {
                                int amount = obj.getInt("amount");
                                // Now initialize popupText from popupView
                                TextView popupText = popupView.findViewById(R.id.popup_text);
                                popupText.setText("+" + amount + " Coins Credited!");
//                                bonusPopup.setVisibility(View.VISIBLE);
                                SharedPreferences prefs = getSharedPreferences("pgamerapp", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();

                            // Save today's date as string (e.g., 2025-06-02)
                                String claimDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                editor.putString("lastClaimDate", claimDate);
                                editor.apply();

                            // Disable button
                                bonusBtn.setText("CLAIMED");
                                bonusBtn.setEnabled(false);



                        // Create rounded background programmatically
                                GradientDrawable background = new GradientDrawable();
                                background.setColor(Color.WHITE); // Background color
                                background.setCornerRadius(30f);  // Radius in pixels

                                popupView.setBackground(background);

                                AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                                        .setView(popupView)
                                        .setCancelable(false)
                                        .create();

                            // Make background transparent to see rounded corners
                                if (dialog.getWindow() != null) {
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                }

                                dialog.show();

                            // Auto-dismiss after 3 seconds
                                new Handler().postDelayed(dialog::dismiss, 3000);


                            } else if (status.equals("already_claimed")) {
                                Toast.makeText(this, "Bonus already claimed today", Toast.LENGTH_SHORT).show();
                                SharedPreferences prefs = getSharedPreferences("pgamerapp", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();

                                // Save today's date as string (e.g., 2025-06-02)
                                String claimDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                editor.putString("lastClaimDate", claimDate);
                                editor.apply();

                                // Disable button
                                bonusBtn.setText("CLAIMED");
                                bonusBtn.setEnabled(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", storedID);
                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        });



        app_home_top_sec_1_game = sharedPreferences.getString("app_home_top_sec_1_game", "NULL");
        app_home_top_sec_1_game_url = sharedPreferences.getString("app_home_top_sec_1_game_url", "NULL");

        app_home_top_sec_1_apptask = sharedPreferences.getString("app_home_top_sec_1_apptask", "NULL");
        app_home_top_sec_1_apptask_url = sharedPreferences.getString("app_home_top_sec_1_apptask_url", "NULL");

        app_home_top_sec_1_survey = sharedPreferences.getString("app_home_top_sec_1_survey", "NULL");
        app_home_top_sec_1_survey_url = sharedPreferences.getString("app_home_top_sec_1_survey_url", "NULL");

        app_home_top_sec_3_game_onoff = sharedPreferences.getString("app_home_top_sec_3_game_onoff", "NULL");
        app_home_top_sec_3_game_url = sharedPreferences.getString("app_home_top_sec_3_game_url", "NULL");

        app_home_top_sec_3_ffblog_onoff = sharedPreferences.getString("app_home_top_sec_3_ffblog_onoff", "NULL");
        app_home_top_sec_3_ffblog_url = sharedPreferences.getString("app_home_top_sec_3_ffblog_url", "NULL");

        app_home_top_sec_3_quiz_onoff = sharedPreferences.getString("app_home_top_sec_3_quiz_onoff", "NULL");
        app_home_top_sec_3_quiz_url = sharedPreferences.getString("app_home_top_sec_3_quiz_url", "NULL");

        // Withdraw LIST ENABLE DISABLE SETTINGS and ICONS setting
        withdraw_list_data_setting = sharedPreferences.getString("withdraw_list_data_setting", "NULL");

        withdrawRecyclerView  = findViewById(R.id.withdraw_selctionlist_recyclerView);
        withdrawRecyclerView .setLayoutManager(new LinearLayoutManager(this));
        withdraw_selection_ItemList = new ArrayList<>();

        String jsonData = withdraw_list_data_setting;
        if (!jsonData.equals("NULL")) {
            try {
                JSONArray jsonArray = new JSONArray(jsonData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    WithdrawSelectionItem item = new WithdrawSelectionItem(
                            object.getString("id"),
                            object.getString("abbrevation"),
                            object.getString("title"),
                            object.getString("description"),
                            object.getString("img_icon")
                    );

                    withdraw_selection_ItemList.add(item);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        WithdrawSelectionAdapter adapter = new WithdrawSelectionAdapter(withdraw_selection_ItemList, item -> {
            Toast.makeText(HomeActivity.this, "Opening", Toast.LENGTH_SHORT).show();
            startWithdrawListActivity(item.getAbbrevation(), item.getTitle());
        });
        withdrawRecyclerView .setAdapter(adapter);

        if (app_home_top_sec_1_game.toLowerCase().equals("off")) {
            home_sec1_layout_game_tab.setVisibility(View.GONE);
        }
        if (app_home_top_sec_1_apptask.toLowerCase().equals("off")) {
            home_sec1_layout_apptask_tab.setVisibility(View.GONE);
        }
        if (app_home_top_sec_1_survey.toLowerCase().equals("off")) {
            home_sec1_layout_survey_tab.setVisibility(View.GONE);
        }

        if (app_home_top_sec_3_game_onoff.toLowerCase().equals("off")) {
            home_sec3_layout_game_tab.setVisibility(View.GONE);
        }
        if (app_home_top_sec_3_ffblog_onoff.toLowerCase().equals("off")) {
            home_sec3_layout_ffblog_tab.setVisibility(View.GONE);
        }
        if (app_home_top_sec_3_quiz_onoff.toLowerCase().equals("off")) {
            home_sec3_layout_quiz_tab.setVisibility(View.GONE);
        }

        showBottomSheetDialog();

    }



    private void fetchUserData(int userId) {
        String url = getString(R.string.app_url) + "/amsit-adm/get_user_info_api.php?id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject user = jsonObject.getJSONObject("user");

                            int coins = user.getInt("coins");
                            int tickets = user.getInt("tickets");

                            // Save to SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("coins", coins);
                            editor.putInt("tickets", tickets);
                            editor.apply();

                            // Now read from SharedPreferences and set TextViews
                            int savedCoins = sharedPreferences.getInt("coins", 0);
                            int savedTickets = sharedPreferences.getInt("tickets", 0);

                            coinsHeader.setText(String.valueOf(savedCoins));
                            account_page_coins_text_value.setText(String.valueOf(savedCoins));
                            coins_rewards_screen.setText(String.valueOf(savedCoins));

                            ticketsHeader.setText(String.valueOf(savedTickets));
                            account_page_tickets_text_value.setText(String.valueOf(savedTickets));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();

                    // On error, load last saved values from SharedPreferences anyway
                    SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
                    int savedCoins = sharedPreferences.getInt("coins", 0);
                    int savedTickets = sharedPreferences.getInt("tickets", 0);

                    coinsHeader.setText(String.valueOf(savedCoins));
                    account_page_coins_text_value.setText(String.valueOf(savedCoins));
                    coins_rewards_screen.setText(String.valueOf(savedCoins));

                    ticketsHeader.setText(String.valueOf(savedTickets));
                    account_page_tickets_text_value.setText(String.valueOf(savedTickets));
                });

        queue.add(stringRequest);
    }


    // Logout method for the MaterialCardView onClick
    public void logoutUser(View view) {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Show toast message
        Toast.makeText(this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();

        // Redirect to OnboardingDisclosureActivity
        Intent intent = new Intent(this, OnboardingDisclosureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Called when the LinearLayout is clicked
    public void openGameOfferwall(View view) {
        Intent intent = new Intent(this, GameOfferwall.class);
        startActivity(intent);
    }

    private void startWithdrawListActivity(String category, String name) {
        Intent intent = new Intent(this, WithdrawListActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    private void showBottomSheetDialog() {
        // BottomSheetDialog banaen
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        // Layout inflate karen
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // BottomSheetData object banaen
        BottomSheetData data = new BottomSheetData(
                "Welcome to pGamer!",
                "Earn Coins",
                "Earn more coins 💰 by playing games and filling surveys",
                "Redeem easily with our rewards",
                "Earn Coins"
        );

        // Bottom sheet ke andar ke views ko find karen aur data set karen
        TextView titleTextView = bottomSheetView.findViewById(R.id.bottom_sheet_title);
        titleTextView.setText(data.getTitle());

        TextView noticeTitleTextView = bottomSheetView.findViewById(R.id.bottom_sheet_notice_title);
        noticeTitleTextView.setText(data.getNoticeTitle());

        TextView notice1TextView = bottomSheetView.findViewById(R.id.bottom_sheet_notice_1);
        notice1TextView.setText(data.getNotice1());

        TextView notice2TextView = bottomSheetView.findViewById(R.id.bottom_sheet_notice_2);
        notice2TextView.setText(data.getNotice2());

//        Button confirmButton = bottomSheetView.findViewById(R.id.bottom_sheet_confirm_button);
//        confirmButton.setText(data.getConfirmButtonText());
//
//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheetDialog.dismiss();
//            }
//        });

        // Bottom sheet ko dikhaen
        bottomSheetDialog.show();
    }

    private void resetAllNavCards() {
        home_scroll_section.setVisibility(View.GONE);
        game_scroll_section.setVisibility(View.GONE);
        reward_scroll_section.setVisibility(View.GONE);
        profile_scroll_section.setVisibility(View.GONE);

        nav_home.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        nav_game.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        nav_reward.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        nav_profile.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));

        text_home.setTextColor(getResources().getColor(R.color.bottomnavbar_unselected_color));
        text_game.setTextColor(getResources().getColor(R.color.bottomnavbar_unselected_color));
        text_reward.setTextColor(getResources().getColor(R.color.bottomnavbar_unselected_color));
        text_profile.setTextColor(getResources().getColor(R.color.bottomnavbar_unselected_color));

        icon_home.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home));
        icon_game.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_game));
        icon_reward.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_reward));
        icon_profile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_profile));
    }

    public void bottom_nav_homebtn(View view) {
        resetAllNavCards();
        nav_home.setCardBackgroundColor(getResources().getColor(R.color.bottomnavbar_activecard_bg_color));
        text_home.setTextColor(getResources().getColor(R.color.bottomnavbar_selected_color));
        home_scroll_section.setVisibility(View.VISIBLE);
    }

    public void bottom_nav_gamebtn(View view) {
        resetAllNavCards();
        nav_game.setCardBackgroundColor(getResources().getColor(R.color.bottomnavbar_activecard_bg_color));
        text_game.setTextColor(getResources().getColor(R.color.bottomnavbar_selected_color));
        icon_game.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_game_selected));
        game_scroll_section.setVisibility(View.VISIBLE);
    }

    public void bottom_nav_rewardbtn(View view) {
        resetAllNavCards();
        nav_reward.setCardBackgroundColor(getResources().getColor(R.color.bottomnavbar_activecard_bg_color));
        text_reward.setTextColor(getResources().getColor(R.color.bottomnavbar_selected_color));
        icon_reward.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_reward_selected));
        reward_scroll_section.setVisibility(View.VISIBLE);
    }

    public void bottom_nav_profilebtn(View view) {
        resetAllNavCards();
        nav_profile.setCardBackgroundColor(getResources().getColor(R.color.bottomnavbar_activecard_bg_color));
        text_profile.setTextColor(getResources().getColor(R.color.bottomnavbar_selected_color));
        icon_profile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_profile_selected));
        profile_scroll_section.setVisibility(View.VISIBLE);
    }

    public void goto_rewards_history(View view) {
        startActivity(new Intent(HomeActivity.this, MyRewardActivity.class));

    }
    public void goto_account_history(View view) {
        startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
    }

    public void invite_others_fn(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        String owncode = sharedPreferences.getString("owncode", "pGamer");
        String app_share_message_before_refercode = sharedPreferences.getString("app_share_message_before_refercode", "");
        String app_share_message_refercode_link = sharedPreferences.getString("app_share_message_refercode_link", "");
        String app_share_message_after_refercode = sharedPreferences.getString("app_share_message_after_refercode", "");

        String final_string_refer_link = app_share_message_refercode_link + owncode;
        String message = app_share_message_before_refercode + " " + owncode + "  and use link to download app : " + final_string_refer_link + app_share_message_after_refercode;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(sendIntent, "Share " + sharedPreferences.getString("app_name", "")));
    }

    public void get_user_data_thread(String user_id) {
        String get_user_data_qry = getResources().getString(R.string.app_url) + "/app-apis/user/get_view_homescrdata.php?";
        String datatohash = "";
        try {
            datatohash = "i=" + URLEncoder.encode(user_id, "UTF-8");
            String token = temp.sha256_temp(datatohash);
            get_user_data_qry = get_user_data_qry + datatohash + "&token=" + token;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalget_user_data_qry = get_user_data_qry;
        class dbprocess extends AsyncTask<String, Void, String> implements com.aksofts.mgamerapp.dbprocess {
            @Override
            protected void onPostExecute(String data) {
                if (data.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Something went Wrong ! - Contact Support Now", Toast.LENGTH_SHORT).show();
                } else if (data.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Something went Wrong ! - Contact Support Now", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        // Create a JSONObject from the JSON response string
                        JSONObject jsonObject = new JSONObject(data);
                        String status = jsonObject.getString("status");
                        String email = jsonObject.getString("email");
                        String owncode = jsonObject.getString("owncode");
                        String balance = jsonObject.getString("balance");
                        String kyc = jsonObject.getString("kyc");
                        String name = jsonObject.getString("name");

                        // Storing Into Shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("status", status);
                        myEdit.putString("email", email);
                        myEdit.putString("owncode", owncode);
                        myEdit.putString("balance", balance);
                        myEdit.putString("kyc", kyc);
                        myEdit.putString("name", name);
                        myEdit.apply();

                        // Calculating Update Versions and Maintenance here
                        if (status.equals("Blocked") || status.equals("0")) {
                            Toast.makeText(getApplicationContext(), "Your Account has been Blocked - Contact Our Support team For More Info", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String furl = params[0];
                try {
                    URL url = new URL(furl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    return br.readLine();
                } catch (Exception ex) {
                    return ex.getMessage();
                }
            }
        }

        dbprocess obj = new dbprocess();
        obj.execute(finalget_user_data_qry);
    }

    public void btn_fn_sec1_playgame(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_1_game_url));
        startActivity(intent);
    }

    public void btn_fn_sec1_apptask(View view) {
        TaskOffersBottomSheet bottomSheet = new TaskOffersBottomSheet();
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }


    public void btn_fn_sec1_survey(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_1_survey_url));
        startActivity(intent);
    }

    public void btn_fn_sec2_luckydraw(View view) {
        Intent intent = new Intent(this, LuckyDrawActivity.class);
        startActivity(intent);
    }

    public void btn_fn_sec2_luckynumber(View view) {
        Intent intent = new Intent(this, LuckyNumber.class);
        startActivity(intent);
    }

    public void btn_fn_sec3_playgame(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_3_game_url));
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void btn_fn_sec3_freefireblog(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_3_ffblog_url));
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void btn_fn_sec3_quiz(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_3_quiz_url));
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void settings_terms_conditions(View view) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
            String url = sharedPreferences.getString("app_internal_settings_page_terms_condition_page_link", "NULL");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void settings_privcay_policy(View view) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
            String url = sharedPreferences.getString("app_internal_settings_page_privacy_page_link", "NULL");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void settings_faq(View view) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
            String url = sharedPreferences.getString("app_internal_settings_page_helpandsupport_page_link", "NULL");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void goto_tournaments_activity_ff(View view) {
        try {
            Intent intent = new Intent(this, TournamentListActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public class GameModel {
        public String game_id;
        public String game_name;
        public String package_name;
        public String game_image;
        public String game_rules;
    }

    private void fetchGames() {
        String url = getString(R.string.app_url) +"/amsit-adm/game_list_api.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,

                response -> {
                    try {
                        if (response.getBoolean("status")) {
                            JSONArray dataArray = response.getJSONArray("data");

                            // Clear old data before adding new items
                            gameList.clear();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                GameModel game = new GameModel();
                                game.game_id = obj.getString("game_id");
                                game.game_name = obj.getString("game_name");
                                game.package_name = obj.getString("package_name");
                                game.game_image = obj.getString("game_image");
                                game.game_rules = obj.getString("game_rules");
                                gameList.add(game);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("API_ERROR", error.toString())
        );

        queue.add(request);
    }


    // Adapter Class
    class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
        List<GameModel> games;

        GameAdapter(List<GameModel> games) {
            this.games = games;
        }

        @NonNull
        @Override
        public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.item_game_card, parent, false);
            return new GameViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
            GameModel game = games.get(position);
            holder.title.setText(game.game_name);
            new DownloadImageTask(holder.image).execute(game.game_image);

            // Set click listener on the entire itemView
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(HomeActivity.this, "Clicked: " + game.game_name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, TournamentListActivity.class);
                intent.putExtra("game_id", game.game_id);
                intent.putExtra("game_name", game.game_name);
                intent.putExtra("package_name", game.package_name);
                intent.putExtra("game_image", game.game_image);
                intent.putExtra("game_rules", game.game_rules);
                HomeActivity.this.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return games.size();
        }

        class GameViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;

            GameViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.game_image);
                title = itemView.findViewById(R.id.game_title);
            }
        }
    }

    // Image Downloader
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(urlDisplay);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream in = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }

    }

}

package com.rewards.espotask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.ads.offerwall.OfferwallAdapterBridge;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import com.makeopinion.cpxresearchlib.CPXResearch;
import com.makeopinion.cpxresearchlib.CPXResearchListener;
import com.makeopinion.cpxresearchlib.models.CPXCardConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXCardStyle;
import com.makeopinion.cpxresearchlib.models.SurveyItem;
import com.makeopinion.cpxresearchlib.models.TransactionItem;
import android.graphics.Color;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // Declare this at the top of your class
    private String unityRewardedId = null;
    private Context appContext;
    private int userId; // Declare globally here

    RecyclerView recyclerView, withdrawRecyclerView ;
    List<GameModel> gameList;
    GameAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    ScrollView home_scroll_section, game_scroll_section, reward_scroll_section, profile_scroll_section;
    ImageView icon_home, icon_game, icon_reward, icon_profile;
    TextView welcome_user, text_home, text_game, text_reward, text_profile, username_profile, coinsHeader, ticketsHeader, account_page_coins_text_value, account_page_tickets_text_value, coins_rewards_screen;
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

    Button btnFacebook, btnInstagram, btnTelegram, btnYoutube;
    String facebookUrl, instagramUrl, telegramUrl, youtubeUrl;

    private Switch switchPushNotification;
    private SharedPreferences notificationPrefs;

    private CPXResearch cpxResearch;
    private LinearLayout surveyCardsContainer;


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize CPX Research
        initializeCPXResearch();

        // Setup CPX Survey Cards
        setupCPXSurveyCards();


        // Validate session on app start
        SessionHelper.validateSession(this, new SessionHelper.SessionValidationCallback() {
            @Override
            public void onSessionValid() {
                // Session is valid, continue normally
            }

            @Override
            public void onSessionInvalid(String reason, String message) {
                if (reason.equals("user_blocked")) {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Account Blocked")
                            .setMessage(message)
                            .setPositiveButton("OK", (dialog, which) -> {
                                SessionHelper.handleSessionExpired(HomeActivity.this);
                            })
                            .setCancelable(false)
                            .show();
                } else if (reason.equals("device_blocked")) {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Device Blocked")
                            .setMessage(message)
                            .setPositiveButton("OK", (dialog, which) -> {
                                SessionHelper.handleSessionExpired(HomeActivity.this);
                            })
                            .setCancelable(false)
                            .show();
                } else if (reason.equals("session_expired")) {
                    SessionHelper.handleSessionExpired(HomeActivity.this);
                }
            }
        });

        // Bind Buttons
        btnFacebook = findViewById(R.id.btnFacebook);
        btnInstagram = findViewById(R.id.btnInstagram);
        btnTelegram = findViewById(R.id.btnTelegram);
        btnYoutube = findViewById(R.id.btnYoutube);

        ViewPager2 viewPager2 = findViewById(R.id.imageSlider);

        // Fetch social links
        fetchSocialLinks();


        Button cardTaskOffers = findViewById(R.id.card_task_offers);
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

        welcome_user = findViewById(R.id.welcome_user);

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

        HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScrollView);

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
        // LinearLayout bonusPopup = findViewById(R.id.bonus_popup);
        //TextView popupText = findViewById(R.id.popup_text);


        // Storing Into Shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);

        String storedID = sharedPreferences.getString("userID", "NULL");
        String userName = sharedPreferences.getString("userName", "NULL");
        username_profile.setText(userName);
        welcome_user.setText(userName);
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
        setupNotificationSwitch();

        horizontalScrollView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    swipeRefreshLayout.setEnabled(false);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    swipeRefreshLayout.setEnabled(true);
                    break;
            }
            return false; // allow horizontal scroll to work
        });

        // Sample images (from drawable)
        WormDotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);

        // Sample images (from drawable)
        List<Integer> images = Arrays.asList(
                R.drawable.freefire,
                R.drawable.freefire3,
                R.drawable.freefire2
        );

        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(images);
        viewPager2.setAdapter(sliderAdapter);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Margin transformer for space between pages
        int pageMargin =20; // adjust as needed
        viewPager2.setPageTransformer(new MarginPageTransformer(pageMargin));

        // Connect indicator with ViewPager2
        dotsIndicator.setViewPager2(viewPager2);


        // Optional: Auto-slide
        new Handler().postDelayed(new Runnable() {
            int currentPage = 0;

            @Override
            public void run() {
                if (currentPage == images.size()) {
                    currentPage = 0;
                }
                viewPager2.setCurrentItem(currentPage++, true);
                new Handler().postDelayed(this, 3000); // 3 sec delay
            }
        }, 3000);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    swipeRefreshLayout.setEnabled(false);  // disable swipe refresh while dragging
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    swipeRefreshLayout.setEnabled(true);   // enable swipe refresh after scroll ends
                }
            }
        });

        cardTaskOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskOffersBottomSheet bottomSheet = new TaskOffersBottomSheet();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });


                      userId = Integer.parseInt(storedID); // pass the actual user ID here
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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.app_url) +"/get_daily_bonus.php",
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
                                SharedPreferences prefs = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
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
                                SharedPreferences prefs = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
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

        fetchAndSetupAds(this);
        setupWatchAdButton(this);
    }


    // Add this in your onCreate() method after your existing findViewById calls
    private void setupNotificationSwitch() {
        switchPushNotification = findViewById(R.id.switchPushNotification);
        notificationPrefs = getSharedPreferences("notification_settings", MODE_PRIVATE);

        // Load saved preference
        boolean isEnabled = notificationPrefs.getBoolean("push_notifications_enabled", true);
        switchPushNotification.setChecked(isEnabled);

        // Set switch listener
        switchPushNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleNotificationToggle(isChecked);
        });
    }

    public void openRewardHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }


    // Add this method to handle notification toggle
    private void handleNotificationToggle(boolean isEnabled) {
        // Save preference
        SharedPreferences.Editor editor = notificationPrefs.edit();
        editor.putBoolean("push_notifications_enabled", isEnabled);
        editor.apply();

        if (isEnabled) {
            Toast.makeText(this, "Push notifications enabled", Toast.LENGTH_SHORT).show();
            // Enable notifications logic here if needed
        } else {
            Toast.makeText(this, "Push notifications disabled", Toast.LENGTH_SHORT).show();
            // Disable notifications logic here if needed
        }
    }



    private void fetchUserData(int userId) {
        String url = getString(R.string.app_url) + "/get_user_info_api.php?id=" + userId;

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
                            SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
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
                    SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
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
        // Use SessionHelper to logout
        SessionHelper.logout(this, new SessionHelper.LogoutCallback() {
            @Override
            public void onLogoutComplete(boolean success) {
                Toast.makeText(getApplicationContext(), "Logged Out Successfully!", Toast.LENGTH_SHORT).show();

                // Redirect to OnboardingDisclosureActivity
                Intent intent = new Intent(getApplicationContext(), OnboardingDisclosureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
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
    public void HowtoWinCoin(View view) {
        showBottomSheetDialog();
    }

    public void SurveyClick(View view) {
        Toast.makeText(this, "No Surveys Available Right Now", Toast.LENGTH_SHORT).show();
    }

    private void showBottomSheetDialog() {
        // BottomSheetDialog banaen
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        // Layout inflate karen
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // BottomSheetData object banaen
        BottomSheetData data = new BottomSheetData(
                "Welcome to EspoTask - eSports Rewards",
                "Play & Earn",
                "Earn more coins 💰 by playing PUBG, Free Fire, and other tournaments",
                "Redeem coins for exciting gaming rewards & vouchers",
                "Start Playing"
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

        Button confirmButton = bottomSheetView.findViewById(R.id.bottom_sheet_confirm_button);
        confirmButton.setText(data.getConfirmButtonText());

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

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


    public void openLuckyWinner(View view) {
        Intent intent = new Intent(this, GameOfferwall.class);
        startActivity(intent);
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

    public void openProfileSettings(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


    public void invite_others_fn(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
        String owncode = sharedPreferences.getString("owncode", "EspoTask");
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
        String get_user_data_qry = getResources().getString(R.string.app_url) + "/user/get_view_homescrdata.php?";
        String datatohash = "";
        try {
            datatohash = "i=" + URLEncoder.encode(user_id, "UTF-8");
            String token = temp.sha256_temp(datatohash);
            get_user_data_qry = get_user_data_qry + datatohash + "&token=" + token;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalget_user_data_qry = get_user_data_qry;
        class dbprocess extends AsyncTask<String, Void, String> implements com.rewards.espotask.dbprocess {
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
                        SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
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

    public void openProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


    public void btn_fn_sec3_quiz(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_3_quiz_url));
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void openDeleteAccountPage(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://espotask.in/delete-request.php"));
        startActivity(intent);
    }

    public void settings_privcay_policy(View view) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
            String url = sharedPreferences.getString("app_internal_settings_page_privacy_page_link", "NULL");

            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("PAGE_TITLE", "Privacy Policy");
            intent.putExtra("PAGE_SUBTITLE", "Read our policy");
            intent.putExtra("URL", url);
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Toast.makeText(this, "Error opening Privacy Policy", Toast.LENGTH_SHORT).show();
        }
    }

    public void settings_terms_conditions(View view) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
            String url = sharedPreferences.getString("app_internal_settings_page_terms_condition_page_link", "NULL");

            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("PAGE_TITLE", "Terms & Conditions");
            intent.putExtra("PAGE_SUBTITLE", "Read our terms");
            intent.putExtra("URL", url);
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Toast.makeText(this, "Error opening Terms & Conditions", Toast.LENGTH_SHORT).show();
        }
    }
    // Java Example - How to use the layout dynamically
//    public class CardItemHelper {
//
//        // Method to populate card with data
//        public void bindCardData(View cardView, CardItem item) {
//            ImageView itemIcon = cardView.findViewById(R.id.itemIcon);
//            TextView itemTitle = cardView.findViewById(R.id.itemTitle);
//            TextView itemSubtitle = cardView.findViewById(R.id.itemDescription);  // Changed to itemDescription
//            LinearLayout statusContainer = cardView.findViewById(R.id.statusContainer);
//            TextView statusText = cardView.findViewById(R.id.statusText);
//            LinearLayout progressContainer = cardView.findViewById(R.id.progressContainer);
//            TextView progressValue = cardView.findViewById(R.id.progressValue);
//            ProgressBar progressBar = cardView.findViewById(R.id.progressBar);
//            MaterialCardView cardContainer = cardView.findViewById(R.id.cardContainer);
//
//            // Set basic data
//            itemTitle.setText(item.getTitle());
//            if (itemSubtitle != null) {  // Add null check
//                itemSubtitle.setText(item.getSubtitle());
//            }
//
//            // Load image with better error handling
//            if (item.getIconResource() != 0) {
//                itemIcon.setImageResource(item.getIconResource());
//            } else if (item.getIconUrl() != null && !item.getIconUrl().isEmpty()) {
//                // Uncomment if you're using Glide
//                // Glide.with(cardView.getContext()).load(item.getIconUrl()).into(itemIcon);
//            } else {
//                // Set default icon
//                itemIcon.setImageResource(android.R.drawable.ic_dialog_info);
//            }
//
//            // Show/hide status badge (only if these views exist in layout)
//            if (statusContainer != null) {
//                if (item.getStatusText() != null) {
//                    statusContainer.setVisibility(View.VISIBLE);
//                    if (statusText != null) {
//                        statusText.setText(item.getStatusText());
//                    }
//                } else {
//                    statusContainer.setVisibility(View.GONE);
//                }
//            }
//
//            // Show/hide progress (only if these views exist in layout)
//            if (progressContainer != null) {
//                if (item.hasProgress()) {
//                    progressContainer.setVisibility(View.VISIBLE);
//                    if (progressValue != null) {
//                        progressValue.setText(item.getProgressText());
//                    }
//                    if (progressBar != null) {
//                        progressBar.setProgress(item.getProgressValue());
//                    }
//                } else {
//                    progressContainer.setVisibility(View.GONE);
//                }
//            }
//
//            // Set click listener
//            if (cardContainer != null) {
//                cardContainer.setOnClickListener(v -> {
//                    // Add click animation
//                    animateCardClick(cardContainer);
//
//                    // Handle click action
//                    if (item.getClickListener() != null) {
//                        item.getClickListener().onClick(v);
//                    }
//                });
//
//                // Add subtle entrance animation
//                animateCardEntrance(cardContainer);
//            } else {
//                // If cardContainer is null, set click listener on the whole view
//                cardView.setOnClickListener(v -> {
//                    if (item.getClickListener() != null) {
//                        item.getClickListener().onClick(v);
//                    }
//                });
//            }
//        }
//
//        // Smooth click animation
//        private void animateCardClick(MaterialCardView card) {
//            card.animate()
//                    .scaleX(0.95f)
//                    .scaleY(0.95f)
//                    .setDuration(100)
//                    .withEndAction(() -> {
//                        card.animate()
//                                .scaleX(1.0f)
//                                .scaleY(1.0f)
//                                .setDuration(100)
//                                .start();
//                    })
//                    .start();
//        }
//
//        // Entrance animation for cards
//        private void animateCardEntrance(MaterialCardView card) {
//            card.setAlpha(0f);
//            card.setTranslationY(50f);
//            card.animate()
//                    .alpha(1f)
//                    .translationY(0f)
//                    .setDuration(300)
//                    .setInterpolator(new DecelerateInterpolator())
//                    .start();
//        }
//    }

    // Data model class
    public static class CardItem {
        private final String title;
        private final String subtitle;
        private String iconUrl;
        private int iconResource;
        private String statusText;
        private boolean hasProgress;
        private int progressValue;
        private String progressText;
        private View.OnClickListener clickListener;

        // Constructor
        public CardItem(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }

        // Builder pattern for easy creation
        public static class Builder {
            private CardItem item;

            public Builder(String title, String subtitle) {
                item = new CardItem(title, subtitle);
            }

            public Builder setIcon(int resourceId) {
                item.iconResource = resourceId;
                return this;
            }

            public Builder setIconUrl(String url) {
                item.iconUrl = url;
                return this;
            }

            public Builder setStatus(String status) {
                item.statusText = status;
                return this;
            }

            public Builder setProgress(int progress, String text) {
                item.hasProgress = true;
                item.progressValue = progress;
                item.progressText = text;
                return this;
            }

            public Builder setOnClickListener(View.OnClickListener listener) {
                item.clickListener = listener;
                return this;
            }

            public CardItem build() {
                return item;
            }
        }

        // Getters
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
        public String getIconUrl() { return iconUrl; }
        public int getIconResource() { return iconResource; }
        public String getStatusText() { return statusText; }
        public boolean hasProgress() { return hasProgress; }
        public int getProgressValue() { return progressValue; }
        public String getProgressText() { return progressText; }
        public View.OnClickListener getClickListener() { return clickListener; }
    }

// Usage Example in Activity/Fragment:
/*
// Create card items
CardItem premiumFeature = new CardItem.Builder("Premium Unlock", "Access all premium features")
    .setIcon(R.drawable.ic_premium)
    .setStatus("NEW")
    .setOnClickListener(v -> openPremiumScreen())
    .build();

CardItem progressItem = new CardItem.Builder("Daily Challenge", "Complete your daily tasks")
    .setIcon(R.drawable.ic_challenge)
    .setProgress(75, "75%")
    .setOnClickListener(v -> openChallengeScreen())
    .build();

// Bind to view
View cardView = inflater.inflate(R.layout.professional_card_item, container, false);
CardItemHelper.bindCardData(cardView, premiumFeature);
*/

    public void settings_faq(View view) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
            String url = sharedPreferences.getString("app_internal_settings_page_helpandsupport_page_link", "NULL");

            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("PAGE_TITLE", "Help & Support");
            intent.putExtra("PAGE_SUBTITLE", "FAQs");
            intent.putExtra("URL", url);
            startActivity(intent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Toast.makeText(this, "Error opening Help & Support", Toast.LENGTH_SHORT).show();
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
        String url = getString(R.string.app_url) +"/game_list_api.php";
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

        // Fetch ads and setup Unity if needed
        public void fetchAndSetupAds(Context context) {
            appContext = context;
            Toast.makeText(context, "Fetching Ads...", Toast.LENGTH_SHORT).show();

            String url = context.getString(R.string.app_url) + "/get_active_ads.php";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("status").equals("success")) {
                                JSONObject data = json.getJSONObject("data");
                                String provider = data.getString("provider_name");

                                Log.e("UNITY_AD", provider);
//                                Toast.makeText(context, "Ad Provider: " + provider, Toast.LENGTH_SHORT).show();

                                if (provider.equalsIgnoreCase("Unity")) {
                                    String unityAppId = data.getString("app_id");

                                    if (!UnityAds.isInitialized()) {
                                        UnityAds.initialize((Activity) context, unityAppId, false);
                                    }

                                    FrameLayout bannerContainer = ((Activity) context).findViewById(R.id.banner_container);

                                    if (data.optBoolean("is_banner_enabled", false)) {
                                        String unityBannerId = data.optString("banner_ad_id", "");
                                        bannerContainer.setVisibility(View.VISIBLE);
                                        loadUnityBannerAd(context, unityBannerId);
                                    } else {
                                        bannerContainer.setVisibility(View.GONE);
                                    }


                                    if (data.optBoolean("is_interstitial_enabled", false)) {
                                        String unityInterstitialId = data.optString("interstitial_ad_id", "");
                                        loadUnityInterstitialAd(context, unityInterstitialId);
                                    }

                                    if (data.optBoolean("is_rewarded_enabled", false)) {
                                        unityRewardedId = data.optString("rewarded_ad_id", "");
                                        preloadUnityRewardedAd(context, unityRewardedId);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Ad Fetch Error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Log.e("AD_FETCH_ERROR", error.toString())
            );

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(stringRequest);
        }

        private void loadUnityInterstitialAd(Context context, String adUnitId) {
            UnityAds.load(adUnitId, new IUnityAdsLoadListener() {
                @Override
                public void onUnityAdsAdLoaded(String placementId) {
                    Toast.makeText(context, "Unity Interstitial Loaded: " + placementId, Toast.LENGTH_SHORT).show();
                    UnityAds.show((Activity) context, placementId);
                }

                @Override
                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                    Log.e("UNITY_AD", "Interstitial Load Failed: " + message);
                }
            });
        }

        private void preloadUnityRewardedAd(Context context, String adUnitId) {
            UnityAds.load(adUnitId, new IUnityAdsLoadListener() {
                @Override
                public void onUnityAdsAdLoaded(String placementId) {
                    Toast.makeText(context, "Unity Rewarded Ad Loaded: " + placementId, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                    Log.e("UNITY_AD", "Rewarded Load Failed: " + message);
                }
            });
        }

        private void loadUnityBannerAd(Context context, String adUnitId) {
            if (UnityAds.isInitialized()) {
                Toast.makeText(context, "Unity Banner Ad Loaded: " + adUnitId, Toast.LENGTH_SHORT).show();
                BannerView bannerView = new BannerView((Activity) context, adUnitId, new UnityBannerSize(320, 50));
                FrameLayout bannerContainer = ((Activity) context).findViewById(R.id.banner_container);
                bannerContainer.removeAllViews();
                bannerContainer.addView(bannerView);
                bannerView.load();
            } else {
                Toast.makeText(context, "Unity Banner Ad Loaded Not Loaded: " + adUnitId, Toast.LENGTH_SHORT).show();
            }
        }

        // Setup watch ad button (call this in your activity)
        public void setupWatchAdButton(Activity activity) {
            Button watchAdBtn = activity.findViewById(R.id.watch_ad);
            watchAdBtn.setOnClickListener(v -> {
                if (unityRewardedId != null && UnityAds.isInitialized()) {
                    UnityAds.show(activity, unityRewardedId, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                            if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                                rewardForVideoTask(activity, userId);
                                Toast.makeText(activity, "Ad Seen, reward granted!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override public void onUnityAdsShowStart(String placementId) {}
                        @Override public void onUnityAdsShowClick(String placementId) {}
                        @Override public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                            Toast.makeText(activity, "Failed to show ad: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(activity, "Ad not ready yet. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Reward the user after ad view
        public void rewardForVideoTask(Context context, int userId) {
            String url = context.getString(R.string.app_url) + "/reward_video_task.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                int coins = jsonObject.getInt("coins_rewarded");
                                Toast.makeText(context, "You earned " + coins + " coins!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error parsing reward response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(userId));
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(stringRequest);
        }

    private void fetchSocialLinks() {
        String url = getString(R.string.app_url) + "/get_social_links.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            JSONObject links = json.getJSONObject("social_links");
                            facebookUrl = links.getString("facebook");
                            instagramUrl = links.getString("instagram");
                            telegramUrl = links.getString("telegram");
                            youtubeUrl = links.getString("youtube");

                            setButtonListeners();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void setButtonListeners() {
        btnFacebook.setOnClickListener(v -> openUrl(facebookUrl));
        btnInstagram.setOnClickListener(v -> openUrl(instagramUrl));
        btnTelegram.setOnClickListener(v -> openUrl(telegramUrl));
        btnYoutube.setOnClickListener(v -> openUrl(youtubeUrl));
    }

    private void openUrl(String url) {
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Link not available", Toast.LENGTH_SHORT).show();
        }
    }

    private class EditProfileActivity {
    }

    private void initializeCPXResearch() {
        try {
            CPXApplication app = (CPXApplication) getApplication();
            cpxResearch = app.getCpxResearch();

            // Register listener for survey updates
            cpxResearch.registerListener(new CPXResearchListener() {
                @Override
                public void onSurveysUpdated() {
                    List<SurveyItem> surveys = cpxResearch.getSurveys();
                    Log.d("CPX_SURVEYS", "Surveys updated: " + surveys.size() + " surveys available");

                    // Update UI with new survey count
                    runOnUiThread(() -> {
                        if (surveys.size() > 0) {
                            Toast.makeText(HomeActivity.this,
                                    surveys.size() + " surveys available",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onTransactionsUpdated(List<TransactionItem> unpaidTransactions) {
                    for (TransactionItem transaction : unpaidTransactions) {
                        Log.d("CPX_TRANSACTION", "Earning: " + transaction.getEarningPublisher());

                        // Award coins to user
//                        awardCoinsForTransaction(transaction);
                    }
                }

                @Override
                public void onSurveysDidOpen() {
                    Log.d("CPX_SURVEYS", "Surveys list opened");
                }

                @Override
                public void onSurveysDidClose() {
                    Log.d("CPX_SURVEYS", "Surveys list closed");
                    // Refresh user balance when they return
                    SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
                    String userId = sharedPreferences.getString("userID", "NULL");
                    if (!userId.equals("NULL")) {
                        fetchUserData(Integer.parseInt(userId));
                    }
                }

                @Override
                public void onSurveyDidOpen() {
                    Log.d("CPX_SURVEYS", "Survey opened");
                }

                @Override
                public void onSurveyDidClose() {
                    Log.d("CPX_SURVEYS", "Survey closed");
                }
            });

            // Enable automatic banner display (if you want floating banner)
            // cpxResearch.setSurveyVisibleIfAvailable(true, this);

            // Request initial survey update
            cpxResearch.requestSurveyUpdate(true);

        } catch (Exception e) {
            Log.e("CPX_INIT", "Error initializing CPX: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupCPXSurveyCards() {
        try {
            // Find the container where you want to add survey cards
            LinearLayout surveyCardsParent = findViewById(R.id.survey_cards_container);

            if (surveyCardsParent == null) {
                Log.e("CPX_SETUP", "Survey cards container not found in layout");
                return;
            }

            // Configure CPX card appearance
            CPXCardConfiguration cardConfig = new CPXCardConfiguration.Builder()
                    .accentColor(Color.parseColor("#260975"))        // Purple accent color
                    .backgroundColor(Color.WHITE)                     // White background
                    .starColor(Color.parseColor("#ffaa00"))          // Gold stars
                    .inactiveStarColor(Color.parseColor("#dfdfdf"))  // Gray inactive stars
                    .textColor(Color.DKGRAY)                         // Dark gray text
                    .dividerColor(Color.parseColor("#260975"))       // Purple divider
                    .promotionAmountColor(Color.parseColor("#FF0000")) // Red for promotions
                    .cardsOnScreen(3)                                 // Show 3 cards at once
                    .cornerRadius(15f)                                // Rounded corners
                    .maximumSurveys(6)                               // Show max 6 surveys
                    .paddingHorizontal(0)                          // Horizontal spacing
                    .paddingVertical(10f)                            // Vertical spacing
                    .cpxCardStyle(CPXCardStyle.DEFAULT)              // Use default card style
                    .hideCurrencyName(false)                         // Show "Coins"
                    .hideRatingAmount(false)                         // Show rating count
                    .showCurrencyBeforeValue(false)                  // Show value before currency
                    .build();

            // Insert CPX cards into the container
            CPXApplication app = (CPXApplication) getApplication();
            app.getCpxResearch().insertCPXResearchCardsIntoContainer(
                    this,
                    surveyCardsParent,
                    cardConfig
            );

            Log.d("CPX_SETUP", "CPX survey cards setup complete");

        } catch (Exception e) {
            Log.e("CPX_SETUP", "Error setting up CPX cards: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to award coins when user completes survey
//    private void awardCoinsForTransaction(TransactionItem transaction) {
//        SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
//        String userId = sharedPreferences.getString("userID", "NULL");
//
//        if (userId.equals("NULL")) {
//            Log.e("CPX_REWARD", "User ID not found");
//            return;
//        }
//
//        String url = getString(R.string.app_url) + "/award_survey_coins.php";
//
//        StringRequest request = new StringRequest(Request.Method.POST, url,
//                response -> {
//                    try {
//                        JSONObject json = new JSONObject(response);
//                        if (json.getBoolean("success")) {
//                            // Mark transaction as paid in CPX system
//                            cpxResearch.markTransactionAsPaid(
//                                    transaction.getTransactionId(),
//                                    transaction.getMessageId()
//                            );
//
//                            // Show success message
//                            int coinsEarned = (int) transaction.getEarningPublisher();
//                            runOnUiThread(() -> {
//                                Toast.makeText(HomeActivity.this,
//                                        "Earned " + coinsEarned + " coins!",
//                                        Toast.LENGTH_LONG).show();
//                            });
//
//                            // Refresh user balance
//                            fetchUserData(Integer.parseInt(userId));
//
//                            Log.d("CPX_REWARD", "Successfully awarded " + coinsEarned + " coins");
//                        } else {
//                            Log.e("CPX_REWARD", "Server returned error: " + json.getString("message"));
//                        }
//                    } catch (JSONException e) {
//                        Log.e("CPX_REWARD", "JSON parsing error: " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                },
//                error -> {
//                    Log.e("CPX_REWARD", "Network error awarding coins: " + error.toString());
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", userId);
//                params.put("coins", String.valueOf((int) transaction.getEarningPublisher()));
//                params.put("transaction_id", transaction.getTransactionId());
//                return params;
//            }
//        };
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(request);
//    }

    // Method to manually open survey list (connect to button click)
    public void openCPXSurveyList(View view) {
        try {
            CPXApplication app = (CPXApplication) getApplication();
            app.getCpxResearch().openSurveyList(this);
        } catch (Exception e) {
            Log.e("CPX_OPEN", "Error opening survey list: " + e.getMessage());
            Toast.makeText(this, "Unable to open surveys", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to open specific survey
    private void openSpecificSurvey(String surveyId) {
        try {
            CPXApplication app = (CPXApplication) getApplication();
            app.getCpxResearch().openSurvey(this, surveyId);
        } catch (Exception e) {
            Log.e("CPX_OPEN", "Error opening specific survey: " + e.getMessage());
        }
    }

    // Method to manually refresh surveys
    public void refreshCPXSurveys(View view) {
        try {
            CPXApplication app = (CPXApplication) getApplication();
            app.getCpxResearch().requestSurveyUpdate(false);
            Toast.makeText(this, "Refreshing surveys...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("CPX_REFRESH", "Error refreshing surveys: " + e.getMessage());
        }
    }
}



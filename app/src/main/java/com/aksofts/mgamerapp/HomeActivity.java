package com.aksofts.mgamerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ScrollView home_scroll_section , game_scroll_section , reward_scroll_section , profile_scroll_section;
    ImageView icon_home , icon_game , icon_reward , icon_profile;
    TextView text_home , text_game , text_reward , text_profile;
    MaterialCardView nav_home, nav_game, nav_reward, nav_profile;
    private Button btnLogout;
    MaterialCardView home_sec1_layout_game_tab, home_sec1_layout_apptask_tab, home_sec1_layout_survey_tab;
    String app_home_top_sec_1_game,app_home_top_sec_1_game_url,app_home_top_sec_1_apptask,app_home_top_sec_1_apptask_url,app_home_top_sec_1_survey,app_home_top_sec_1_survey_url;

    MaterialCardView home_sec3_layout_game_tab, home_sec3_layout_ffblog_tab, home_sec3_layout_quiz_tab;
    String app_home_top_sec_3_game_onoff, app_home_top_sec_3_game_url,app_home_top_sec_3_ffblog_onoff,app_home_top_sec_3_ffblog_url,app_home_top_sec_3_quiz_onoff,app_home_top_sec_3_quiz_url;

    String withdraw_list_data_setting;

    private Button showBottomSheetButton;

    RecyclerView recyclerView;
    WithdrawSelectionItem withdraw_selection_adapter;
    List<WithdrawSelectionItem> withdraw_selection_ItemList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        icon_home = findViewById(R.id.icon_home);

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

        btnLogout = findViewById(R.id.btnLogout);
        // Storing Into Shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);



        String storedID = sharedPreferences.getString("userID", "NULL");
        get_user_data_thread(storedID);
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

        recyclerView = findViewById(R.id.withdraw_selctionlist_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
            startWithdrawListActivity(item.getAbbrevation());
        });
        recyclerView.setAdapter(adapter);

//        String storedID = sharedPreferences.getString("userID", "NULL");
//        String storedID = sharedPreferences.getString("userID", "NULL");


        if (app_home_top_sec_1_game.toLowerCase().equals("off")){
            home_sec1_layout_game_tab.setVisibility(View.GONE);
        }
        if (app_home_top_sec_1_apptask.toLowerCase().equals("off")){
            home_sec1_layout_apptask_tab.setVisibility(View.GONE);
        }
        if (app_home_top_sec_1_survey.toLowerCase().equals("off")){
            home_sec1_layout_survey_tab.setVisibility(View.GONE);
        }

        if (app_home_top_sec_3_game_onoff.toLowerCase().equals("off")){
            home_sec3_layout_game_tab.setVisibility(View.GONE);
        }
        if (app_home_top_sec_3_ffblog_onoff.toLowerCase().equals("off")){
            home_sec3_layout_ffblog_tab.setVisibility(View.GONE);
        }
        if (app_home_top_sec_3_quiz_onoff.toLowerCase().equals("off")){
            home_sec3_layout_quiz_tab.setVisibility(View.GONE);
        }





        showBottomSheetDialog();



        findViewById(R.id.upiButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithdrawListActivity("upi");
            }
        });
        btnLogout.setOnClickListener(v -> logout_thread());  // लॉगआउट थ्रेड को कॉल करें
        findViewById(R.id.googlePlayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithdrawListActivity("google_play");
            }
        });
        findViewById(R.id.mobileLegendsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithdrawListActivity("mobile_legends_diamonds");
            }
        });
        findViewById(R.id.lordsMobileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithdrawListActivity("lords_mobile_diamonds");
            }
        });

        findViewById(R.id.ffDiamondsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithdrawListActivity("ff_diamonds");
            }
        });

    }
    // Called when the LinearLayout is clicked
    public void openGameOfferwall(View view) {
        Intent intent = new Intent(this, GameOfferwall.class);
        startActivity(intent);
    }
    public void logout_thread() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data
        editor.apply();

        // Show toast message for successful logout
        Toast.makeText(getApplicationContext(), "Logged Out Successfully!", Toast.LENGTH_SHORT).show();

        // Redirect to Login Activity
        Intent intent = new Intent(HomeActivity.this, OnboardingDisclosureActivity.class);  // Replace LoginActivity with your actual login activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();  // Finish this activity to clear it from the back stack
    }
    private void startWithdrawListActivity(String category) {
        Intent intent = new Intent(this, WithdrawListActivity.class);
        intent.putExtra("category", category);
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
        startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
    }

    public void invite_others_fn(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        String owncode = sharedPreferences.getString("owncode","pGamer");
        String app_share_message_before_refercode = sharedPreferences.getString("app_share_message_before_refercode","");
        String app_share_message_refercode_link = sharedPreferences.getString("app_share_message_refercode_link","");
        String app_share_message_after_refercode = sharedPreferences.getString("app_share_message_after_refercode","");

        String final_string_refer_link = app_share_message_refercode_link + owncode;
        String message = app_share_message_before_refercode +" " +owncode+"  and use link to download app : "+final_string_refer_link+app_share_message_after_refercode;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(sendIntent, "Share "+sharedPreferences.getString("app_name","")));
    }


    public void get_user_data_thread(String user_id){
        String get_user_data_qry = getResources().getString(R.string.app_url) + "/app-apis/user/get_view_homescrdata.php?";
        String datatohash="";
        try {
            datatohash ="i=" + URLEncoder.encode(user_id, "UTF-8");
            String token = temp.sha256_temp(datatohash);
            get_user_data_qry = get_user_data_qry+datatohash+"&token="+token;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalget_user_data_qry = get_user_data_qry;
        class dbprocess extends AsyncTask<String, Void, String> implements com.aksofts.mgamerapp.dbprocess {
            @Override
            protected void onPostExecute(String data) {
                if(data.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Something went Wrong ! - Contact Support Now", Toast.LENGTH_SHORT).show();
                }else if (data.equals("0")) {
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
                        if (status.equals("Blocked") || status.equals("0")){
                            Toast.makeText(getApplicationContext(), "Your Account has been Blocked - Contact Our Support team For More Info", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_1_apptask_url));
        startActivity(intent);
    }

    public void btn_fn_sec1_survey(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_1_survey_url));
        startActivity(intent);
    }

//    public void btn_fn_sec2_luckydraw(View view) {
//        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
//    }
//
//
//
//    public void btn_fn_sec2_luckynumber(View view) {
//        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
//    }

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
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void btn_fn_sec3_freefireblog(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_3_ffblog_url));
            startActivity(intent);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void btn_fn_sec3_quiz(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app_home_top_sec_3_quiz_url));
            startActivity(intent);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void settings_terms_conditions(View view) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
            String url = sharedPreferences.getString("app_internal_settings_page_terms_condition_page_link", "NULL");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void settings_privcay_policy(View view) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
            String url = sharedPreferences.getString("app_internal_settings_page_privacy_page_link", "NULL");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void settings_faq(View view) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
            String url = sharedPreferences.getString("app_internal_settings_page_helpandsupport_page_link", "NULL");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void goto_tournaments_activity_ff(View view) {
        try {
            Intent intent = new Intent(this, TournamentMatchesActivity.class);
            startActivity(intent);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }


}
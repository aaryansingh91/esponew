package com.rewards.espotask;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.makeopinion.cpxresearchlib.CPXResearch;
import com.makeopinion.cpxresearchlib.models.CPXConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXConfigurationBuilder;
import com.makeopinion.cpxresearchlib.models.CPXStyleConfiguration;
import com.makeopinion.cpxresearchlib.models.SurveyPosition;

public class CPXApplication extends Application {
    private CPXResearch cpxResearch;

    @Override
    public void onCreate() {
        super.onCreate();
        // Don't initialize CPX here - wait until we have user ID
    }

    @NonNull
    public CPXResearch getCpxResearch() {
        if (cpxResearch == null) {
            initCPX();
        }
        return cpxResearch;
    }

    public void initCPX() {
        // Get user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", "guest_user");

        // CPX Research credentials (Get these from your CPX Dashboard)
        String cpxAppId = "29292";
        String cpxSecureKey = "QAEBN5DP8HNsvjacE6I2n1Gjfytl3HFU";

        // Generate secure hash
        String secureHash = CPXHashGenerator.generateHash(cpxAppId, userId, cpxSecureKey);

        // Style configuration for CPX banner
        CPXStyleConfiguration style = new CPXStyleConfiguration(
                SurveyPosition.SideRightNormal,
                "Earn up to 3 Coins in<br> 4 minutes with surveys",
                20,
                "#ffffff",
                "#ffaf20",
                true
        );

        // Build CPX configuration
        CPXConfiguration config = new CPXConfigurationBuilder(
                cpxAppId,      // Your CPX App ID
                userId,        // User's unique ID
                secureHash,    // Secure hash
                style
        )
                // Optional: Add additional user info
                .withEmail(sharedPreferences.getString("email", ""))
                .build();

        // Initialize CPX Research - only pass config
        cpxResearch = CPXResearch.Companion.init(config);
    }

    // Method to reinitialize CPX with new user
    public void reinitializeCPX(String newUserId) {
        String cpxAppId = "29292";
        String cpxSecureKey = "QAEBN5DP8HNsvjacE6I2n1Gjfytl3HFU";
        String secureHash = CPXHashGenerator.generateHash(cpxAppId, newUserId, cpxSecureKey);

        CPXStyleConfiguration style = new CPXStyleConfiguration(
                SurveyPosition.SideRightNormal,
                "Earn up to 3 Coins in<br> 4 minutes with surveys",
                20,
                "#ffffff",
                "#ffaf20",
                true
        );

        CPXConfiguration config = new CPXConfigurationBuilder(cpxAppId, newUserId, secureHash, style)
                .build();

        cpxResearch = CPXResearch.Companion.init(config);
    }
}
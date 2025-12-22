package com.app.earnstation;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.makeopinion.cpxresearchlib.CPXResearch;
import com.makeopinion.cpxresearchlib.models.CPXConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXConfigurationBuilder;
import com.makeopinion.cpxresearchlib.models.CPXStyleConfiguration;
import com.makeopinion.cpxresearchlib.models.SurveyPosition;

public class EspoTaskApplication extends Application {

    private CPXResearch cpxResearch;

    @Override
    public void onCreate() {
        super.onCreate();

        // ✅ Initialize AdManager
        AdManager.getInstance().initialize(this);
    }

    @NonNull
    public CPXResearch getCpxResearch() {
        if (cpxResearch == null) {
            initCPX();
        }
        return cpxResearch;
    }

    public void initCPX() {
        SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", "guest_user");

        String cpxAppId = "29292";
        String cpxSecureKey = "QAEBN5DP8HNsvjacE6I2n1Gjfytl3HFU";
        String secureHash = CPXHashGenerator.generateHash(cpxAppId, userId, cpxSecureKey);

        CPXStyleConfiguration style = new CPXStyleConfiguration(
                SurveyPosition.SideRightNormal,
                "Earn up to 3 Coins in<br> 4 minutes with surveys",
                20,
                "#ffffff",
                "#ffaf20",
                true
        );

        CPXConfiguration config = new CPXConfigurationBuilder(
                cpxAppId,
                userId,
                secureHash,
                style
        )
                .withEmail(sharedPreferences.getString("email", ""))
                .build();

        cpxResearch = CPXResearch.Companion.init(config);
    }

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
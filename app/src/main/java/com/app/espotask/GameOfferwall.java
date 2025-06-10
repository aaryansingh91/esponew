package com.app.espotask;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJLogLevel;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJSegment;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;

import java.util.Hashtable;

public class GameOfferwall extends AppCompatActivity {

    private TJPlacement offerwallPlacement;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_offerwall);

        // ✅ initialize loader animation
        animationView = findViewById(R.id.animation_view);
        animationView.setAnimation(R.raw.animation_offerwall);
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE); // Show loader

        // ✅ Initialize Tapjoy
        Hashtable<String, Object> connectFlags = new Hashtable<>();
        connectFlags.put(TapjoyConnectFlag.TJC_OPTION_LOGGING_LEVEL, TJLogLevel.DEBUG);
        connectFlags.put(TapjoyConnectFlag.USER_ID, "user123");

        Tapjoy.connect(getApplicationContext(), "PJV3hj3FTHqM06rXziHEggECzeONpkgPA7EzSQ2P5TPqKvf44NmZCvInGHlb", connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                Toast.makeText(GameOfferwall.this, "Tapjoy Connected Successfully", Toast.LENGTH_SHORT).show();
                setupOfferwall();
            }

            @Override
            public void onConnectWarning(int code, String message) {
                Toast.makeText(GameOfferwall.this, "Tapjoy Connect Warning: " + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectFailure(int code, String message) {
                Toast.makeText(GameOfferwall.this, "Tapjoy Connect Failed: " + message, Toast.LENGTH_SHORT).show();
                hideLoader(); // stop animation if failed
            }
        });

        Tapjoy.setMaxLevel(10);
        Tapjoy.setUserSegment(TJSegment.NON_PAYER);
    }

    private void setupOfferwall() {
        offerwallPlacement = Tapjoy.getPlacement("App Offerwall", new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement placement) {
                // Optional: request successful
            }

            @Override
            public void onRequestFailure(TJPlacement placement, TJError error) {
                hideLoader();
                Toast.makeText(GameOfferwall.this, "Offerwall request failed: " + error.message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onContentReady(TJPlacement placement) {
                Toast.makeText(GameOfferwall.this, "Offerwall Ready", Toast.LENGTH_SHORT).show();
                placement.showContent(); // Show offerwall
            }

            @Override
            public void onContentShow(TJPlacement placement) {
                // Content is being shown
            }

            @Override
            public void onContentDismiss(TJPlacement placement) {
//                placement.requestContent(); // Request again when dismissed
                finish(); // ✅ Close the activity
            }

            @Override
            public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {
                // Not used
            }

            @Override
            public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {
                // Not used
            }
        });

        offerwallPlacement.requestContent(); // ✅ Trigger content loading
    }

    private void hideLoader() {
        if (animationView != null) {
            animationView.cancelAnimation();
            animationView.setVisibility(View.GONE);
        }
    }
}

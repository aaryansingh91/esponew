package com.app.earnstation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import com.vungle.ads.InitializationListener;
import com.vungle.ads.VungleAds;
import com.vungle.ads.VungleError;
import com.vungle.ads.BannerAd;
import com.vungle.ads.BannerAdListener;
import com.vungle.ads.VungleAdSize;
import com.vungle.ads.BaseAd;
import com.vungle.ads.InterstitialAd;
import com.vungle.ads.InterstitialAdListener;
import com.vungle.ads.RewardedAd;
import com.vungle.ads.RewardedAdListener;
import com.vungle.ads.AdConfig;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdManager implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "AdManager";
    private static AdManager instance;

    private Context context;
    private String providerName;
    private String appId;

    // Ad Unit IDs
    private String bannerAdId;
    private String interstitialAdId;
    private String rewardedAdId;

    // Ad Enable/Disable flags
    private boolean isBannerEnabled;
    private boolean isInterstitialEnabled;
    private boolean isRewardedEnabled;

    // Ad Control Settings
    private int backClicksForInterstitial = 3;
    private int secondsForInterstitial = 60;
    private boolean showAdOnAppOpen = false;
    private boolean preloadRewardedAds = true;

    // Unity Ad Objects
    private BannerView unityBannerView;
    private boolean isUnityInitialized = false;

    // Vungle Ad Objects
    private BannerAd vungleBannerAd;
    private InterstitialAd vungleInterstitialAd;
    private RewardedAd vungleRewardedAd;
    private boolean isVungleInitialized = false;

    // AdMob Ad Objects
    private AdView admobBannerView;
    private com.google.android.gms.ads.interstitial.InterstitialAd admobInterstitialAd;
    private com.google.android.gms.ads.rewarded.RewardedAd admobRewardedAd;
    private boolean isAdmobInitialized = false;

    // Tracking variables
    private int backPressCount = 0;
    private long appOpenTime = 0;
    private long lastInterstitialShowTime = 0;

    private AdManager() {}

    public static AdManager getInstance() {
        if (instance == null) {
            instance = new AdManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        this.context = context.getApplicationContext();
        ((Application) this.context).registerActivityLifecycleCallbacks(this);
        appOpenTime = System.currentTimeMillis();
    }

    public void setupWithConfig(String providerName, String appId,
                                String bannerAdId, boolean isBannerEnabled,
                                String interstitialAdId, boolean isInterstitialEnabled,
                                String rewardedAdId, boolean isRewardedEnabled,
                                int backClicks, int seconds,
                                boolean showOnOpen, boolean preloadRewarded) {

        this.providerName = providerName;
        this.appId = appId;
        this.bannerAdId = bannerAdId;
        this.isBannerEnabled = isBannerEnabled;
        this.interstitialAdId = interstitialAdId;
        this.isInterstitialEnabled = isInterstitialEnabled;
        this.rewardedAdId = rewardedAdId;
        this.isRewardedEnabled = isRewardedEnabled;

        this.backClicksForInterstitial = backClicks;
        this.secondsForInterstitial = seconds;
        this.showAdOnAppOpen = showOnOpen;
        this.preloadRewardedAds = preloadRewarded;

        Log.d(TAG, "Ad Provider: " + providerName);

        if ("Unity".equalsIgnoreCase(providerName)) {
            initializeUnity();
        } else if ("Vungle".equalsIgnoreCase(providerName)) {
            initializeVungle();
        } else if ("AdMob".equalsIgnoreCase(providerName)) {
            initializeAdMob();
        }
    }

    // ======================== UNITY ADS ========================

    private void initializeUnity() {
        if (isUnityInitialized) {
            Log.d(TAG, "Unity already initialized");
            return;
        }

        UnityAds.initialize(context, appId, false, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                isUnityInitialized = true;
                Log.d(TAG, "Unity initialized successfully");

                if (preloadRewardedAds && isRewardedEnabled) {
                    loadUnityRewardedAd();
                }

                if (isInterstitialEnabled) {
                    preloadInterstitial();
                }

                if (showAdOnAppOpen && isInterstitialEnabled) {
                    loadAndShowUnityInterstitial(null);
                }
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                Log.e(TAG, "Unity initialization failed: " + message);
            }
        });
    }

    public BannerView loadUnityBanner(Activity activity) {
        if (!isBannerEnabled || !isUnityInitialized) {
            return null;
        }

        unityBannerView = new BannerView(activity, bannerAdId, new UnityBannerSize(320, 50));
        unityBannerView.load();
        return unityBannerView;
    }

    private void loadAndShowUnityInterstitial(Activity activity) {
        if (!isInterstitialEnabled || !isUnityInitialized) {
            return;
        }

        UnityAds.load(interstitialAdId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.d(TAG, "Unity interstitial loaded");
                if (activity != null && !activity.isFinishing()) {
                    UnityAds.show(activity, placementId, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowStart(String placementId) {
                            Log.d(TAG, "Unity interstitial started");
                        }

                        @Override
                        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                            Log.d(TAG, "Unity interstitial completed");
                            lastInterstitialShowTime = System.currentTimeMillis();
                            backPressCount = 0;
                        }

                        @Override
                        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                            Log.e(TAG, "Unity interstitial failed: " + message);
                        }

                        @Override
                        public void onUnityAdsShowClick(String placementId) {
                            Log.d(TAG, "Unity interstitial clicked");
                        }
                    });
                }
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Unity interstitial failed to load: " + message);
            }
        });
    }

    private void loadUnityRewardedAd() {
        if (!isRewardedEnabled || !isUnityInitialized) {
            return;
        }

        UnityAds.load(rewardedAdId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.d(TAG, "Unity rewarded ad loaded");
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Unity rewarded ad failed to load: " + message);
            }
        });
    }

    public void showUnityRewardedAd(Activity activity, RewardCallback callback) {
        if (!isRewardedEnabled || !isUnityInitialized) {
            if (callback != null) callback.onAdNotAvailable();
            return;
        }

        UnityAds.show(activity, rewardedAdId, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowStart(String placementId) {
                Log.d(TAG, "Unity rewarded ad started");
            }

            @Override
            public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                    Log.d(TAG, "Unity rewarded ad completed - user rewarded");
                    if (callback != null) callback.onUserRewarded();
                }
                loadUnityRewardedAd();
            }

            @Override
            public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                Log.e(TAG, "Unity rewarded ad failed: " + message);
                if (callback != null) callback.onAdNotAvailable();
            }

            @Override
            public void onUnityAdsShowClick(String placementId) {
                Log.d(TAG, "Unity rewarded ad clicked");
            }
        });
    }

    // ======================== VUNGLE ADS ========================

    private void initializeVungle() {
        if (isVungleInitialized) {
            Log.d(TAG, "Vungle already initialized");
            return;
        }

        VungleAds.init(context, appId, new InitializationListener() {
            @Override
            public void onSuccess() {
                isVungleInitialized = true;
                Log.d(TAG, "Vungle initialized successfully");

                if (preloadRewardedAds && isRewardedEnabled) {
                    loadVungleRewardedAd();
                }

                if (isInterstitialEnabled) {
                    preloadInterstitial();
                }

                if (showAdOnAppOpen && isInterstitialEnabled) {
                    loadAndShowVungleInterstitial(null);
                }
            }

            @Override
            public void onError(@NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle initialization failed: " + vungleError.getErrorMessage());
            }
        });
    }

    public View loadVungleBanner(Activity activity) {
        if (!isBannerEnabled || !isVungleInitialized) {
            return null;
        }

        vungleBannerAd = new BannerAd(activity, bannerAdId, VungleAdSize.BANNER);
        vungleBannerAd.setAdListener(new BannerAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle banner loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle banner failed: " + vungleError.getErrorMessage());
            }

            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle banner failed to play: " + vungleError.getErrorMessage());
            }

            @Override
            public void onAdStart(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle banner started");
            }

            @Override
            public void onAdEnd(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle banner ended");
            }

            @Override
            public void onAdImpression(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdClicked(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
        });

        vungleBannerAd.load(null);

        try {
            return vungleBannerAd.getBannerView();
        } catch (Exception e) {
            Log.e(TAG, "Error getting banner view: " + e.getMessage());
            return null;
        }
    }

    private void loadAndShowVungleInterstitial(Activity activity) {
        if (!isInterstitialEnabled || !isVungleInitialized) {
            return;
        }

        vungleInterstitialAd = new InterstitialAd(context, interstitialAdId, new AdConfig());
        vungleInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle interstitial loaded");
                if (activity != null && !activity.isFinishing()) {
                    vungleInterstitialAd.play(activity);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle interstitial failed: " + vungleError.getErrorMessage());
            }

            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle interstitial failed to play: " + vungleError.getErrorMessage());
            }

            @Override
            public void onAdEnd(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle interstitial ended");
                lastInterstitialShowTime = System.currentTimeMillis();
                backPressCount = 0;
            }

            @Override
            public void onAdImpression(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdClicked(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdStart(@NonNull BaseAd baseAd) {}
        });

        vungleInterstitialAd.load(null);
    }

    private void loadVungleRewardedAd() {
        if (!isRewardedEnabled || !isVungleInitialized) {
            return;
        }

        vungleRewardedAd = new RewardedAd(context, rewardedAdId, new AdConfig());
        vungleRewardedAd.setAdListener(new RewardedAdListener() {
            @Override
            public void onAdLoaded(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle rewarded ad loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle rewarded ad failed: " + vungleError.getErrorMessage());
            }

            @Override
            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                Log.e(TAG, "Vungle rewarded ad failed to play: " + vungleError.getErrorMessage());
            }

            @Override
            public void onAdRewarded(@NonNull BaseAd baseAd) {
                Log.d(TAG, "Vungle rewarded ad - user rewarded");
            }

            @Override
            public void onAdEnd(@NonNull BaseAd baseAd) {
                loadVungleRewardedAd();
            }

            @Override
            public void onAdImpression(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdClicked(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
            @Override
            public void onAdStart(@NonNull BaseAd baseAd) {}
        });

        vungleRewardedAd.load(null);
    }

    public void showVungleRewardedAd(Activity activity, RewardCallback callback) {
        if (!isRewardedEnabled || !isVungleInitialized) {
            if (callback != null) callback.onAdNotAvailable();
            return;
        }

        if (vungleRewardedAd != null && vungleRewardedAd.canPlayAd()) {
            final boolean[] rewardGranted = {false};

            vungleRewardedAd.setAdListener(new RewardedAdListener() {
                @Override
                public void onAdRewarded(@NonNull BaseAd baseAd) {
                    rewardGranted[0] = true;
                    if (callback != null) callback.onUserRewarded();
                }

                @Override
                public void onAdEnd(@NonNull BaseAd baseAd) {
                    loadVungleRewardedAd();
                }

                @Override
                public void onAdLoaded(@NonNull BaseAd baseAd) {}

                @Override
                public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {}

                @Override
                public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                    if (callback != null) callback.onAdNotAvailable();
                }

                @Override
                public void onAdImpression(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdClicked(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdStart(@NonNull BaseAd baseAd) {}
            });

            vungleRewardedAd.play(activity);
        } else {
            if (callback != null) callback.onAdNotAvailable();
            loadVungleRewardedAd();
        }
    }

    // ======================== ADMOB ADS ========================

    private void initializeAdMob() {
        if (isAdmobInitialized) {
            Log.d(TAG, "AdMob already initialized");
            return;
        }

        // ✅ Initialize AdMob dynamically (no manifest needed)
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                isAdmobInitialized = true;
                Log.d(TAG, "AdMob initialized successfully");

                if (preloadRewardedAds && isRewardedEnabled) {
                    loadAdMobRewardedAd();
                }

                if (isInterstitialEnabled) {
                    preloadInterstitial();
                }

                if (showAdOnAppOpen && isInterstitialEnabled) {
                    loadAndShowAdMobInterstitial(null);
                }
            }
        });
    }

    public View loadAdMobBanner(Activity activity) {
        if (!isBannerEnabled || !isAdmobInitialized) {
            return null;
        }

        admobBannerView = new AdView(activity);
        admobBannerView.setAdUnitId(bannerAdId);
        admobBannerView.setAdSize(AdSize.BANNER);

        AdRequest adRequest = new AdRequest.Builder().build();
        admobBannerView.loadAd(adRequest);

        admobBannerView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "AdMob banner loaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.e(TAG, "AdMob banner failed: " + adError.getMessage());
            }
        });

        return admobBannerView;
    }

    private void loadAndShowAdMobInterstitial(Activity activity) {
        if (!isInterstitialEnabled || !isAdmobInitialized) {
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(
                context,
                interstitialAdId,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                        admobInterstitialAd = interstitialAd;
                        Log.d(TAG, "AdMob interstitial loaded");

                        if (activity != null && !activity.isFinishing()) {
                            admobInterstitialAd.show(activity);

                            admobInterstitialAd.setFullScreenContentCallback(
                                    new com.google.android.gms.ads.FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            Log.d(TAG, "AdMob interstitial dismissed");
                                            admobInterstitialAd = null;
                                            lastInterstitialShowTime = System.currentTimeMillis();
                                            backPressCount = 0;
                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                            Log.e(TAG, "AdMob interstitial failed to show: " + adError.getMessage());
                                            admobInterstitialAd = null;
                                        }

                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            Log.d(TAG, "AdMob interstitial showed");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "AdMob interstitial failed to load: " + loadAdError.getMessage());
                        admobInterstitialAd = null;
                    }
                });
    }

    private void loadAdMobRewardedAd() {
        if (!isRewardedEnabled || !isAdmobInitialized) {
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.rewarded.RewardedAd.load(
                context,
                rewardedAdId,
                adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd rewardedAd) {
                        admobRewardedAd = rewardedAd;
                        Log.d(TAG, "AdMob rewarded ad loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "AdMob rewarded ad failed to load: " + loadAdError.getMessage());
                        admobRewardedAd = null;
                    }
                });
    }

    public void showAdMobRewardedAd(Activity activity, RewardCallback callback) {
        if (!isRewardedEnabled || !isAdmobInitialized) {
            if (callback != null) callback.onAdNotAvailable();
            return;
        }

        if (admobRewardedAd != null) {
            admobRewardedAd.show(activity, rewardItem -> {
                Log.d(TAG, "AdMob rewarded ad - user rewarded");
                if (callback != null) callback.onUserRewarded();
            });

            admobRewardedAd.setFullScreenContentCallback(
                    new com.google.android.gms.ads.FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.d(TAG, "AdMob rewarded ad dismissed");
                            admobRewardedAd = null;
                            loadAdMobRewardedAd();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                            Log.e(TAG, "AdMob rewarded ad failed to show: " + adError.getMessage());
                            admobRewardedAd = null;
                            if (callback != null) callback.onAdNotAvailable();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(TAG, "AdMob rewarded ad showed");
                        }
                    });
        } else {
            if (callback != null) callback.onAdNotAvailable();
            loadAdMobRewardedAd();
        }
    }

    // ======================== UNIVERSAL METHODS ========================

    public void loadBanner(Activity activity, FrameLayout container) {
        if (!isBannerEnabled) {
            container.setVisibility(View.GONE);
            return;
        }

        container.setVisibility(View.VISIBLE);
        container.removeAllViews();

        if ("Unity".equalsIgnoreCase(providerName)) {
            BannerView bannerView = loadUnityBanner(activity);
            if (bannerView != null) container.addView(bannerView);
        } else if ("Vungle".equalsIgnoreCase(providerName)) {
            View bannerView = loadVungleBanner(activity);
            if (bannerView != null) container.addView(bannerView);
        } else if ("AdMob".equalsIgnoreCase(providerName)) {
            View bannerView = loadAdMobBanner(activity);
            if (bannerView != null) container.addView(bannerView);
        }
    }

    public void showRewardedAd(Activity activity, RewardCallback callback) {
        if ("Unity".equalsIgnoreCase(providerName)) {
            showUnityRewardedAd(activity, callback);
        } else if ("Vungle".equalsIgnoreCase(providerName)) {
            showVungleRewardedAd(activity, callback);
        } else if ("AdMob".equalsIgnoreCase(providerName)) {
            showAdMobRewardedAd(activity, callback);
        } else {
            if (callback != null) callback.onAdNotAvailable();
        }
    }

    public boolean isRewardedAdReady() {
        if ("Vungle".equalsIgnoreCase(providerName)) {
            return vungleRewardedAd != null && vungleRewardedAd.canPlayAd();
        } else if ("AdMob".equalsIgnoreCase(providerName)) {
            return admobRewardedAd != null;
        }
        return isUnityInitialized;
    }

    // ✅ PRELOAD INTERSTITIAL AD
    public void preloadInterstitial() {
        if (!isInterstitialEnabled) {
            return;
        }

        if ("Unity".equalsIgnoreCase(providerName) && isUnityInitialized) {
            UnityAds.load(interstitialAdId, new IUnityAdsLoadListener() {
                @Override
                public void onUnityAdsAdLoaded(String placementId) {
                    Log.d(TAG, "Unity interstitial preloaded");
                }

                @Override
                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                    Log.e(TAG, "Unity interstitial preload failed: " + message);
                }
            });
        } else if ("Vungle".equalsIgnoreCase(providerName) && isVungleInitialized) {
            vungleInterstitialAd = new InterstitialAd(context, interstitialAdId, new AdConfig());
            vungleInterstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onAdLoaded(@NonNull BaseAd baseAd) {
                    Log.d(TAG, "Vungle interstitial preloaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                    Log.e(TAG, "Vungle interstitial preload failed: " + vungleError.getErrorMessage());
                }

                @Override
                public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {}
                @Override
                public void onAdEnd(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdImpression(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdClicked(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdStart(@NonNull BaseAd baseAd) {}
            });
            vungleInterstitialAd.load(null);
        } else if ("AdMob".equalsIgnoreCase(providerName) && isAdmobInitialized) {
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.interstitial.InterstitialAd.load(
                    context,
                    interstitialAdId,
                    adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                            admobInterstitialAd = interstitialAd;
                            Log.d(TAG, "AdMob interstitial preloaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e(TAG, "AdMob interstitial preload failed: " + loadAdError.getMessage());
                            admobInterstitialAd = null;
                        }
                    });
        }
    }

    // ✅ SHOW INTERSTITIAL AD WITH CALLBACK
    public void showInterstitialAd(Activity activity, InterstitialCallback callback) {
        if (!isInterstitialEnabled) {
            if (callback != null) callback.onAdClosed();
            return;
        }

        if ("Unity".equalsIgnoreCase(providerName) && isUnityInitialized) {
            showUnityInterstitialWithCallback(activity, callback);
        } else if ("Vungle".equalsIgnoreCase(providerName) && isVungleInitialized) {
            showVungleInterstitialWithCallback(activity, callback);
        } else if ("AdMob".equalsIgnoreCase(providerName) && isAdmobInitialized) {
            showAdMobInterstitialWithCallback(activity, callback);
        } else {
            Log.d(TAG, "No ad provider available");
            if (callback != null) callback.onAdClosed();
        }
    }

    // Unity Interstitial with callback
    private void showUnityInterstitialWithCallback(Activity activity, InterstitialCallback callback) {
        UnityAds.load(interstitialAdId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                if (activity != null && !activity.isFinishing()) {
                    UnityAds.show(activity, placementId, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                            Log.d(TAG, "Unity interstitial completed");
                            lastInterstitialShowTime = System.currentTimeMillis();
                            backPressCount = 0;
                            if (callback != null) {
                                activity.runOnUiThread(() -> callback.onAdClosed());
                            }
                            // Preload next ad
                            preloadInterstitial();
                        }

                        @Override
                        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                            Log.e(TAG, "Unity interstitial failed: " + message);
                            if (callback != null) {
                                activity.runOnUiThread(() -> callback.onAdClosed());
                            }
                        }

                        @Override
                        public void onUnityAdsShowStart(String placementId) {
                            Log.d(TAG, "Unity interstitial started");
                        }

                        @Override
                        public void onUnityAdsShowClick(String placementId) {
                            Log.d(TAG, "Unity interstitial clicked");
                        }
                    });
                } else {
                    if (callback != null) callback.onAdClosed();
                }
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Unity interstitial load failed: " + message);
                if (callback != null) {
                    activity.runOnUiThread(() -> callback.onAdClosed());
                }
            }
        });
    }

    // Vungle Interstitial with callback
    private void showVungleInterstitialWithCallback(Activity activity, InterstitialCallback callback) {
        if (vungleInterstitialAd != null && vungleInterstitialAd.canPlayAd()) {
            vungleInterstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onAdEnd(@NonNull BaseAd baseAd) {
                    Log.d(TAG, "Vungle interstitial ended");
                    lastInterstitialShowTime = System.currentTimeMillis();
                    backPressCount = 0;
                    if (callback != null) {
                        activity.runOnUiThread(() -> callback.onAdClosed());
                    }
                    // Preload next ad
                    preloadInterstitial();
                }

                @Override
                public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                    Log.e(TAG, "Vungle interstitial failed to play: " + vungleError.getErrorMessage());
                    if (callback != null) {
                        activity.runOnUiThread(() -> callback.onAdClosed());
                    }
                }

                @Override
                public void onAdLoaded(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {}
                @Override
                public void onAdImpression(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdClicked(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdStart(@NonNull BaseAd baseAd) {}
            });

            vungleInterstitialAd.play(activity);
        } else {
            Log.d(TAG, "Vungle interstitial not ready, loading now...");
            // Load and show
            vungleInterstitialAd = new InterstitialAd(context, interstitialAdId, new AdConfig());
            vungleInterstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onAdLoaded(@NonNull BaseAd baseAd) {
                    Log.d(TAG, "Vungle interstitial loaded, showing now");
                    if (activity != null && !activity.isFinishing()) {
                        vungleInterstitialAd.play(activity);
                    } else {
                        if (callback != null) callback.onAdClosed();
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                    Log.e(TAG, "Vungle interstitial failed to load: " + vungleError.getErrorMessage());
                    if (callback != null) {
                        activity.runOnUiThread(() -> callback.onAdClosed());
                    }
                }

                @Override
                public void onAdEnd(@NonNull BaseAd baseAd) {
                    Log.d(TAG, "Vungle interstitial ended");
                    if (callback != null) {
                        activity.runOnUiThread(() -> callback.onAdClosed());
                    }
                    preloadInterstitial();
                }

                @Override
                public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                    Log.e(TAG, "Vungle interstitial failed to play: " + vungleError.getErrorMessage());
                    if (callback != null) {
                        activity.runOnUiThread(() -> callback.onAdClosed());
                    }
                }

                @Override
                public void onAdImpression(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdClicked(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdLeftApplication(@NonNull BaseAd baseAd) {}
                @Override
                public void onAdStart(@NonNull BaseAd baseAd) {}
            });
            vungleInterstitialAd.load(null);
        }
    }

    // AdMob Interstitial with callback
    private void showAdMobInterstitialWithCallback(Activity activity, InterstitialCallback callback) {
        if (admobInterstitialAd != null) {
            admobInterstitialAd.setFullScreenContentCallback(
                    new com.google.android.gms.ads.FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.d(TAG, "AdMob interstitial dismissed");
                            admobInterstitialAd = null;
                            lastInterstitialShowTime = System.currentTimeMillis();
                            backPressCount = 0;
                            if (callback != null) {
                                activity.runOnUiThread(() -> callback.onAdClosed());
                            }
                            // Preload next ad
                            preloadInterstitial();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                            Log.e(TAG, "AdMob interstitial failed to show: " + adError.getMessage());
                            admobInterstitialAd = null;
                            if (callback != null) {
                                activity.runOnUiThread(() -> callback.onAdClosed());
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(TAG, "AdMob interstitial showed");
                        }
                    });

            admobInterstitialAd.show(activity);
        } else {
            Log.d(TAG, "AdMob interstitial not ready, loading now...");
            // Load and show
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.interstitial.InterstitialAd.load(
                    context,
                    interstitialAdId,
                    adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                            admobInterstitialAd = interstitialAd;
                            Log.d(TAG, "AdMob interstitial loaded, showing now");

                            if (activity != null && !activity.isFinishing()) {
                                admobInterstitialAd.setFullScreenContentCallback(
                                        new com.google.android.gms.ads.FullScreenContentCallback() {
                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                Log.d(TAG, "AdMob interstitial dismissed");
                                                admobInterstitialAd = null;
                                                if (callback != null) {
                                                    activity.runOnUiThread(() -> callback.onAdClosed());
                                                }
                                                preloadInterstitial();
                                            }

                                            @Override
                                            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                                Log.e(TAG, "AdMob interstitial failed to show: " + adError.getMessage());
                                                admobInterstitialAd = null;
                                                if (callback != null) {
                                                    activity.runOnUiThread(() -> callback.onAdClosed());
                                                }
                                            }

                                            @Override
                                            public void onAdShowedFullScreenContent() {
                                                Log.d(TAG, "AdMob interstitial showed");
                                            }
                                        });

                                admobInterstitialAd.show(activity);
                            } else {
                                if (callback != null) callback.onAdClosed();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e(TAG, "AdMob interstitial failed to load: " + loadAdError.getMessage());
                            admobInterstitialAd = null;
                            if (callback != null) {
                                activity.runOnUiThread(() -> callback.onAdClosed());
                            }
                        }
                    });
        }
    }

    public boolean handleBackPress(Activity activity) {
        if (!isInterstitialEnabled) {
            return false;
        }

        backPressCount++;
        Log.d(TAG, "Back press count: " + backPressCount);

        if (backPressCount >= backClicksForInterstitial) {
            long timeSinceLastAd = System.currentTimeMillis() - lastInterstitialShowTime;
            long requiredTime = secondsForInterstitial * 1000L;

            if (timeSinceLastAd >= requiredTime) {
                if ("Unity".equalsIgnoreCase(providerName)) {
                    loadAndShowUnityInterstitial(activity);
                } else if ("Vungle".equalsIgnoreCase(providerName)) {
                    loadAndShowVungleInterstitial(activity);
                } else if ("AdMob".equalsIgnoreCase(providerName)) {
                    loadAndShowAdMobInterstitial(activity);
                }
                return true;
            }
        }

        return false;
    }

    public boolean shouldShowInterstitialByTime() {
        if (!isInterstitialEnabled) {
            return false;
        }

        long timeSinceAppOpen = System.currentTimeMillis() - appOpenTime;
        long timeSinceLastAd = System.currentTimeMillis() - lastInterstitialShowTime;

        return timeSinceAppOpen >= (secondsForInterstitial * 1000L) &&
                timeSinceLastAd >= (secondsForInterstitial * 1000L);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (shouldShowInterstitialByTime()) {
            if ("Unity".equalsIgnoreCase(providerName)) {
                loadAndShowUnityInterstitial(activity);
            } else if ("Vungle".equalsIgnoreCase(providerName)) {
                loadAndShowVungleInterstitial(activity);
            } else if ("AdMob".equalsIgnoreCase(providerName)) {
                loadAndShowAdMobInterstitial(activity);
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {}
    @Override
    public void onActivityStarted(@NonNull Activity activity) {}
    @Override
    public void onActivityPaused(@NonNull Activity activity) {}
    @Override
    public void onActivityStopped(@NonNull Activity activity) {}
    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {}
    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (admobBannerView != null) {
            admobBannerView.destroy();
        }
    }

    // ✅ CALLBACK INTERFACES
    public interface RewardCallback {
        void onUserRewarded();
        void onAdNotAvailable();
    }

    public interface InterstitialCallback {
        void onAdClosed();
    }
}
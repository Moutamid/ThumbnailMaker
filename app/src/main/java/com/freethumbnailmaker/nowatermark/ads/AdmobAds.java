package com.freethumbnailmaker.nowatermark.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.freethumbnailmaker.nowatermark.R;


import com.google.android.gms.ads.*;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

public class AdmobAds {

    private static InterstitialAd interstitialAd;
    private UnifiedNativeAd nativeAd;
    private boolean isAdLoad = true;
    private NativeAdLayout nativeAdLayout;
    public static OnAdsCloseListener mOnAdsCloseListener;
    private com.facebook.ads.InterstitialAd fBInterstitial;
    public interface OnAdsCloseListener {
        void onAdsClose();
    }

    public static void initFullAds(final Context context) {
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(context);

            interstitialAd.setAdUnitId(context.getString(R.string.admob_inter_id));
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    AdmobAds.loadFullAds();
                    if (AdmobAds.mOnAdsCloseListener != null) {
                        AdmobAds.mOnAdsCloseListener.onAdsClose();
                    }
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d("qq_ads","onAdFailedToLoad");
                }
            });
        }
        loadFullAds();
    }


    public static void loadFullAds() {
        InterstitialAd interstitialAd2 = interstitialAd;
        if (interstitialAd2 != null) {
            interstitialAd2.loadAd(new AdRequest.Builder().build());
        }
    }

    public static boolean showFullAds(OnAdsCloseListener onAdsCloseListener) {
        mOnAdsCloseListener = onAdsCloseListener;
        InterstitialAd interstitialAd2 = interstitialAd;
        if (interstitialAd2 == null || !interstitialAd2.isLoaded()) {
            return false;
        }
        interstitialAd.show();
        return true;
    }

    public static void loadBanner(Activity activity) {
        AdView adView = new AdView(activity);

        adView.setAdUnitId(activity.getString(R.string.admob_banner_id));
        adView.setAdSize(AdSize.SMART_BANNER);
        ((FrameLayout) activity.findViewById(R.id.admob_banner)).addView(adView);
        AdRequest build = new AdRequest.Builder().build();
        adView.loadAd(build);
    }

    public static void loadNativeAds(Activity activity, final View view) {
        final ViewGroup viewGroup = activity.findViewById(R.id.admob_native_container);
        final UnifiedNativeAdView unifiedNativeAdView = activity.findViewById(R.id.native_ad_view);
        unifiedNativeAdView.setMediaView(unifiedNativeAdView.findViewById(R.id.media_view));
        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.primary));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.secondary));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.cta));
        unifiedNativeAdView.setIconView(unifiedNativeAdView.findViewById(R.id.icon));
        unifiedNativeAdView.setAdvertiserView(unifiedNativeAdView.findViewById(R.id.tertiary));
        AdLoader build = new AdLoader.Builder(activity, activity.getString(R.string.admob_native_id)).forUnifiedNativeAd(unifiedNativeAd -> {
            AdmobAds.populateNativeAdView(unifiedNativeAd, unifiedNativeAdView);

            viewGroup.setVisibility(View.VISIBLE);
            ((View) viewGroup.getParent()).setVisibility(View.VISIBLE);

            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        }).build();
        build.loadAd(new AdRequest.Builder().build());
    }


    public static void populateNativeAdView(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView unifiedNativeAdView) {
        ((TextView) unifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
        ((TextView) unifiedNativeAdView.getBodyView()).setText(unifiedNativeAd.getBody());
        ((TextView) unifiedNativeAdView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
        NativeAd.Image icon = unifiedNativeAd.getIcon();
        if (icon == null) {
            unifiedNativeAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) unifiedNativeAdView.getIconView()).setImageDrawable(icon.getDrawable());
            unifiedNativeAdView.getIconView().setVisibility(View.VISIBLE);
        }
        if (unifiedNativeAd.getAdvertiser() == null) {
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            ((TextView) unifiedNativeAdView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
    }


//    private void loadFbBannerAd( Activity activity) {
//        com.facebook.ads.AdView fBAdView = new com.facebook.ads.AdView(activity,
//                activity.getString(R.string.fb_banner_id), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//        FrameLayout adContainer=((FrameLayout) activity.findViewById(R.id.admob_banner));
//        adContainer.addView(fBAdView);
//        fBAdView.loadAd();
//
//    }
//
//    private void loadFbInterstitial(Context context) {
//
//        fBInterstitial = new com.facebook.ads.InterstitialAd(context,
//                context.getString(R.string.Fb_interstitial_id));
//        fBInterstitial.loadAd(
//                fBInterstitial.buildLoadAdConfig()
//                        .build());
//
//
//    }
//
//    public  void showFbNativeAdExit(final Context context, final Activity activity) {
//
//
//        final String TAG = "NativeAdActivity".getClass().getSimpleName();
//
//
//        final NativeAd nativeAdfb = new NativeAd(context, context.getString(R.string.fb_native_id));
//
//
//        NativeAdListener nativeAdListener = new NativeAdListener() {
//            @Override
//            public void onMediaDownloaded(Ad ad) {
//                // Native ad finished downloading all assets
//                Log.e(TAG, "Native ad finished downloading all assets.");
//            }
//
//            @Override
//            public void onError(Ad ad, com.facebook.ads.AdError adError) {
//
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                if (nativeAdfb != ad) {
//
//                }
//                // Inflate Native Ad into Container
//                inflateAdExit(nativeAdfb, context, activity);
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                // Native ad clicked
//                Log.d(TAG, "Native ad clicked!");
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                // Native ad impression
//                Log.d(TAG, "Native ad impression logged!");
//            }
//        };
//
//        // Request an ad
//        nativeAdfb.loadAd(
//                nativeAdfb.buildLoadAdConfig()
//                        .withAdListener(nativeAdListener)
//                        .build());
//
//    }
//    public  void inflateAdExit(NativeAd nativeAd,Context context,Activity activity) {
//
//        nativeAd.unregisterView();
//        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View mView = layoutInflater.inflate(R.layout.exitdialog, null);
//        nativeAdLayout = mView.findViewById(R.id.native_ad_container_exit);
//        // Add the Ad view into the ad container.
//        LayoutInflater inflater = LayoutInflater.from(context);
//        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
//        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.exit_fb_native, nativeAdLayout, false);
//        nativeAdLayout.addView(adView);
//        bottomSheetDialog.setContentView(mView);
//        bottomSheetDialog.setCanceledOnTouchOutside(true);
//
//        com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
//        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
//        com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
//        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
//        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
//        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
//
//        // Set the Text.
//        nativeAdTitle.setText(nativeAd.getAdvertiserName());
//        nativeAdBody.setText(nativeAd.getAdBodyText());
//        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
//        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
//
//        // Create a list of clickable views
//        List<View> clickableViews = new ArrayList<>();
//        clickableViews.add(nativeAdTitle);
//        clickableViews.add(nativeAdCallToAction);
//
//        // Register the Title and CTA button to listen for clicks.
//        nativeAd.registerViewForInteraction(
//                adView, nativeAdMedia, nativeAdIcon, clickableViews);
//
//    }
}
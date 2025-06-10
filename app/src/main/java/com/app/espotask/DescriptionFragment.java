package com.app.espotask;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class DescriptionFragment extends Fragment {

    private TextView matchTypeOverlay, matchTitle, matchTypeText, matchMapText, entryFeeText, entryFeeTextTicket,
            perKillText, matchScheduleText, prizeDetailsText, matchDescriptionText, entryFeeSeparator ;
    private ImageView topImage, entryFeeIcon, entryFeeIconTicket;
    private static final String TAG = "DescriptionFragment";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Creating DescriptionFragment view");
        View view = inflater.inflate(R.layout.fragment_description, container, false);

        // Initialize views
        topImage = view.findViewById(R.id.top_image);
        matchTypeOverlay = view.findViewById(R.id.match_type_overlay);
        matchTitle = view.findViewById(R.id.match_title);
        matchTypeText = view.findViewById(R.id.match_type_text);
        matchMapText = view.findViewById(R.id.match_map_text);
        entryFeeText = view.findViewById(R.id.entry_fee_text);
        perKillText = view.findViewById(R.id.per_kill_text);
        matchScheduleText = view.findViewById(R.id.match_schedule_text);
        prizeDetailsText = view.findViewById(R.id.prize_details_text);
        matchDescriptionText = view.findViewById(R.id.match_description_text);

         entryFeeIcon = view.findViewById(R.id.entry_fee_icon);
         entryFeeIconTicket = view.findViewById(R.id.entry_fee_icon_ticket);
         entryFeeTextTicket = view.findViewById(R.id.entry_fee_text_ticket);
         entryFeeSeparator = view.findViewById(R.id.entry_fee_separator);


        // Apply arguments if available
        if (getArguments() != null) {
            updateMatchDetails(getArguments());
        } else {
            Log.d(TAG, "No data available for DescriptionFragment");
        }

        return view;
    }

    public void updateMatchDetails(Bundle bundle) {
        Log.d(TAG, "Updating match details");
        if (bundle == null) {
            Log.e(TAG, "Received null bundle");
            return;
        }
        matchTypeOverlay.setVisibility(View.VISIBLE);
        matchTypeOverlay.setText(bundle.getString("match_type", "Unknown"));
        matchTitle.setText(bundle.getString("match_name", "Unknown"));
        matchTypeText.setText(bundle.getString("match_type", "Unknown"));
        matchMapText.setText(bundle.getString("map", "Unknown"));
        entryFeeText.setText(String.valueOf(bundle.getInt("entry_fee", 0)));
        perKillText.setText(String.valueOf(bundle.getInt("per_kill", 0)));
        matchScheduleText.setText(bundle.getString("match_time", "Unknown"));
        prizeDetailsText.setText(bundle.getString("prize_details", ""));
        matchDescriptionText.setText(Html.fromHtml(bundle.getString("match_desc", "")));

        String entryType = bundle.getString("entry_type", "any");
        int coinFee = bundle.getInt("entry_fee_coins", 0);
        int ticketFee = bundle.getInt("entry_fee_tickets", 0);

        switch (entryType) {
            case "coin":
                entryFeeIcon.setVisibility(View.VISIBLE);
                entryFeeIcon.setImageResource(R.drawable.ic_coin_24);
                entryFeeText.setVisibility(View.VISIBLE);
                entryFeeText.setText(String.valueOf(coinFee));

                entryFeeSeparator.setVisibility(View.GONE);
                entryFeeIconTicket.setVisibility(View.GONE);
                entryFeeTextTicket.setVisibility(View.GONE);
                break;

            case "tickets":
                entryFeeIcon.setVisibility(View.VISIBLE);
                entryFeeIcon.setImageResource(R.drawable.ic_ticket_24);
                entryFeeText.setVisibility(View.VISIBLE);
                entryFeeText.setText(String.valueOf(ticketFee));

                entryFeeSeparator.setVisibility(View.GONE);
                entryFeeIconTicket.setVisibility(View.GONE);
                entryFeeTextTicket.setVisibility(View.GONE);
                break;

            case "any":
            default:
                entryFeeIcon.setVisibility(View.VISIBLE);
                entryFeeIcon.setImageResource(R.drawable.ic_coin_24);
                entryFeeText.setVisibility(View.VISIBLE);
                entryFeeText.setText(String.valueOf(coinFee));

                entryFeeSeparator.setVisibility(View.VISIBLE);

                entryFeeIconTicket.setVisibility(View.VISIBLE);
                entryFeeTextTicket.setVisibility(View.VISIBLE);
                entryFeeTextTicket.setText(String.valueOf(ticketFee));
                break;
        }



        // Load image
        String bannerUrl = bundle.getString("match_banner", "");
        if (!bannerUrl.isEmpty()) {
            Glide.with(this)
                    .load(bannerUrl)
                    .placeholder(R.drawable.freefire)
                    .error(R.drawable.freefire)
                    .into(topImage);
        } else {
            topImage.setImageResource(R.drawable.freefire);
        }
    }
}
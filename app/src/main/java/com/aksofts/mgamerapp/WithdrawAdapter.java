package com.aksofts.mgamerapp;
import static android.provider.Settings.System.getString;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.WithdrawViewHolder> {

    private List<WithdrawItem> withdrawItems;
    private Context context;
    private String userId;
    private int userCoins;

    public WithdrawAdapter(Context context, List<WithdrawItem> withdrawItems, int userCoins) {
        this.context = context;
        this.withdrawItems = withdrawItems;
        this.userCoins = userCoins;

        SharedPreferences sharedPreferences = context.getSharedPreferences("pgamerapp", Context.MODE_PRIVATE);
        this.userId = sharedPreferences.getString("userID", "0");
    }


    @NonNull
    @Override
    public WithdrawViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_withdraw, parent, false);
        return new WithdrawViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WithdrawViewHolder holder, int position) {
        WithdrawItem currentItem = withdrawItems.get(position);
        holder.nameTextView.setText(currentItem.getName());
        holder.rewardAmountTextView.setText(currentItem.getReward_amount());
        holder.coinsAmountTextView.setText(currentItem.getCoins_amount());

        if (currentItem.getType().equals("diamonds")) {
            holder.iconImageView.setImageResource(R.drawable.ic_ticket_24);
        } else if (currentItem.getType().equals("inr")) {
            holder.iconImageView.setImageResource(R.drawable.ic_rupee_24);
        }

        holder.itemView.setOnClickListener(v -> {
            // Convert required coins to integer
            int requiredCoins;
            try {
                requiredCoins = Integer.parseInt(currentItem.getCoins_amount());
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid coin amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userCoins < requiredCoins) {
                Toast.makeText(context, "Insufficient coins to withdraw", Toast.LENGTH_SHORT).show();
                return;
            }

            // Proceed with dialog if coins are enough
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_withdraw_form);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);

            EditText etNumber = dialog.findViewById(R.id.etNumber);
            Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

            btnSubmit.setOnClickListener(view -> {
                String number = etNumber.getText().toString();
                String amount = currentItem.getReward_amount();

                if (number.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(context, "Fill Details", Toast.LENGTH_SHORT).show();
                } else {
                    sendWithdrawRequest(context, userId, number, amount, currentItem.getType(), requiredCoins);

                    dialog.dismiss();
                }
            });

            dialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return withdrawItems.size();
    }

    public static class WithdrawViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView rewardAmountTextView;
        TextView coinsAmountTextView;
        ImageView iconImageView;

        public WithdrawViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            rewardAmountTextView = itemView.findViewById(R.id.rewardAmountTextView);
            coinsAmountTextView = itemView.findViewById(R.id.coinsAmountTextView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }
    }
    private void sendWithdrawRequest(Context context, String userId, String number, String amount, String type, int coins){
    String url = context.getString(R.string.app_url) + "/amsit-adm/withdraw_request_api.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("number", number);
                params.put("amount", amount);
                params.put("withdraw_type", type);
                params.put("coins", String.valueOf(coins));

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }

}
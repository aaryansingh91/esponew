package com.app.espotask;

// TaskOffersBottomSheet.java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TaskOffersBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_offers_bottom_sheet, container, false);

        ImageView closeBtn = view.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v -> dismiss());

        return view;
    }

}

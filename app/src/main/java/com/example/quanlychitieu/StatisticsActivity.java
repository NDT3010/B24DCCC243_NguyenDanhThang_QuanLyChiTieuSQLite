package com.example.quanlychitieu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvTotalAll;
    private LinearLayout layoutCategories;
    private Button btnBack;
    private DatabaseHelper dbHelper;

    private String[] allCategories = {"Ăn uống", "Đi lại", "Học tập", "Giải trí", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DatabaseHelper(this);

        tvTotalAll = findViewById(R.id.tvTotalAll);
        layoutCategories = findViewById(R.id.layoutCategories);
        btnBack = findViewById(R.id.btnBackStats);

        btnBack.setOnClickListener(v -> finish());

        loadStatistics();
    }

    private void loadStatistics() {
        Map<String, Long> categoryTotals = dbHelper.getTotalByCategory();

        long grandTotal = 0;
        for (Long value : categoryTotals.values()) {
            grandTotal += value;
        }

        DecimalFormat formatter = new DecimalFormat("#,###");
        tvTotalAll.setText("Tổng chi tiêu: " + formatter.format(grandTotal) + " đ");

        layoutCategories.removeAllViews();

        for (String category : allCategories) {
            long amount = categoryTotals.containsKey(category) ? categoryTotals.get(category) : 0;
            int percentage = grandTotal > 0 ? (int) (amount * 100 / grandTotal) : 0;

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(0, 16, 0, 16);

            TextView tvCategoryInfo = new TextView(this);
            tvCategoryInfo.setText(category + ": " + formatter.format(amount) + " đ (" + percentage + "%)");
            tvCategoryInfo.setTextSize(16);

            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setMax(100);
            progressBar.setProgress(percentage);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    24
            );
            params.topMargin = 8;
            progressBar.setLayoutParams(params);

            itemLayout.addView(tvCategoryInfo);
            itemLayout.addView(progressBar);

            layoutCategories.addView(itemLayout);
        }
    }
}

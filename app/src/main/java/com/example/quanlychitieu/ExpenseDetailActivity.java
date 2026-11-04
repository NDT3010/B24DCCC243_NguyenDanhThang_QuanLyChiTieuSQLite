package com.example.quanlychitieu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseDetailActivity extends AppCompatActivity {

    private TextView tvDetailName, tvDetailAmount, tvDetailCategory, tvDetailDate, tvDetailNote;
    private Button btnEdit, btnDelete, btnBack;
    private DatabaseHelper dbHelper;
    private int expenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        dbHelper = new DatabaseHelper(this);
        expenseId = getIntent().getIntExtra("expense_id", -1);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailAmount = findViewById(R.id.tvDetailAmount);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailNote = findViewById(R.id.tvDetailNote);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        loadExpenseDetail();

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseDetailActivity.this, AddEditExpenseActivity.class);
            intent.putExtra("expense_id", expenseId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(ExpenseDetailActivity.this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa khoản chi này?")
                    .setNegativeButton("Hủy", null)
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        dbHelper.deleteExpense(expenseId);
                        Toast.makeText(ExpenseDetailActivity.this, "Đã xóa khoản chi", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenseDetail();
    }

    private void loadExpenseDetail() {
        Expense expense = dbHelper.getExpenseById(expenseId);
        if (expense == null) {
            return;
        }

        tvDetailName.setText(expense.getName());

        DecimalFormat formatter = new DecimalFormat("#,###");
        tvDetailAmount.setText(formatter.format(expense.getAmount()) + " đ");

        tvDetailCategory.setText(expense.getCategory());

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = dbFormat.parse(expense.getDate());
            tvDetailDate.setText(displayFormat.format(date));
        } catch (ParseException e) {
            tvDetailDate.setText(expense.getDate());
        }

        if (expense.getNote() != null && !expense.getNote().isEmpty()) {
            tvDetailNote.setText(expense.getNote());
        } else {
            tvDetailNote.setText("Không có ghi chú");
        }
    }
}

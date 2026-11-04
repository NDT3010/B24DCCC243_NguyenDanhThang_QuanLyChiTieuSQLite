package com.example.quanlychitieu;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddEditExpenseActivity extends AppCompatActivity {

    private TextView tvTitle;
    private EditText edtName, edtAmount, edtNote;
    private Spinner spnCategory;
    private DatePicker datePicker;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int expenseId = -1;

    private String[] categories = {"Ăn uống", "Đi lại", "Học tập", "Giải trí", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense);

        dbHelper = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvTitle);
        edtName = findViewById(R.id.edtName);
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        spnCategory = findViewById(R.id.spnCategoryEdit);
        datePicker = findViewById(R.id.datePicker);
        btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(categoryAdapter);

        Calendar today = Calendar.getInstance();
        datePicker.setMaxDate(today.getTimeInMillis());

        if (getIntent().hasExtra("expense_id")) {
            expenseId = getIntent().getIntExtra("expense_id", -1);
            tvTitle.setText("Sửa khoản chi");
            btnSave.setText("Cập nhật khoản chi");
            loadExpenseData();
        } else {
            tvTitle.setText("Thêm khoản chi");
            btnSave.setText("Lưu khoản chi");
        }

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void loadExpenseData() {
        Expense expense = dbHelper.getExpenseById(expenseId);
        if (expense != null) {
            edtName.setText(expense.getName());
            edtAmount.setText(String.valueOf(expense.getAmount()));
            edtNote.setText(expense.getNote());

            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(expense.getCategory())) {
                    spnCategory.setSelection(i);
                    break;
                }
            }

            String[] dateParts = expense.getDate().split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int day = Integer.parseInt(dateParts[2]);
            datePicker.updateDate(year, month, day);
        }
    }

    private void saveExpense() {
        String name = edtName.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String note = edtNote.getText().toString().trim();
        String category = spnCategory.getSelectedItem().toString();

        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập tên khoản chi");
            edtName.requestFocus();
            return;
        }

        if (amountStr.isEmpty()) {
            edtAmount.setError("Vui lòng nhập số tiền");
            edtAmount.requestFocus();
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            edtAmount.setError("Số tiền không hợp lệ");
            edtAmount.requestFocus();
            return;
        }

        if (amount <= 0) {
            edtAmount.setError("Số tiền phải lớn hơn 0");
            edtAmount.requestFocus();
            return;
        }

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        String date = String.format("%04d-%02d-%02d", year, month, day);

        Expense expense = new Expense();
        expense.setName(name);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(date);
        expense.setNote(note);

        if (expenseId == -1) {
            long id = dbHelper.insertExpense(expense);
            if (id != -1) {
                Toast.makeText(this, "Đã thêm khoản chi", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi thêm khoản chi", Toast.LENGTH_SHORT).show();
            }
        } else {
            expense.setId(expenseId);
            int rows = dbHelper.updateExpense(expense);
            if (rows > 0) {
                Toast.makeText(this, "Đã cập nhật khoản chi", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật khoản chi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

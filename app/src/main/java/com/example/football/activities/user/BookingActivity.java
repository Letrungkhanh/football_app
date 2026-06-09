package com.example.football.activities.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.adapters.TimeSlotAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private TextView txtCourtName, txtCourtPrice;
    private Button btnSelectDate, btnConfirmBooking;
    private EditText edtUserPhone;

    // Khai báo dịch vụ
    private CheckBox chkWater, chkSting;
    private EditText edtQtyWater, edtQtySting;

    private String courtId, courtName;
    private int courtPrice;
    private String selectedDate = "";

    // HỆ THỐNG Ô LƯỚI THỜI GIAN THAY THẾ SPINNER
    private RecyclerView recyclerViewTimeSlots;
    private TimeSlotAdapter timeSlotAdapter;
    private List<String> totalSlotsList;
    private List<String> bookedSlotsList;
    private String selectedSlotTime = "";
    private TextView txtOwnerNameBooking, txtOwnerPhoneBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        if (getIntent() != null) {
            courtId = getIntent().getStringExtra("courtId");
            courtName = getIntent().getStringExtra("courtName");
            courtPrice = getIntent().getIntExtra("courtPrice", 0);
        }

        initViews();
        setupToolbar();
        setupTimeSlotsGrid();
        loadOwnerInfo();

        // =========================================================================
        // 👉 ĐÃ SỬA CHUẨN: Lấy đúng ngày tháng năm hiện tại không bị lệch ca
        // =========================================================================
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH); // Giữ nguyên giá trị gốc (0-11) để tí truyền vào DatePicker
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Chuỗi đối chiếu Firebase: Tháng hiển thị = Số gốc + 1
        selectedDate = currentDay + "/" + (currentMonth + 1) + "/" + currentYear;
        btnSelectDate.setText("📅 Ngày chọn: " + selectedDate);

        // Quét lịch trùng ngay lập tức cho ngày hôm nay
        loadBookedSlotsFromServer();
        // =========================================================================

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnConfirmBooking.setOnClickListener(v -> executeBooking());
    }

    private void initViews() {
        txtCourtName = findViewById(R.id.txtBookingCourtName);
        txtCourtPrice = findViewById(R.id.txtBookingCourtPrice);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        edtUserPhone = findViewById(R.id.edtUserPhoneBooking);
        btnConfirmBooking = findViewById(R.id.btnConfirmBookingUser);

        chkWater = findViewById(R.id.chkServiceWater);
        chkSting = findViewById(R.id.chkServiceSting);
        edtQtyWater = findViewById(R.id.edtQuantityWater);
        edtQtySting = findViewById(R.id.edtQuantitySting);
        txtOwnerNameBooking = findViewById(R.id.txtOwnerNameBooking);
        txtOwnerPhoneBooking = findViewById(R.id.txtOwnerPhoneBooking);

        chkWater.setOnCheckedChangeListener((buttonView, isChecked) -> {
            edtQtyWater.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) edtQtyWater.setText("1");
        });

        chkSting.setOnCheckedChangeListener((buttonView, isChecked) -> {
            edtQtySting.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) edtQtySting.setText("1");
        });

        recyclerViewTimeSlots = findViewById(R.id.recyclerViewTimeSlots);

        txtCourtName.setText(courtName);
        txtCourtPrice.setText("Giá tiền: " + String.format("%,d đ", courtPrice) + " / trận");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarBooking);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupTimeSlotsGrid() {
        recyclerViewTimeSlots.setLayoutManager(new GridLayoutManager(this, 3));

        totalSlotsList = new ArrayList<>();
        bookedSlotsList = new ArrayList<>();

        // Nạp danh sách các ca đá cố định của hệ thống
        totalSlotsList.add("7:00 - 8:30");
        totalSlotsList.add("8:30 - 10:00");
        totalSlotsList.add("10:00 - 11:30");
        totalSlotsList.add("11:30 - 13:00");
        totalSlotsList.add("13:30 - 14:30");
        totalSlotsList.add("14:30 - 16:00");
        totalSlotsList.add("16:00 - 17:30");
        totalSlotsList.add("17:30 - 19:00");
        totalSlotsList.add("19:00 - 20:30");
        totalSlotsList.add("20:30 - 22:00");



        // Cấu hình Adapter bắt sự kiện Click ô trống
        timeSlotAdapter = new TimeSlotAdapter(totalSlotsList, bookedSlotsList, slotTime -> {
            selectedSlotTime = slotTime;
        });
        recyclerViewTimeSlots.setAdapter(timeSlotAdapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Khởi tạo tháng gốc (0-11) chuẩn Android
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Truyền đúng biến 'month' gốc vào Dialog để mở đúng lịch tháng hiện tại
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            btnSelectDate.setText("📅 Ngày chọn: " + selectedDate);

            selectedSlotTime = ""; // Xóa ca cũ tránh bug lưu giờ ngày cũ
            loadBookedSlotsFromServer(); // Quét lại Firebase
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void loadBookedSlotsFromServer() {
        if (TextUtils.isEmpty(courtId) || TextUtils.isEmpty(selectedDate)) return;

        final String cleanCourtId = courtId.trim();
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings");

        bookingRef.orderByChild("date").equalTo(selectedDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookedSlotsList.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            String bCourtId = data.child("courtId").getValue(String.class);
                            String bStatus = data.child("status").getValue(String.class);


                            if (bCourtId != null && bStatus != null) {
                                String cleanBCourtId = bCourtId.trim();

                                // Kiểm tra trùng mã sân nhỏ và đơn hàng hợp lệ
                                if (cleanCourtId.equals(cleanBCourtId) && !"Bị hủy".equals(bStatus)) {
                                    String slotTime = data.child("slotTime").getValue(String.class);
                                    if (slotTime != null) {
                                        bookedSlotsList.add(slotTime.trim());
                                    }
                                }
                            }
                        }

                        System.out.println("DEBUG_KHANH: ID sân = " + cleanCourtId + " | Ngày = " + selectedDate);
                        System.out.println("DEBUG_KHANH: Số ca trùng hóa ĐỎ = " + bookedSlotsList.size());

                        // Cập nhật giao diện lưới ô vuông lập tức
                        timeSlotAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BookingActivity.this, "Lỗi quét lịch sân trùng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void executeBooking() {
        String phone = edtUserPhone.getText().toString().trim();

        // 1. Kiểm tra các điều kiện cơ bản
        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Vui lòng chọn ngày muốn đá sân!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(selectedSlotTime)) {
            Toast.makeText(this, "Vui lòng chọn khung giờ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bookedSlotsList != null && bookedSlotsList.contains(selectedSlotTime)) {
            Toast.makeText(this, "Khung giờ này đã có người đặt!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Lấy thông tin sân và chủ sân
        DatabaseReference courtRef = FirebaseDatabase.getInstance().getReference("courts").child(courtId);
        courtRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String stadiumId = snapshot.child("stadiumId").getValue(String.class);
                if (stadiumId == null) stadiumId = "unknown";

                // Lấy thêm thông tin chủ sân (Owner) từ node "stadiums"
                String finalStadiumId = stadiumId;
                DatabaseReference stadiumRef = FirebaseDatabase.getInstance().getReference("stadiums").child(finalStadiumId);
                stadiumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sSnapshot) {
                        String ownerId = sSnapshot.child("ownerId").getValue(String.class);

                        // Lấy chi tiết thông tin chủ sân từ node "users"
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(ownerId != null ? ownerId : "admin");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot uSnapshot) {
                                String ownerName = uSnapshot.child("name").getValue(String.class);
                                String ownerPhone = uSnapshot.child("phone").getValue(String.class);

                                // 3. Tính toán giá tiền
                                int finalPrice = courtPrice;
                                StringBuilder servicesBuilder = new StringBuilder();
                                if (chkWater.isChecked()) {
                                    int qty = TextUtils.isEmpty(edtQtyWater.getText().toString()) ? 1 : Integer.parseInt(edtQtyWater.getText().toString());
                                    finalPrice += (10000 * qty);
                                    servicesBuilder.append("Nước suối x").append(qty).append(", ");
                                }
                                if (chkSting.isChecked()) {
                                    int qty = TextUtils.isEmpty(edtQtySting.getText().toString()) ? 1 : Integer.parseInt(edtQtySting.getText().toString());
                                    finalPrice += (12000 * qty);
                                    servicesBuilder.append("Sting dâu x").append(qty).append(", ");
                                }

                                String selectedServicesResult = servicesBuilder.toString().replaceAll(", $", "");
                                if (TextUtils.isEmpty(selectedServicesResult)) selectedServicesResult = "Không chọn dịch vụ";

                                // 4. Tạo intent và truyền thông tin chủ sân sang PaymentActivity
                                String bookingId = FirebaseDatabase.getInstance().getReference("bookings").push().getKey();
                                String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "guest_user";

                                Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
                                intent.putExtra("bookingId", bookingId);
                                intent.putExtra("courtId", courtId);
                                intent.putExtra("stadiumId", finalStadiumId);
                                intent.putExtra("courtName", courtName);
                                intent.putExtra("userId", userId);
                                intent.putExtra("userPhone", phone);
                                intent.putExtra("bookingDate", selectedDate);
                                intent.putExtra("timeSlot", selectedSlotTime);
                                intent.putExtra("courtPrice", finalPrice);
                                intent.putExtra("selectedServices", selectedServicesResult);

                                // ĐÃ TRUYỀN THÔNG TIN CHỦ SÂN
                                intent.putExtra("ownerName", ownerName != null ? ownerName : "Hệ thống");
                                intent.putExtra("ownerPhone", ownerPhone != null ? ownerPhone : "Chưa cập nhật");

                                startActivity(intent);
                            }
                            @Override public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void loadOwnerInfo() {
        // 1. Lấy stadiumId trước
        DatabaseReference courtRef = FirebaseDatabase.getInstance().getReference("courts").child(courtId);
        courtRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String stadiumId = snapshot.child("stadiumId").getValue(String.class);
                if (stadiumId == null) return;

                // 2. Lấy ownerId từ node stadiums
                DatabaseReference stadiumRef = FirebaseDatabase.getInstance().getReference("stadiums").child(stadiumId);
                stadiumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sSnapshot) {
                        String ownerId = sSnapshot.child("ownerId").getValue(String.class);

                        // 3. Lấy thông tin user
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(ownerId != null ? ownerId : "admin");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot uSnapshot) {
                                String name = uSnapshot.child("name").getValue(String.class);
                                String phone = uSnapshot.child("phone").getValue(String.class);

                                // Hiển thị lên giao diện
                                txtOwnerNameBooking.setText("Tên: " + (name != null ? name : "Hệ thống"));
                                txtOwnerPhoneBooking.setText("SĐT: " + (phone != null ? phone : "Chưa cập nhật"));
                            }
                            @Override public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
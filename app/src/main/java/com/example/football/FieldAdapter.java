package com.example.football;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.football.models.Field;
import java.util.List;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    private Context context;
    private List<Field> fieldList;

    public FieldAdapter(Context context, List<Field> fieldList) {
        this.context = context;
        this.fieldList = fieldList;
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_field, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        Field field = fieldList.get(position);

        holder.nameTextView.setText(field.getName());
        holder.addressTextView.setText(field.getAddress());
        holder.priceTextView.setText(field.getPrice() + " VNĐ");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("fieldId", field.getId());
            intent.putExtra("fieldName", field.getName());
            intent.putExtra("fieldPrice", field.getPrice());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    public static class FieldViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, priceTextView;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
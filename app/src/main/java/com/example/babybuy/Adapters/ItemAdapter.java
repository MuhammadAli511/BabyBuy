package com.example.babybuy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybuy.ItemDetails;
import com.example.babybuy.Models.Item;
import com.example.babybuy.R;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    Context mContext;
    List<Item> itemList;

    public ItemAdapter(Context mContext, List<Item> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        holder.itemName.setText(itemList.get(pos).getName());
        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ItemDetails.class);
                intent.putExtra("id", itemList.get(pos).getItemID());
                intent.putExtra("name", itemList.get(pos).getName());
                intent.putExtra("price", itemList.get(pos).getPrice());
                intent.putExtra("image", itemList.get(pos).getImageURL());
                intent.putExtra("description", itemList.get(pos).getDescription());
                intent.putExtra("delegate", itemList.get(pos).getDelegateNumber());
                intent.putExtra("userID", itemList.get(pos).getUserID());
                intent.putExtra("purchased", itemList.get(pos).getPurchased());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public Button viewDetails;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            viewDetails = itemView.findViewById(R.id.viewDetails);
        }
    }
}
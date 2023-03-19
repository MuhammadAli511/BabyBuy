package com.example.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.babybuy.Adapters.ItemAdapter;
import com.example.babybuy.Models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    ImageView addItemButton;
    RecyclerView itemRecyclerView;
    FirebaseFirestore db;
    List<Item> itemsList;
    ItemAdapter itemAdapter;
    FirebaseUser user;
    FirebaseAuth mAuth;

    List<Item> purchasedList;
    ItemAdapter purchasedAdapter;
    RecyclerView purchasedRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        addItemButton = findViewById(R.id.addItemButton);
        itemRecyclerView = findViewById(R.id.itemRecyclerView);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        itemsList = new ArrayList<>();

        purchasedList = new ArrayList<>();
        purchasedRecyclerView = findViewById(R.id.purchasedRecyclerView);



        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        purchasedRecyclerView.setLayoutManager(layoutManager2);
        getData();

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, AddItem.class);
                startActivity(intent);
            }
        });
    }

    public void getData(){
        db.collection("Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    itemsList.clear();
                    purchasedList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item items = new Item(document.getString("name"), document.getString("imageURL"), document.getString("price"), document.getString("description"), document.getString("delegateNumber"), document.getString("userID"), document.getString("purchased"), document.getId());
                        if (items.getUserID().equals(user.getUid())){
                            if (items.getPurchased().equals("false")){
                                itemsList.add(items);
                            } else {
                                purchasedList.add(items);
                            }
                        }
                    }
                    itemAdapter = new ItemAdapter(MainScreen.this, itemsList);
                    itemRecyclerView.setAdapter(itemAdapter);

                    purchasedAdapter = new ItemAdapter(MainScreen.this, purchasedList);
                    purchasedRecyclerView.setAdapter(purchasedAdapter);
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
package com.example.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemDetails extends AppCompatActivity {

    ImageView back1, itemImage;
    EditText itemName, itemPrice, itemDescription, itemDelegate;
    CheckBox itemCheckBox;
    Button updateItem, deleteItem;
    FirebaseFirestore db;
    String userName1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            userName1 = documentSnapshot.getString("name");
        });

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String image = intent.getStringExtra("image");
        String description = intent.getStringExtra("description");
        String delegate = intent.getStringExtra("delegate");
        String userID = intent.getStringExtra("userID");
        String purchased = intent.getStringExtra("purchased");
        back1 = findViewById(R.id.back1);
        itemImage = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemDescription = findViewById(R.id.itemDescription);
        itemDelegate = findViewById(R.id.itemDelegate);
        itemCheckBox = findViewById(R.id.itemCheckBox);
        updateItem = findViewById(R.id.updateItem);
        deleteItem = findViewById(R.id.deleteItem);
        Glide.with(this).load(image).into(itemImage);
        itemName.setText(name);
        itemPrice.setText(price);
        itemDescription.setText(description);
        itemDelegate.setText(delegate);
        if (purchased.equals("true")) {
            itemCheckBox.setChecked(true);
        } else {
            itemCheckBox.setChecked(false);
        }
        Log.d("TAG 789", "onCreate: " + id);

        back1.setOnClickListener(v -> {
            finish();
        });

        updateItem.setOnClickListener(v -> {
            String name1 = itemName.getText().toString();
            String price1 = itemPrice.getText().toString();
            String description1 = itemDescription.getText().toString();
            String delegate1 = itemDelegate.getText().toString();
            String purchased1 = String.valueOf(itemCheckBox.isChecked());
            db.collection("Items").document(id).update("name", name1, "price", price1, "description", description1, "delegateNumber", delegate1, "purchased", purchased1);
            if (!delegate1.equals("")){
                db.collection("Users").whereEqualTo("phoneNum", delegate1).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            String delegateId = task.getResult().getDocuments().get(0).getId();
                            db.collection("Users").document(delegateId).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    String delegateDeviceId = task1.getResult().get("deviceID").toString();
                                    String message = userName1 + " wants you to buy " + name1 + " for " + price1 + ". It is" + description1 + ".";
                                    try {
                                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+message+"'}, 'include_player_ids': ['" + delegateDeviceId + "']}"),null);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    Toast.makeText(ItemDetails.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(ItemDetails.this, "No such delegate", Toast.LENGTH_SHORT).show();
                });
            }
            finish();
        });

        deleteItem.setOnClickListener(v -> {
            db.collection("Items").document(id).delete();
            finish();
        });

    }
}
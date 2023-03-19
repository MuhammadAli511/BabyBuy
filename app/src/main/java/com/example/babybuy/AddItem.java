package com.example.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.babybuy.Models.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class AddItem extends AppCompatActivity {

    ImageView back,uploadImage;
    Button addItem;
    EditText name, price, description, delegate;
    FirebaseStorage storage;
    FirebaseFirestore db;
    StorageReference storageRef;
    Uri imageURI;
    ProgressDialog progressDialog;
    String userName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        back = findViewById(R.id.back);
        uploadImage = findViewById(R.id.uploadImage);
        addItem = findViewById(R.id.addItem);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        delegate = findViewById(R.id.delegate);
        storageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Item...");

        // current user name using firestore

        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            userName = documentSnapshot.getString("name");
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty() || price.getText().toString().isEmpty() || description.getText().toString().isEmpty()) {
                    Toast.makeText(AddItem.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    if (imageURI != null) {
                        StorageReference ref = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
                        ref.putFile(imageURI).addOnSuccessListener(taskSnapshot -> {
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                String url = uri.toString();
                                String name1 = name.getText().toString();
                                String price1 = price.getText().toString();
                                String description1 = description.getText().toString();
                                String delegate1 = delegate.getText().toString();
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String purchased = "false";
                                Item item = new Item(name1, url, price1, description1, delegate1, userId, purchased);
                                // get device id using users db and delegate
                                if (delegate1.equals("")){
                                    db.collection("Items").document().set(item).addOnSuccessListener(aVoid -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddItem.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddItem.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    db.collection("Users").whereEqualTo("phoneNum", delegate1).get().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().size() > 0) {
                                                String delegateId = task.getResult().getDocuments().get(0).getId();
                                                db.collection("Users").document(delegateId).get().addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        String delegateDeviceId = task1.getResult().get("deviceID").toString();
                                                        db.collection("Items").document().set(item).addOnSuccessListener(aVoid -> {
                                                            String message = userName + " wants you to buy " + name1 + " for " + price1 + ". It is" + description1 + ".";
                                                            try {
                                                                OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+message+"'}, 'include_player_ids': ['" + delegateDeviceId + "']}"),null);
                                                            } catch (JSONException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddItem.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }).addOnFailureListener(e -> {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddItem.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    }).addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddItem.this, "No such delegate", Toast.LENGTH_SHORT).show();
                                    });
                                }



                            });
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AddItem.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            uploadImage.setImageURI(data.getData());
            imageURI = data.getData();
        }
    }
}
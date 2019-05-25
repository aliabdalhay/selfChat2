package com.image.get.selfchat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("myDatabase").document("A74ym65IhadHvbhKF4TM");
    private static final String USER_NAME = "user_name";
    private static final String EMPTY_NAME = "";
    private String userName = EMPTY_NAME;
    private Map<String, Object> keyMap = new HashMap<>();
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        userName = sp.getString(USER_NAME, EMPTY_NAME);
        if(!userName.equals(EMPTY_NAME))
        {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("USER_NAME", userName);
            startActivity(intent);
            finish();
        }
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists())
                {
                    try
                    {
                        userName = documentSnapshot.getString(USER_NAME);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("USER_NAME", userName);
                        startActivity(intent);
                        finish();
                    }
                    catch (NumberFormatException e)
                    {
                        userName = EMPTY_NAME;
                    }
                }
                else
                {
                    userName = EMPTY_NAME;
                }
            }
        });

        final EditText user_name_edit_txt = findViewById(R.id.editText_User_name);
        final Button set_name_btn = findViewById(R.id.buttonMyname);
        final Button skip = findViewById(R.id.buttonSkip);
        set_name_btn.setVisibility(View.GONE);
        user_name_edit_txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()==0){
                    set_name_btn.setVisibility(View.GONE);
                } else {
                    set_name_btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        set_name_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userName = user_name_edit_txt.getText().toString();
                editor.putString(USER_NAME, userName);
                editor.apply();
                keyMap.put(USER_NAME, userName);
                docRef.set(keyMap);
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("USER_NAME", userName);
                startActivity(intent);
                finish();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("USER_NAME", EMPTY_NAME);
                startActivity(intent);
                finish();
            }
        });
    }


}
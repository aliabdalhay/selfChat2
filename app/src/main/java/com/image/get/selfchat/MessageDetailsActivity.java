package com.image.get.selfchat;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        final String current_user_name = getIntent().getStringExtra("CURRENT_USER_NAME");
        final String position = getIntent().getStringExtra("POSITION");
        String content = getIntent().getStringExtra("CONTENT");
        String time_stamp = getIntent().getStringExtra("TIME_STAMP");
        String device_model = getIntent().getStringExtra("DEVICE_MODEL");
        TextView details = findViewById(R.id.textView);
        TextView user_name = findViewById(R.id.textView_userName);
        TextView message_content = findViewById(R.id.textView_Content);
        TextView message_time_stamp = findViewById(R.id.textView_Timestamp);
        TextView message_device_model = findViewById(R.id.textView_DeviceInfo);
        Button delete_message = findViewById(R.id.button_Delete);
        user_name.setText("The current user: " + current_user_name);
        message_content.setText("The message content: " + content);
        message_time_stamp.setText("The message sent at: " + time_stamp);
        message_device_model.setText("Phone model is: " + device_model);
        delete_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("POSITION", position);
                intent.putExtra("USER_NAME", current_user_name);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
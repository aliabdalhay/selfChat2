package com.image.get.selfchat;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity implements ClickOnMsg{

    private int length_of_list;
    final Gson gson = new Gson();
    Button sendButton;
    EditText messageEditText;
    RecyclerView recyclerView;
    MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = (Button) findViewById(R.id.send_btn);
        messageEditText = (EditText) findViewById(R.id.edit_text);
        recyclerView = (RecyclerView) findViewById(R.id.myRecycle);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        final Toast toast = Toast.makeText(context, "you can't send an empty message, oh silly!", duration);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        length_of_list = sp.getInt("lst_length", 0);
        final SharedPreferences.Editor editor = sp.edit();
        myAdapter = new MyAdapter(this);
        myAdapter.setClickListener(this);


        recyclerView.setAdapter(myAdapter);
        myAdapter.checker(length_of_list, sp, gson);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                messageEditText.setText("");
                boolean flag = false;
                if(message.equals(""))
                {
                    toast.show();
                }
                else
                {
                    flag = true;
                    reserve(editor, flag, message, 0);
                }
            }
        });
    }

    public void reserve(SharedPreferences.Editor editor, boolean flag, String message, int pos){
        if(flag) {
            myAdapter.add_message(message);
            length_of_list += 1;
        }
        else
        {
            myAdapter.delete_message(pos);
            length_of_list -= 1;
        }
        editor.putInt("lst_length", length_of_list);
        String wjson = gson.toJson(myAdapter.myDataset);
        editor.putString("messages_lst", wjson);
        editor.apply();

    }

    @Override
    public void messageClick(View v, final int pos) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int poORne) {
                boolean flag = true;
                if(poORne == DialogInterface.BUTTON_POSITIVE){
                    flag = false;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    final SharedPreferences.Editor editor = sp.edit();
                    reserve(editor, flag, null, pos);
                    return;
                }
                else
                {
                    return;
                }
            }
        };


       dialogClick(dialogClickListener, v);

    }


    public void dialogClick(DialogInterface.OnClickListener dialogClickListener, View v ){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are You Sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("wrote_message", myAdapter.myDataset);
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getStringArrayList("wrote_message") != null)
        {
            myAdapter.myDataset.addAll(savedInstanceState.getStringArrayList("wrote_message"));
        }
    }
}



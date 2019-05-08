package com.image.get.selfchat;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements ClickOnMsg {

    int length_of_list;
    final Gson gson = new Gson();
    Button sendButton;
    EditText messageEditText;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    SharedPreferences sp;
    FirebaseFirestore db;
    SharedPreferences.Editor editor;

    CollectionReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = (Button) findViewById(R.id.send_btn);
        messageEditText = (EditText) findViewById(R.id.edit_text);
        recyclerView = (RecyclerView) findViewById(R.id.myRecycle);

        FirebaseApp.initializeApp(MainActivity.this);
        db = FirebaseFirestore.getInstance();
        reference = db.collection("myDatabase");


        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        final Toast toast = Toast.makeText(context, "you can't send an empty message, oh silly!", duration);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        final int length_of_list = sp.getInt("lst_length", 0);

        myAdapter = new MyAdapter(this, length_of_list, db, sp);
        myAdapter.setClickListener(this);

        new getFireBaseId().execute();


        recyclerView.setAdapter(myAdapter);

        if (length_of_list != 0){
            myAdapter.checker(gson);
        }
        else if (sp.getBoolean("first_launch", true))
        {
            new syncLocalToRemoteFireBase().execute();
            editor.putBoolean("first_launch", false);
            editor.apply();
        }

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
                if (message.equals("")) {
                    toast.show();
                } else {
                    new insertDataToFireBase().execute(message);
                }
            }
        });
    }


    public void saveEditedData() {
        editor.putInt("data_size", length_of_list);
        String wjson = gson.toJson(myAdapter.myDataset);
        editor.putString("sent_messages", wjson);
        editor.apply();
        myAdapter.notifyDataSetChanged();
    }




    @Override
    public void messageClick(View v, final int pos) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int poORne) {
                if (poORne == DialogInterface.BUTTON_POSITIVE) {
                    myAdapter.delete_message(pos);
                    saveEditedData();
                    myAdapter.notifyItemRemoved(pos);

                } else {
                    return;
                }
            }
        };

        dialogClick(dialogClickListener, v);

    }




    public void dialogClick(DialogInterface.OnClickListener dialogClickListener, View v) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are You Sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        myAdapter.myDataset = new ArrayList<MyAdapter.Messages>();
        reference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    return;
                }
                for(DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges())
                {
                    DocumentSnapshot documentSnapshot = documentChange.getDocument();
                    String id = documentSnapshot.getId();
                    boolean isDocumentDeleted = documentChange.getOldIndex() != -1;
                    boolean isDocumentAdded = documentChange.getNewIndex() != -1;
                    if (isDocumentDeleted)
                    {
                        for(int index = 0 ; index < myAdapter.myDataset.size(); index++)
                            if (myAdapter.myDataset.get(index).Id.equals(id)) {
                                myAdapter.delete_message(index);
                                saveEditedData();
                                myAdapter.notifyItemRemoved(index);
                                break;
                            }
                    }

                    else if(isDocumentAdded && !
                            documentSnapshot.getId().equals("V7HLvB57aGm5CE0qAeai"))
                    {
                        Map<String, Object> new_doc_data = documentSnapshot.getData();
                        String Id = new_doc_data.get("id")+"";
                        String content = new_doc_data.get("content")+"";
                        String timestamp = new_doc_data.get("timestamp")+"";
                        myAdapter.add_message(Id, timestamp, content);
                        saveEditedData();
                        myAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }




    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("wrote_message", messageEditText.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String wrote_message = savedInstanceState.getString("wrote_message");
        messageEditText.setText(wrote_message);
        myAdapter.checker(gson);
        myAdapter.notifyDataSetChanged();

    }


    private class getFireBaseId extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            myAdapter.getGlobalId();
            return null;
        }
    }

    private class insertDataToFireBase extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            myAdapter.addToRemoteFireBase(strings[0]);
            return null;
        }
    }

    public class syncLocalToRemoteFireBase extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            myAdapter.loadDataFromRemoteFireBase(sp, gson);
            return null;
        }
    }


}




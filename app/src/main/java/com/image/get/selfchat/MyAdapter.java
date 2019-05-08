package com.image.get.selfchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;





public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public ArrayList<Messages> myDataset;
    private LayoutInflater recycleInflater;
    private ClickOnMsg myMsgClick;
    public FirebaseFirestore db;
    private SharedPreferences sp;
    int data_size;

    int my_id = 0;

    public static final String TIME_FORMAT = "kk:mm";


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        TextView textView;
        public MyViewHolder(View v){
            super(v);
            textView = v.findViewById(R.id.textView);
            v.setOnLongClickListener(this);

;        }


        public boolean onLongClick(View v){
            if (myMsgClick != null) {
                myMsgClick.messageClick(v, getAdapterPosition());
            }

            return true;
        }


    }


    public MyAdapter(Context context, int size, FirebaseFirestore db, SharedPreferences other_sp){

        this.recycleInflater = LayoutInflater.from(context);
        this.myDataset = new ArrayList<Messages>();
        this.db = db;
        this.sp = other_sp;
        this.data_size = size;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = recycleInflater.inflate(R.layout.one_msg, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String str = myDataset.get(position).Content;
        holder.textView.setText(str);
    }

    @Override
    public int getItemCount() {
        if(myDataset == null)
        {
            myDataset = new ArrayList<>();
            return 0;
        }
        return myDataset.size();
    }


    public void add_message(String id, String timestamp, String message){
        this.myDataset.add(new Messages(id, timestamp, message));
        data_size += 1;
    }

    public void delete_message(int pos){
        new DeleteDataFromFireBase().execute(this.myDataset.get(pos).Id);
        this.myDataset.remove(pos);
        data_size -= 1;
    }



    void setClickListener(ClickOnMsg itemClick) {
        this.myMsgClick = itemClick;
    }


    public void checker(Gson gson) {
        String rjson = sp.getString("sent_messages", "");
        Type type = new TypeToken<List<Messages>>() {
        }.getType();
        this.myDataset = gson.fromJson(rjson, type);


    }

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
        return dateFormat.format(new Date());
    }


    public class Messages {
        public String Id, Timestamp, Content;

        public Messages(String Id, String Timestamp, String Content) {
            this.Content = Content;
            this.Timestamp = Timestamp;
            this.Id = Id;
        }
    }


    public void addToRemoteFireBase(final String message)
    {
        String currentTime = getTime();

        DocumentReference washingtonRef = db.collection("myDatabase").
                document("V7HLvB57aGm5CE0qAeai");

        washingtonRef
                .update("my_id", my_id + 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error updating document", e);
                    }
                });
        Map<String, Object> sent_message = new HashMap<>();
        int increment_id = my_id + 1;

        sent_message.put("content", message);
        sent_message.put("timestamp",currentTime);
        sent_message.put("id", increment_id);

        db.collection("myDatabase")
                .document(increment_id + "")
                .set(sent_message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(" ", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(" ", "Error writing document", e);
                    }
                });
        my_id++;

    }




    public void getGlobalId()
    {
        DocumentReference docRef = db.collection("myDatabase").
                document("V7HLvB57aGm5CE0qAeai");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final String id = document.getData().get("my_id") + "";
                        my_id = Integer.parseInt(id);
                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });
    }


    public void deleteDocument(String doc_id)
    {
        db.collection("myDatabase").document(doc_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(" ", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(" ", "Error deleting document", e);
                    }
                });
    }


    public void loadDataFromRemoteFireBase(final SharedPreferences sp, final Gson gson)
    {
        final ArrayList<Messages> d = new ArrayList<Messages>();
        db.collection("myDatabase")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id, timestamp, content;
                            Map<String, Object> one_message;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!document.getId().equals("V7HLvB57aGm5CE0qAeai"))
                                {
                                    one_message = document.getData();
                                    id = one_message.get("id") + "";
                                    timestamp = one_message.get("timestamp") + "";
                                    content = one_message.get("content") + "";
                                    d.add(new Messages(id, timestamp, content));
                                }
                            }

                            for (Messages m: d)
                                add_message(m.Id, m.Timestamp, m.Content);
                            checker(gson);

                        } else {
                            Log.d(" ", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private class DeleteDataFromFireBase extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            deleteDocument(strings[0]);
            return null;
        }
    }


}



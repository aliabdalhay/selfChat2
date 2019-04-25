package com.image.get.selfchat;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public ArrayList<String> myDataset;
    private LayoutInflater recycleInflater;
    private ClickOnMsg myMsgClick;


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


    public MyAdapter(Context context){
        this.recycleInflater = LayoutInflater.from(context);
        this.myDataset = new ArrayList<String>();
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = recycleInflater.inflate(R.layout.one_msg, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String str = myDataset.get(position);
        holder.textView.setText(str);
    }

    @Override
    public int getItemCount() {
        return myDataset.size();
    }


    public void add_message(String msg){
        this.myDataset.add(msg);
        this.notifyDataSetChanged();
    }

    public void delete_message(int pos){
        this.myDataset.remove(pos);
        this.notifyItemRemoved(pos);
    }


    void setClickListener(ClickOnMsg itemClick) {
        this.myMsgClick = itemClick;
    }

    public void checker(int length_of_list, SharedPreferences sp, Gson gson){

        if (length_of_list != 0)
        {
            String rjson = sp.getString("messages_lst", "");
            Type type = new TypeToken<List<String>>() {}.getType();
            this.myDataset = gson.fromJson(rjson, type);
        }

    }


}

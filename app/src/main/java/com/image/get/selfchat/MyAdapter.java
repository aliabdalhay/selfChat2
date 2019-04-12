package com.image.get.selfchat;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public ArrayList<String> myDataset;
    private LayoutInflater recycleInflater;



    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        public MyViewHolder(View v){
            super(v);
            textView = v.findViewById(R.id.textView);
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



}

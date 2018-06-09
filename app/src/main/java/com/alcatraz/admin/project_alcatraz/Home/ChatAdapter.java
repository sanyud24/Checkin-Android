package com.alcatraz.admin.project_alcatraz.Home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.List;


/**
 * Created by TAIYAB on 05-02-2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyView> {

    private List<String> message;
    private List<String> time;
    private List<Integer> sentorget;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    public static class MyView extends RecyclerView.ViewHolder {

        public TextView message;
        public TextView time;

        public MyView(View view, int viewType) {
            super(view);
            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                message = (TextView) view.findViewById(R.id.sent_message);
                time = view.findViewById(R.id.sent_time);
            } else {
                message = (TextView) view.findViewById(R.id.received_message);
                time = view.findViewById(R.id.recieved_time);
            }
        }
    }


    public ChatAdapter(List<String> horizontalList,List<String> time,List<Integer> sentorget) {
        this.message = horizontalList;
        this.sentorget=sentorget;
        this.time=time;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return sentorget.get(position);
    }
    @Override

    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new MyView(view,viewType);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new MyView(view,viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
        // Log.e("TAG",name.get(position));
        holder.message.setText(message.get(position));
        holder.time.setText(time.get(position));
    }

    @Override
    public int getItemCount()
    {
        return message.size();
    }

}
package com.example.mypermission.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypermission.ChatsModel;
import com.example.mypermission.R;
import java.util.ArrayList;

public class chatbotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChatsModel> chatsModelArrayList;
    private Context context;

    public chatbotAdapter(ArrayList<ChatsModel> chatsModelArrayList, Context context) {
        this.chatsModelArrayList = chatsModelArrayList;
        if (context != null) {
            this.context = context;
        } else {
            throw new IllegalArgumentException("Context cannot be null");
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbot_usermsg, parent, false);
                return new UserViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbot_response, parent, false);
                return new BotViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatsModel chatsModel = chatsModelArrayList.get(position);
        switch (chatsModel.getSender()) {
            case "User":
                ((UserViewHolder) holder).userTV.setText(chatsModel.getMessage());
                break;
            case "bot":
                ((BotViewHolder) holder).botMsgTV.setText(chatsModel.getMessage());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (chatsModelArrayList.get(position).getSender()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                throw new IllegalArgumentException("Invalid sender type");
        }
    }

    @Override
    public int getItemCount() {
        return chatsModelArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userTV;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userTV = itemView.findViewById(R.id.idTVUser);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView botMsgTV;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botMsgTV = itemView.findViewById(R.id.idTVBot);
        }
    }
}

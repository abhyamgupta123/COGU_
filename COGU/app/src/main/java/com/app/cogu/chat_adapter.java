package com.app.cogu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.List;


// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class chat_adapter extends RecyclerView.Adapter<chat_adapter.ViewHolder> {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView text;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super( itemView );

            text = (TextView) itemView.findViewById( R.id.text_message );

        }
    }

    private List<chat_messages_class> chat_messages;

    // Pass in the contact array into the constructor
    public chat_adapter(List<chat_messages_class> chats) {
        this.chat_messages = chats;
    }

    @Override
    public int getItemViewType(int position) {
        if (chat_messages.get(position).isMe()) {
            return R.layout.chat_me_layout;
        }else {
            return R.layout.chat_other_layout;
        }
    }

    @Override
    public chat_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false));
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(chat_adapter.ViewHolder holder, int position) {
        // Get the data model based on position
        chat_messages_class chats = chat_messages.get( position );

        // Set item views based on your views and data model
        TextView chat_text = holder.text;
        chat_text.setText( chats.getMessage() );
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return chat_messages.size();
    }
}



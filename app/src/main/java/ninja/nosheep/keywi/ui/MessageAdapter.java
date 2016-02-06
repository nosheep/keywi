package ninja.nosheep.keywi.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ninja.nosheep.keywi.R;
import ninja.nosheep.keywi.data.Conversation;

/**
 * Adapter for message RecyclerView
 *
 * @author David Söderberg
 * @since 2015-11-11
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {

    private final LayoutInflater inflater;
    private final Context context;
    private ArrayList<Conversation> conversationList = new ArrayList<>();
    private AdapterCallback mCallback;

    public MessageAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            mCallback = (AdapterCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AdapterCallback");
        }
        View view = inflater.inflate(R.layout.list_item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Conversation conv = conversationList.get(position);
        holder.addressTextView.setText(conv.getDisplayAddress());
        holder.bodyTextView.setText(conv.getLatestMessageBody());
        holder.dateTextView.setText(conv.getLatestMessageTime());
        holder.holderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onConversationClick(conversationList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public void addConversation(Conversation conversation) {
        conversationList.add(conversation);
        notifyItemInserted(conversationList.size());
    }

    public void addConversationList(ArrayList<Conversation> conversations) {
        int position = conversationList.size() + 1;
        conversationList.addAll(conversations);
        notifyItemInserted(position);
    }

    public void changeAddressText(int position, String newText) {
        conversationList.get(position).setDisplayAddress(newText);
        notifyItemChanged(position);
    }

    public void setConversationList(ArrayList<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    public ArrayList<Conversation> getConversationList() {
        return conversationList;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return conversationList.get(pos).getLatestMessageTime() + "";
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout holderLayout;
        private final TextView addressTextView;
        private final TextView bodyTextView;
        private final TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            holderLayout = (RelativeLayout) itemView.findViewById(R.id.message_list_view);
            addressTextView = (TextView) itemView.findViewById(R.id.message_address_text_view);
            bodyTextView = (TextView) itemView.findViewById(R.id.message_body_text_view);
            dateTextView = (TextView) itemView.findViewById(R.id.message_date_text_view);
        }
    }

    public interface AdapterCallback {
        void onConversationClick(Conversation conversation);
    }
}

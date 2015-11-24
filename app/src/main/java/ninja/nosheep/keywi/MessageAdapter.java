package ninja.nosheep.keywi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for message RecyclerView
 *
 * @author David Söderberg
 * @since 2015-11-11
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Conversation> conversationList = new ArrayList<>();

    public MessageAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.addressTextView.setText(conversationList.get(position).getDisplayAddress());
        holder.bodyTextView.setText(conversationList.get(position).getLatestMessageBody());
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView addressTextView;
        private TextView bodyTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            addressTextView = (TextView) itemView.findViewById(R.id.message_address_text_view);
            bodyTextView = (TextView) itemView.findViewById(R.id.message_body_text_view);
        }
    }
}

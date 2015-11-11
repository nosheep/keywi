package ninja.nosheep.keywi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for message RecyclerView
 *
 * @author David Söderberg
 * @since 2015-11-11
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private ArrayList<SMSObject> messageList;
    private AdapterCallback mCallback;
    private Context context;

    public interface AdapterCallback {
        void onMessageSelected(SMSObject smsObject);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public MessageAdapter(ArrayList<SMSObject> messageList) {
        this.messageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            mCallback = (AdapterCallback) parent.getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(parent.toString() + " must implement AdapterCallback.");
        }

        context = parent.getContext();

        View v = LayoutInflater.from(context)
                .inflate(R.layout.list_message, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ContactHandler contactHandler = new ContactHandler(context.getContentResolver());
        RelativeLayout messageListLayout = (RelativeLayout) holder.itemView.findViewById(R.id.message_list_view);

        TextView addressTextView = (TextView) holder.itemView.findViewById(R.id.message_address_text_view);
        addressTextView.setText(contactHandler.getContactNameFromNumber(messageList.get(position).getAddress()));

        TextView bodyTextView = (TextView) holder.itemView.findViewById(R.id.message_body_text_view);
        bodyTextView.setText(messageList.get(position).getMessageBody());

        TextView folderTextView = (TextView) holder.itemView.findViewById(R.id.message_folder_text_view);
        folderTextView.setText(messageList.get(position).getFolder().substring(0, 1).toUpperCase()
                + messageList.get(position).getFolder().substring(1));

        messageListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMessageSelected(messageList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}

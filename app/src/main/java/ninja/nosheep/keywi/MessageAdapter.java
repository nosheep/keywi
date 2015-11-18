package ninja.nosheep.keywi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter for message ListView
 *
 * @author David Söderberg
 * @since 2015-11-11
 */
public class MessageAdapter extends ArrayAdapter<Conversation> {

    private final Context context;

    public MessageAdapter (Context context) {
        super(context, R.layout.list_message);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final Conversation conversation;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_message, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.senderInfo = (TextView) convertView.findViewById(R.id.message_address_text_view);
            viewHolder.body = (TextView) convertView.findViewById(R.id.message_body_text_view);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        conversation = getItem(position);
        String displayName;
        displayName = conversation.getDisplayAddress().equals("")
                    ? conversation.getAddress()
                    : conversation.getDisplayAddress();
        viewHolder.senderInfo.setText(displayName);
        viewHolder.body.setText(conversation.getLatestMessageBody());
        return convertView;
    }

    private static class ViewHolder {
        TextView senderInfo;
        TextView body;
    }
}

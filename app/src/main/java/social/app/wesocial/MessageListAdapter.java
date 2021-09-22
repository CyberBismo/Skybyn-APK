package social.app.wesocial;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;


public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private final List<MessageListDataClass> MessageListDataClass;
    Functions functions = new Functions();
    Data data = new Data();


    @NonNull
    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_message, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.ViewHolder holder, int position) {
        MessageListDataClass messageListDataClass = MessageListDataClass.get(position);
        holder.username.setText(messageListDataClass.getUserName());
        holder.date.setText(functions.convertUnixToDateAndTime(Long.valueOf(messageListDataClass.getDate())));
        holder.content.setText(messageListDataClass.getContent());
        functions.loadProfilePictureDrawableThumb(messageListDataClass.getAvatarLink(), holder.imgMessageProfilePicture);

        switch (messageListDataClass.getOnline()) {
            case "1":
                holder.imgMessageOnlineStatus.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.online_icon));
                break;
            default:
                holder.imgMessageOnlineStatus.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.offline_icon));
                break;
        }


        holder.displayMsgCardView.setOnClickListener(view ->
                {
                    HashMap<String, String> postData = new HashMap<>();
                    postData.put("userID", Frontpage.userID);
                    postData.put("friendID", messageListDataClass.getFriendID());

                    NetworkController networkController = new NetworkController(holder.itemView.getContext(),
                            new NetworkController.IResult() {
                                @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                                @Override
                                public void notifySuccess(String response) throws JSONException {
                                    Timber.i(response);
                                }

                                @Override
                                public void notifyError(VolleyError error) {

                                }
                            });

                    networkController.PostMethod(data.showMessages_API, postData);
                }

        );

    }
    public MessageListAdapter(List<MessageListDataClass> MessageListDataClass) {
        this.MessageListDataClass = MessageListDataClass;

    }

    @Override
    public int getItemCount() {
        return MessageListDataClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView username, content, date;
        ImageView imgMessageProfilePicture, imgMessageOnlineStatus;
        CardView displayMsgCardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.txtMessageUsername);
            content = itemView.findViewById(R.id.txtMessageContent);
            date = itemView.findViewById(R.id.txtMessageDate);
            imgMessageOnlineStatus = itemView.findViewById(R.id.imgMessageOnlineStatus);
            imgMessageProfilePicture = itemView.findViewById(R.id.imgMessageProfilePicture);
            displayMsgCardView = itemView.findViewById(R.id.displayMessageCardView);


        }
    }
}
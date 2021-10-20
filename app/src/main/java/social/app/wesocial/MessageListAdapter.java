package social.app.wesocial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;


public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private final List<MessageListDataClass> MessageListDataClass;
    private final Activity activity;
    Functions functions;
    Data data = new Data();
    public static String chatMessagejson = "";


    @NonNull
    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_message, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.ViewHolder holder, int position) {
        functions= new Functions(holder.itemView.getContext());
        MessageListDataClass messageListDataClass = MessageListDataClass.get(position);
        holder.username.setText(messageListDataClass.getUserName());
        holder.date.setText(functions.convertUnixToDateAndTime(Long.valueOf(messageListDataClass.getDate())));
        holder.content.setText(messageListDataClass.getContent());
        functions.loadProfilePictureDrawableThumb(messageListDataClass.getAvatarLink(), holder.imgMessageProfilePicture);

        ExecutorService service = Executors.newFixedThreadPool(4);
        service.submit(new Runnable() {
            public void run() {
                //Load Messages
                loadAllMessages(messageListDataClass.getFriendID());            }
        });


        holder.displayMsgCardView.setTag(chatMessagejson);

        switch (messageListDataClass.getOnline()) {
            case "1":
                holder.imgMessageOnlineStatus.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.online_icon));
                break;
            default:
                holder.imgMessageOnlineStatus.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.offline_icon));
                break;
        }

        Drawable drawable = holder.imgMessageOnlineStatus.getDrawable();

        holder.imgMessageProfilePicture.setOnClickListener(view -> functions.loadTimeLineUserProfile(messageListDataClass.getFriendID(),activity,holder.itemView.getContext()));


        holder.displayMsgCardView.setOnClickListener(view ->{
            //Global reference to current chat user ID
             Frontpage.current_chat_user = messageListDataClass.getFriendID();
             //
            Fragment fragmentShowFullChat = social.app.wesocial.showFullChat.newInstance(holder.username.getText().toString().toUpperCase(), drawable,messageListDataClass.getAvatarLink(),messageListDataClass.getFriendID(),chatMessagejson);
            functions.LoadFragment(fragmentShowFullChat,"fullchat",(Activity) holder.itemView.getContext(),false,true);

    });

    }
    public MessageListAdapter(List<MessageListDataClass> MessageListDataClass,Activity activity) {
        this.MessageListDataClass = MessageListDataClass;
        this.activity = activity;

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



    public void loadAllMessages(String friendID) {

        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("friendID", friendID);

        NetworkController networkController = new NetworkController(activity.getApplicationContext(),
                new NetworkController.IResult() {
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        Timber.i(response);
                        if (functions.isJsonArray(response)) {
                            chatMessagejson = response;
                        }
                    }

                    @Override
                    public void notifyError(VolleyError error) {

                    }
                });

        networkController.PostMethod(data.showFullMessages_API, postData);
    }
}
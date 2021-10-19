package social.app.wesocial;
import android.app.Activity;
import android.content.ClipData;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    public static final int messageSenderMe = 0;
    public  static final int messageSenderUser = 1;
    View itemView;
    public static Float datetextSize = 9f;
    private final List<ChatMessageListDataClass> ChatMessageListDataClass;

    Activity activity;

    Functions functions;

    public ChatMessageAdapter(List<ChatMessageListDataClass> ChatMessageListDataClass,Activity activity) {
        this.ChatMessageListDataClass = ChatMessageListDataClass;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case  messageSenderUser:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_incoming_chat_message, parent, false);
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_outgoing_chat_message, parent, false);
                break;
        }
        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        Boolean messageSender = ChatMessageListDataClass.get(position).getUsername().equalsIgnoreCase(Frontpage.username);
        if (messageSender) {
            return messageSenderMe;
        } else {
            return messageSenderUser;
        }
        //return position;
    }


    @Override
    public void onBindViewHolder(@NonNull ChatMessageAdapter.ViewHolder holder, int position) {
        functions = new Functions(holder.itemView.getContext());


        ChatMessageListDataClass chatMessageListDataClass = ChatMessageListDataClass.get(position);
        switch (holder.getItemViewType()) {
            case messageSenderMe:
                holder.myMessage.setTag(chatMessageListDataClass.getMsgID());
                holder.myMessage.setText(chatMessageListDataClass.getContent());
                if (position == 0 || position == 1 || position == ChatMessageListDataClass.size() - 1) {
                    holder.myMessageProfilePicture.setVisibility(View.VISIBLE);
                    functions.loadProfilePictureDrawableThumb(chatMessageListDataClass.getAvatarLink(), holder.myMessageProfilePicture);
                } else {
                    holder.myMessageProfilePicture.setVisibility(View.INVISIBLE);
                    //holder.myMessage.setBackgroundResource(R.drawable.rounded_corner_main);
                }


                //IF SENDING , DONT SHOW DATE  YET
                if (chatMessageListDataClass.getDate().equals("sending")) {
                    holder.myMessageDate.setText(chatMessageListDataClass.getDate());
                } else {
                    holder.myMessageDate.setText(functions.convertUnixToDateAndTime(Long.valueOf(chatMessageListDataClass.getDate())));
                }
                holder.myMessageDate.setTextSize(datetextSize);

                holder.myMessageProfilePicture.setOnClickListener(view -> {
                    functions.loadTimeLineUserProfile(Frontpage.userID,activity ,holder.itemView.getContext());
                });
                break;


            case messageSenderUser:
                holder.otherUserMessage.setTag(chatMessageListDataClass.getMsgID());
                holder.otherUserMessage.setOnClickListener(view -> {
                    functions.loadTimeLineUserProfile(chatMessageListDataClass.getFriendID(), activity ,holder.itemView.getContext());
                });

                holder.otherUserMessage.setText(chatMessageListDataClass.getContent());
                if (position == 0 || position == 1 || position == ChatMessageListDataClass.size() - 1) {
                    holder.otherUserProfilePicture.setVisibility(View.VISIBLE);
                    functions.loadProfilePictureDrawableThumb(chatMessageListDataClass.getAvatarLink(), holder.otherUserProfilePicture);
                } else {
                    holder.otherUserProfilePicture.setVisibility(View.INVISIBLE);
                    //holder.otherUserMessage.setBackgroundResource(R.drawable.rounded_corner_dark);
                }

                //IF SENDING , DON'T SHOW DATE  YET
                if (chatMessageListDataClass.getDate().equals("sending")) {
                    holder.otherUserDate.setText(chatMessageListDataClass.getDate());
                } else {
                    holder.otherUserDate.setText(functions.convertUnixToDateAndTime(Long.valueOf(chatMessageListDataClass.getDate())));
                }
                holder.otherUserMessage.setOnClickListener(view -> {
                   Timber.i("Clicked");;
                });

                holder.otherUserMessage.setOnLongClickListener(view -> {
                    return false;
                });

                holder.otherUserDate.setTextSize(datetextSize);
                break;
        }




    }


    @Override
    public int getItemCount() {
        return ChatMessageListDataClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView myMessage, myMessageDate;
        TextView otherUserMessage, otherUserDate;
        ImageView myMessageProfilePicture, otherUserProfilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.txtChatMessageOut);
            myMessageDate = itemView.findViewById(R.id.txtChatMessageOutDate);
            myMessageProfilePicture = itemView.findViewById(R.id.imgChatMessageOutProfilePicture);

            otherUserMessage = itemView.findViewById(R.id.txtChatMessageIn);
            otherUserDate = itemView.findViewById(R.id.txtChatMessageInDate);
            otherUserProfilePicture = itemView.findViewById(R.id.imgChatMessageInProfilePicture);
        }
    }
}

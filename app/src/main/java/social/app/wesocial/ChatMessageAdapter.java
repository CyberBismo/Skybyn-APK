package social.app.wesocial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    private final List<ChatMessageListDataClass> ChatMessageListDataClass;

    Functions functions = new Functions();

    public static Integer viewTypeMe = 0;
    public static Float datetextSize = 9f;
    public static Integer viewTypeUser = 1;

    public ChatMessageAdapter(List<ChatMessageListDataClass> ChatMessageListDataClass) {
        this.ChatMessageListDataClass = ChatMessageListDataClass;
    }

    @NonNull
    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == viewTypeMe) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_chat_message_out, parent, false);
        } else if (viewType == viewTypeUser) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_chat_message_in, parent, false);
        }
        assert itemView != null;
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        boolean iSentThisMessage = ChatMessageListDataClass.get(position).getUsername().equalsIgnoreCase(Frontpage.loginUsername);
        Integer type;
        if (iSentThisMessage) {
            type = viewTypeMe;
        } else {
            type = viewTypeUser;

        }

        return type;

    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageAdapter.ViewHolder holder, int position) {
        ChatMessageListDataClass chatMessageListDataClass = ChatMessageListDataClass.get(position);
        if (holder.getItemViewType() == viewTypeMe) {
            //REPLACE ::like with image
            holder.myMessage.setText(chatMessageListDataClass.getContent());
            //CHECK POSITION TO SET SPEECH BUBBLE
            if (position == 0 || position == 1 || position == ChatMessageListDataClass.size() - 1) {
                holder.myMessageImageView.setVisibility(View.VISIBLE);
                functions.loadProfilePictureDrawableThumb(chatMessageListDataClass.getAvatarLink(), holder.myMessageImageView);
                holder.outgoingChatConstraintLayout.setBackgroundResource(R.drawable.outgoing_chat_bubble);
            } else {
                holder.myMessageImageView.setVisibility(View.INVISIBLE);
                holder.outgoingChatConstraintLayout.setBackgroundResource(R.color.main_colour);
            }


            //IF SENDING , DONT SHOW DATE  YET
            if (chatMessageListDataClass.getDate().equals("sending")) {
                holder.myMessageDate.setText(chatMessageListDataClass.getDate());
            } else {
                holder.myMessageDate.setText(functions.convertUnixToDateAndTime(Long.valueOf(chatMessageListDataClass.getDate())));
            }


            holder.myMessageDate.setTextSize(datetextSize);

        }


        //IF CHAT IS FROM OTHER USER
        if (holder.getItemViewType() == viewTypeUser) {
            if (position == 0 || position == 1 || position == ChatMessageListDataClass.size() - 1) {
                holder.otherUserImageView.setVisibility(View.VISIBLE);
                functions.loadProfilePictureDrawableThumb(chatMessageListDataClass.getAvatarLink(), holder.otherUserImageView);
                holder.incomingChatConstraintLayout.setBackgroundResource(R.drawable.incoming_chat_bubble);
            } else {
                holder.otherUserImageView.setVisibility(View.INVISIBLE);
                holder.incomingChatConstraintLayout.setBackgroundResource(R.color.dark_gray_2);
            }



            //IF SENDING , DONT SHOW DATE  YET
            if (chatMessageListDataClass.getDate().equals("sending")) {
                holder.otherUserDate.setText(chatMessageListDataClass.getDate());
            } else {
                holder.otherUserDate.setText(functions.convertUnixToDateAndTime(Long.valueOf(chatMessageListDataClass.getDate())));
            }

            /**if (chatMessageListDataClass.getContent().equals("::like")) {
                holder.otherUserMessage.setBackgroundResource(R.drawable.thumbs_up);
                //holder.otherUserMessage.setHeight(550);
                holder.otherUserMessage.setText("");
            } else {

             holder.otherUserMessage.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
             **/
            holder.otherUserMessage.setText(chatMessageListDataClass.getContent());


            holder.otherUserDate.setTextSize(datetextSize);

        }
    }

    @Override
    public int getItemCount() {
        return ChatMessageListDataClass.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView myMessage, myMessageDate;
        TextView otherUserMessage, otherUserDate;
        ImageView myMessageImageView, otherUserImageView;
        ConstraintLayout incomingChatConstraintLayout, outgoingChatConstraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.txtChatMessageOut);
            myMessageDate = itemView.findViewById(R.id.txtChatMessageOutDate);
            myMessageImageView = itemView.findViewById(R.id.imgChatMessageOutProfilePicture);
            incomingChatConstraintLayout = itemView.findViewById(R.id.IncomingchatConstraintLayout);
            outgoingChatConstraintLayout = itemView.findViewById(R.id.outgoingChatConstraintLayout);

            otherUserMessage = itemView.findViewById(R.id.txtChatMessageIn);
            otherUserDate = itemView.findViewById(R.id.txtChatMessageInDate);
            otherUserImageView = itemView.findViewById(R.id.imgChatMessageInProfilePicture);
        }
    }
}

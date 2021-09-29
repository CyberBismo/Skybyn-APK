package social.app.wesocial;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import timber.log.Timber;


public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    private final List<ChatMessageListDataClass> ChatMessageListDataClass;

    Functions functions = new Functions();

    public static Integer viewTypeMe=0;
    public static Float datetextSize=9f;
    public static Integer viewTypeUser=1;

    @NonNull
    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == viewTypeMe) {
             itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_chat_message_out, parent, false);
        }else if(viewType == viewTypeUser){
             itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_chat_message_in, parent, false);
        }
        assert itemView != null;
        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        boolean iSentThisMessage = ChatMessageListDataClass.get(position).getUsername().equalsIgnoreCase(Frontpage.loginUsername);
        Integer type;
        if (iSentThisMessage){
            type = viewTypeMe;
        }else{
            type = viewTypeUser;

        }
        Timber.i(String.valueOf(type));
        return type;

    }


    @Override
    public void onBindViewHolder(@NonNull ChatMessageAdapter.ViewHolder holder, int position) {
        ChatMessageListDataClass chatMessageListDataClass = ChatMessageListDataClass.get(position);
         if (holder.getItemViewType() == viewTypeMe) {
             holder.myMessage.setText(chatMessageListDataClass.getContent());
             holder.myMessageDate.setText(functions.convertUnixToDateAndTime(Long.valueOf(chatMessageListDataClass.getDate())));
             holder.myMessageDate.setTextSize(datetextSize);
             functions.loadProfilePictureDrawableThumb(chatMessageListDataClass.getAvatarLink(),holder.myMessageImageView);
         }else if (holder.getItemViewType() == viewTypeUser){
             holder.otherUserMessage.setText(chatMessageListDataClass.getContent());
             holder.otherUserDate.setText(functions.convertUnixToDateAndTime(Long.valueOf(chatMessageListDataClass.getDate())));
             holder.otherUserDate.setTextSize(datetextSize);
             holder.otherUserMessage.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
             functions.loadProfilePictureDrawableThumb(chatMessageListDataClass.getAvatarLink(),holder.otherUserImageView);
         }
    }

    public ChatMessageAdapter(List<ChatMessageListDataClass> ChatMessageListDataClass) { this.ChatMessageListDataClass = ChatMessageListDataClass;}

    @Override
    public int getItemCount() {
        return ChatMessageListDataClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView myMessage, myMessageDate;
        TextView otherUserMessage, otherUserDate;
        ImageView myMessageImageView,otherUserImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.txtChatMessageOut);
            myMessageDate = itemView.findViewById(R.id.txtChatMessageOutDate);
            myMessageImageView = itemView.findViewById(R.id.imgChatMessageOutProfilePicture);

            otherUserMessage = itemView.findViewById(R.id.txtChatMessageIn);
            otherUserDate = itemView.findViewById(R.id.txtChatMessageInDate);
            otherUserImageView = itemView.findViewById(R.id.imgChatMessageInProfilePicture);
        }
    }
}

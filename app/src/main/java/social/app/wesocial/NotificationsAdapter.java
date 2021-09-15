package social.app.wesocial;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private final List<NotificationDataClass> NotificationDataClass;
    Functions functions = new Functions();
    Data data = new Data();

    public enum NotificationTypes {friend_request, chat, group, page, updates, system, comment}

    ;
    public static final String notificationType_friendRequest = "friend_request";
    public static final String notificationType_message = "message";
    public static final String notificationType_group = "group";
    public static final String notificationType_page = "page";
    public static final String notificationType_updates = "updates";
    public static final String notificationType_system = "system";
    public static final String notificationType_comment = "comment";


    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_notifications, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {

        NotificationDataClass notificationDataClass = NotificationDataClass.get(position);
        holder.Title.setText(notificationDataClass.getTitle());
        holder.Content.setText(notificationDataClass.getContent());
        holder.date.setText(notificationDataClass.getDate());
        holder.imgNotificationSender.setTag(R.integer.integer_key_zero, notificationDataClass.getAvatarLink());

        switch (notificationDataClass.getType()) {

            case notificationType_message:

                functions.loadNotificationThumb(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.png_chat), holder.imgNotificationSender,false);
                break;

            case notificationType_updates:
                functions.loadNotificationThumb(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.png_updates), holder.imgNotificationSender,false);
                break;

            case notificationType_system:
                functions.loadNotificationThumb(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_system_update_white_24dp), holder.imgNotificationSender,false);
                break;

            case notificationType_page:
                functions.loadNotificationThumb(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.page), holder.imgNotificationSender,false);
                break;

            case notificationType_group:
                functions.loadNotificationThumb(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.groups), holder.imgNotificationSender,false);
                break;

            case notificationType_comment:
                functions.loadNotificationThumb(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.comment), holder.imgNotificationSender,false);
                break;

            case notificationType_friendRequest:
                holder.btnNotiAcceptFriendRequest.setVisibility(View.VISIBLE);
                holder.Title.setText(holder.itemView.getContext().getString(R.string.new_friend_request));
                holder.Title.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.light_main_colour));
                functions.loadNotificationThumb(notificationDataClass.getAvatarLink().toString(), holder.imgNotificationSender,true);
                break;

            default:
                holder.Title.setText(notificationDataClass.getTitle());
                holder.btnNotiAcceptFriendRequest.setVisibility(View.INVISIBLE);
        }


        if (notificationDataClass.getRead().equals("0")) {
            Typeface typeface = ResourcesCompat.getFont(holder.Title.getContext(), R.font.nexabold);
            holder.Content.setTypeface(typeface);
            holder.notificationCardView.setCardBackgroundColor(ContextCompat.getColor(holder.notificationCardView.getContext(), R.color.dark_gray_2));
            holder.Title.setTypeface(typeface);

        } else {
            holder.Content.setTextColor(ContextCompat.getColor(holder.Content.getContext(), R.color.light_gray));
            holder.Title.setTextColor(ContextCompat.getColor(holder.Content.getContext(), R.color.light_gray));
        }


    }

    public NotificationsAdapter(List<NotificationDataClass> NotificationDataClass) {
        this.NotificationDataClass = NotificationDataClass;
    }

    @Override
    public int getItemCount() {
        return NotificationDataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Title, Content, date, type, ID;
        ImageView imgNotificationSender;
        Button btnNotiAcceptFriendRequest;
        CardView notificationCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.lblNotificationTitle);
            Content = itemView.findViewById(R.id.lblNotificationContent);
            date = itemView.findViewById(R.id.lblNotificationDate);
            imgNotificationSender = itemView.findViewById(R.id.imgNotificationSender);
            notificationCardView = itemView.findViewById(R.id.notificationCardView);
            btnNotiAcceptFriendRequest= itemView.findViewById(R.id.btnNotiAcceptFriendRequest);

        }
    }
}
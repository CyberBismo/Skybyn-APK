package social.app.wesocial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private final List <NotificationList> NotificationList;
    Functions functions =new Functions();

    @NonNull
    @Override

    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_notifications, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {
        NotificationList notificationList =NotificationList.get(position);

        holder.Title.setText(notificationList.getTitle());
        holder.Content.setText(notificationList.getContent());
        holder.date.setText(notificationList.getDate());
        holder.type.setText(notificationList.getType());
        functions.loadNotificationThumb(notificationList.getAvatarLink(),holder.imgNotificationSender);

    }
    public NotificationsAdapter( List <NotificationList> NotificationList){
        this.NotificationList = NotificationList;
    }

    @Override
    public int getItemCount() {
        return NotificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Title,Content,date,type,ID;
        ImageView imgNotificationSender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.lblNotificationTitle);
            ID = itemView.findViewById(R.id.lblNotificationID);
            Content = itemView.findViewById(R.id.lblNotificationContent);
            date = itemView.findViewById(R.id.lblNotificationDate);
            type = itemView.findViewById(R.id.lblNotificationType);
            imgNotificationSender = itemView.findViewById(R.id.imgNotificationSender);

        }
    }
}
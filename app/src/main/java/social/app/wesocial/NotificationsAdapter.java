package social.app.wesocial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List <NotificationList> NotificationList;
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
        functions.loadProfilePictureThumb(notificationList.getAvatarLink(),holder.imgNotificationSender);

    }
    public NotificationsAdapter( List <NotificationList> NotificationList){
        this.NotificationList = NotificationList;
    }

    @Override
    public int getItemCount() {
        return NotificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Title,Content,date;
        ImageView imgNotificationSender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.lblNotificationTitle);
            Content = itemView.findViewById(R.id.lblnotificationdetails);
            date = itemView.findViewById(R.id.lblnotificationdate);
            imgNotificationSender = itemView.findViewById(R.id.imgNotificationSender);

        }
    }
}
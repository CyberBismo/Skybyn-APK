package social.app.wesocial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class TimelinePostsAdapter extends RecyclerView.Adapter<TimelinePostsAdapter.ViewHolder> {
    private final List <NotificationDataClass> NotificationDataClass;
    Functions functions =new Functions();
    @NonNull
    @Override
    public TimelinePostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_timeline, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelinePostsAdapter.ViewHolder holder, int position) {
        NotificationDataClass notificationDataClass = NotificationDataClass.get(position);
        holder.Title.setText(notificationDataClass.getTitle());
        holder.Content.setText(notificationDataClass.getContent());
        holder.date.setText(notificationDataClass.getDate());
        holder.type.setText(notificationDataClass.getType());
        functions.loadNotificationThumb(notificationDataClass.getAvatarLink(),holder.imgNotificationSender);

    }
    public TimelinePostsAdapter(List <NotificationDataClass> NotificationDataClass){
        this.NotificationDataClass = NotificationDataClass;
    }

    @Override
    public int getItemCount() {
        return NotificationDataClass.size();
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
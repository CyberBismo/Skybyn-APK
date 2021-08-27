package social.app.wesocial;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.net.IDN;
import java.util.HashMap;
import java.util.List;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private final List <NotificationDataClass> NotificationDataClass;
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
        NotificationDataClass notificationDataClass = NotificationDataClass.get(position);
        holder.Title.setText(notificationDataClass.getTitle());
        holder.Content.setText(notificationDataClass.getContent());
        HashMap<String,String>  notiData = new HashMap<>();
        notiData.put("notiID",notificationDataClass.getID());
        notiData.put("notiType",notificationDataClass.getType());
        holder.Content.setTag(R.integer.integer_key, notiData);
        holder.date.setText(notificationDataClass.getDate());



        if (notificationDataClass.getRead().equals("0")) {
            holder.imgNotificationSender.setImageDrawable(ContextCompat.getDrawable(holder.imgNotificationSender.getContext(), R.drawable.unread_notification));
            Typeface typeface  = ResourcesCompat.getFont(holder.Title.getContext(),R.font.nexabold);
            holder.Content.setTypeface(typeface);
            holder.notificationCardView.setCardBackgroundColor(ContextCompat.getColor(holder.notificationCardView.getContext(),R.color.main_colour));
            holder.Title.setTypeface(typeface);

        }else{
            holder.imgNotificationSender.setImageDrawable(ContextCompat.getDrawable(holder.imgNotificationSender.getContext(), R.drawable.read_notification));
            holder.Content.setTextColor(ContextCompat.getColor(holder.Content.getContext(),R.color.light_gray));
            holder.Title.setTextColor(ContextCompat.getColor(holder.Content.getContext(),R.color.light_gray));

        }

    }
    public NotificationsAdapter( List <NotificationDataClass> NotificationDataClass){
        this.NotificationDataClass = NotificationDataClass;
    }

    @Override
    public int getItemCount() {
        return NotificationDataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Title,Content,date,type,ID;
        ImageView imgNotificationSender;
        CardView notificationCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.lblNotificationTitle);
            Content = itemView.findViewById(R.id.lblNotificationContent);
            date = itemView.findViewById(R.id.lblNotificationDate);
            imgNotificationSender = itemView.findViewById(R.id.imgNotificationSender);
            notificationCardView = itemView.findViewById(R.id.notificationCardView);

        }
    }
}
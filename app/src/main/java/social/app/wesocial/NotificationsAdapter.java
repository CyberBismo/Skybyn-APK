package social.app.wesocial;

import android.app.Activity;
import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private final List<NotificationDataClass> NotificationDataClass;
    Functions functions = new Functions();
    Data data = new Data();


    public static final String notificationType_friendRequest = "friend_request";
    public static final String notificationType_message = "message";
    public static final String notificationType_group = "group";
    public static final String notificationType_page = "page";
    public static final String notificationType_updates = "updates";
    public static final String notificationType_system = "system";
    public static final String notificationType_comment = "comment";


    @Override
    public int getItemViewType(int position) {
        {
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_notifications, parent, false);
        return new ViewHolder(itemView);
    }


    public void updateNotificationReadStatus(String notificationID, Context context){
        HashMap<String,String> postData = new HashMap<>();
        postData.put("notiID",notificationID);

        NetworkController networkController = new NetworkController(context, new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                Timber.i(response);
            }

            @Override
            public void notifyError(VolleyError error) {

            }
        });

        networkController.PostMethod(data.read_notification_Api,postData);

    }
    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {

        NotificationDataClass notificationDataClass = NotificationDataClass.get(position);
        holder.Title.setText(notificationDataClass.getTitle());
        holder.Content.setText(notificationDataClass.getContent());
        holder.date.setText(notificationDataClass.getDate());

            holder.notificationCardView.setOnClickListener(view -> updateNotificationReadStatus(notificationDataClass.getID(),holder.itemView.getContext()));

        holder.btnNotiAcceptFriendRequest.setOnClickListener(view -> {
            Fragment friendFragment = Friends.newInstance(data.accept_friend_action);
            functions.LoadFragment(friendFragment, data.accept_friend_action, (Activity) holder.itemView.getContext(),false,false);
            updateNotificationReadStatus(notificationDataClass.getID(),holder.itemView.getContext());

        });

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
                functions.loadNotificationThumb(notificationDataClass.getAvatarLink(), holder.imgNotificationSender,true);
                break;

            default:
                functions.loadNotificationThumb(notificationDataClass.getAvatarLink(), holder.imgNotificationSender,true);
                holder.Title.setText(notificationDataClass.getTitle());
                holder.btnNotiAcceptFriendRequest.setVisibility(View.INVISIBLE);
        }


        if (notificationDataClass.getRead().equals("0")) {
            Typeface typeface = ResourcesCompat.getFont(holder.Title.getContext(), R.font.nexabold);
            holder.Content.setTypeface(typeface);
            holder.Title.setTypeface(typeface);
        } else {
            holder.Content.setTextColor(ContextCompat.getColor(holder.Content.getContext(), R.color.black));
            holder.Title.setTextColor(ContextCompat.getColor(holder.Content.getContext(), R.color.black));
        }


    }

    public NotificationsAdapter( List<NotificationDataClass> NotificationDataClass) {
        this.NotificationDataClass = NotificationDataClass;


    }

    @Override
    public int getItemCount() {
        return NotificationDataClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView Title, Content, date;
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
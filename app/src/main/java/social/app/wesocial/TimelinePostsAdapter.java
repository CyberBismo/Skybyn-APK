package social.app.wesocial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


class TimelinePostsAdapter extends RecyclerView.Adapter<TimelinePostsAdapter.ViewHolder> {

    private List <TimelineDataClass>  TimelineDataClass;
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
        TimelineDataClass timelineDataClass = TimelineDataClass.get(position);
        holder.txtUsername.setText(timelineDataClass.getUsername());
        holder.txtUsername.setTag(timelineDataClass.getUserID());
        holder.txtTimelineContent.setText(timelineDataClass.getContent());
        holder.txtTimelineContent.setTag(timelineDataClass.getPostID());
        holder.txtTimelineDate.setText(timelineDataClass.getDate());
        holder.txtTimelineLikes.setText(timelineDataClass.getLikes());
        holder.txtTimelineCommentsCount.setText(timelineDataClass.getComments_count());
          functions.loadProfilePictureThumb(timelineDataClass.getAvatarLink(),holder.imgTimelinePostPicture);

    }
    public void TimelinePostsAdapter(List <TimelineDataClass> TimelineDataClass){
        this.TimelineDataClass = TimelineDataClass ;
    }

    @Override
    public int getItemCount() {
        return TimelineDataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtUsername,txtTimelineContent, txtTimelineDate, txtTimelineLikes
                , txtTimelineCommentsCount;
        ImageView imgTimelinePostPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTimelineContent = itemView.findViewById(R.id.txtTimelineContent);
            txtUsername = itemView.findViewById(R.id.txtTimelinePostUsername);
            txtTimelineDate = itemView.findViewById(R.id.txtTimelinePostDate);
            txtTimelineLikes = itemView.findViewById(R.id.txtTimelinePostLikes);
            txtTimelineCommentsCount = itemView.findViewById(R.id.txtTimelinePostComments);
            imgTimelinePostPicture = itemView.findViewById(R.id.imgTimelinePostProfilePicture);

        }
    }
}
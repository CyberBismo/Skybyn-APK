package social.app.wesocial;

import static social.app.wesocial.R.id;
import static social.app.wesocial.R.layout;
import static social.app.wesocial.R.string;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.like.LikeButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


class TimelinePostsAdapter extends RecyclerView.Adapter<TimelinePostsAdapter.ViewHolder> {

    private final List<TimelineDataClass> TimelineDataClass;
    Functions functions = new Functions();
    Data data = new Data();
    RecyclerView timelineRecyclerView;
    public Boolean userLikedPost = false;
    String postID;


    public TimelinePostsAdapter(List<TimelineDataClass> timelineDataClass) {
        TimelineDataClass = timelineDataClass;
    }

    @NonNull
    @Override
    public TimelinePostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout.display_timeline, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull TimelinePostsAdapter.ViewHolder holder, int position) {
        TimelineDataClass timelineDataClass = TimelineDataClass.get(position);
        holder.txtUsername.setText(timelineDataClass.getUsername());
        holder.txtUsername.setTag(timelineDataClass.getUserID());
        holder.txtTimelineDate.setText(timelineDataClass.getDate());
        holder.txtTimelineLikes.setText(timelineDataClass.getLikes());
        holder.txtTimelineCommentsCount.setText(timelineDataClass.getComments_count());
        holder.txtTimelineContent.setText(timelineDataClass.getContent());
        holder.txtTimelineContent.setTag(timelineDataClass.getPostID());
        holder.imgTimelinePostLike.setTag(timelineDataClass.getiLike());

        if (holder.imgTimelinePostLike.getTag().toString().equals("0")) {
            holder.imgTimelinePostLike.setLiked(false);
        } else {
            holder.imgTimelinePostLike.setLiked(true);

        }

        String userID = Frontpage.userID;
        //If the timeLine Post is by Me!
        if (holder.txtUsername.getTag().toString().equals(userID)) {
            holder.ImgTimelinePostDelete.setVisibility(View.VISIBLE);
        }

        functions.loadProfilePictureThumb(timelineDataClass.getAvatarLink(), holder.imgTimelinePostPicture);

        holder.ImgTimelinePostDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.itemView.getContext());
            alertDialogBuilder.setMessage(holder.itemView.getContext().getString(string.deletePOST));
            alertDialogBuilder.setPositiveButton(holder.itemView.getContext().getString(string.yes_delete),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            postID = holder.txtTimelineContent.getTag().toString();
                            HashMap<String, String> postData = new HashMap<>();
                            postData.put("userID", Frontpage.userID);
                            postData.put("postID", postID);
                            NetworkController networkController = new NetworkController(holder.itemView.getContext(), new NetworkController.IResult() {
                                @Override
                                public void notifySuccess(String response) throws JSONException {
                                    if (functions.isJsonObject(response)) {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String responseCode = jsonObject.get("responseCode").toString();
                                        String message = jsonObject.get("message").toString();

                                        if (responseCode.equals("1")) {
                                            //REMOVE FROM RECYCLERVIEW
                                            Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                                            TimelineDataClass.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            notifyItemRangeChanged(holder.getAdapterPosition(), TimelineDataClass.size());
                                        }

                                        if (responseCode.equals("0")) {
                                            Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }

                                @Override
                                public void notifyError(VolleyError error) {

                                }
                            });

                            networkController.PostMethod(data.deletePost_Api, postData);
                        }
                    });


            alertDialogBuilder.setNegativeButton(holder.itemView.getContext().getString(string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            AlertDialog alertDialog = alertDialogBuilder.create();
            /*Button deleteButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            deleteButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),color.red));
            deleteButton.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),color.white));*/
            alertDialog.show();
        });

        //like post


        holder.imgTimelinePostLike.setOnClickListener(new View.OnClickListener() {
            public void sendLike() {


                HashMap<String, String> postData = new HashMap<>();
                postID = holder.txtTimelineContent.getTag().toString();
                postData.put("userID", Frontpage.userID);
                postData.put("postID", postID);

                NetworkController networkController = new NetworkController(holder.itemView.getContext(), new NetworkController.IResult() {
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        Log.i("response", response);
                        if (functions.isJsonObject(response)) {
                            JSONObject jsonObject = new JSONObject(response);
                            String responseCode = jsonObject.get("responseCode").toString();
                            String message = jsonObject.get("message").toString();
                            String likes = "";

                            if (responseCode.equals("1")) {
                                likes = jsonObject.get("likes").toString();
                                holder.imgTimelinePostLike.setLiked(true);
                                holder.imgTimelinePostLike.setTag("1");



                            }
                            if (responseCode.equals("2")) {
                                likes = jsonObject.get("likes").toString();
                                holder.imgTimelinePostLike.setLiked(false);
                                holder.imgTimelinePostLike.setTag("0");
                            }

                            holder.txtTimelineLikes.setText(likes);
                            Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();


                        }
                    }

                    @Override
                    public void notifyError(VolleyError error) {
                        Toast.makeText(holder.itemView.getContext(), holder.itemView.getContext().getString(string.network_something_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
                networkController.PostMethod(data.like_Api, postData);


            }
            @Override
            public void onClick(View view) {
                int Likes = Integer.valueOf(holder.txtTimelineLikes.getText().toString()) ;
                if (holder.imgTimelinePostLike.getTag().toString().equals("0")) {
                    holder.imgTimelinePostLike.setLiked(true);
                    holder.txtTimelineLikes.setText(String.valueOf(Likes+1));

                } else {
                    holder.imgTimelinePostLike.setLiked(false);
                    holder.txtTimelineLikes.setText(String.valueOf(Likes-1));
                }
                sendLike();
            }
        });

    }


    @Override
    public int getItemCount() {
        return TimelineDataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtUsername, txtTimelineContent, txtTimelineDate, txtTimelineLikes, txtTimelineCommentsCount;
        ImageView imgTimelinePostPicture, ImgTimelinePostDelete, imgTimelinePostComment;
        LikeButton imgTimelinePostLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTimelineContent = itemView.findViewById(id.txtTimelinePostContent);
            txtUsername = itemView.findViewById(id.txtTimelinePostUsername);
            txtTimelineDate = itemView.findViewById(id.txtTimelinePostDate);
            txtTimelineLikes = itemView.findViewById(id.txtTimelinePostLikes);
            txtTimelineCommentsCount = itemView.findViewById(id.txtTimelinePostComments);
            imgTimelinePostPicture = itemView.findViewById(id.imgTimelinePostProfilePicture);
            ImgTimelinePostDelete = itemView.findViewById(id.imgTimelinePostDelete);
            imgTimelinePostLike = itemView.findViewById(id.imgTimelinePostLike);
            imgTimelinePostComment = itemView.findViewById(id.imgTimelinePostComment);


        }
    }
}
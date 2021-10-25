package social.app.wesocial;

import static social.app.wesocial.R.id;
import static social.app.wesocial.R.layout;
import static social.app.wesocial.R.string;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.like.LikeButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;


class TimelinePostsAdapter extends RecyclerView.Adapter<TimelinePostsAdapter.ViewHolder> {

    private ArrayList<TimelineDataClass> TimelineDataClass;
    Functions functions;
    Data data = new Data();
    String postID;
    Integer PostLength = data.maxPostDisplayLength;
    public boolean isUserTimeline;
    String userID;
    Activity activity;


    public TimelinePostsAdapter(ArrayList<TimelineDataClass> timelineDataClass,Boolean isUserTimeline,String userID,Activity activity) {
        TimelineDataClass = timelineDataClass;
        this.isUserTimeline = isUserTimeline;
        this.activity = activity;
        this.userID = userID;
    }

    @NonNull
    @Override
    public TimelinePostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout.display_timeline, parent, false);

        return new ViewHolder(itemView);
    }


    public String trimValue(String value) {
        if (value.length() == 4) {
            //It is one thousand
            value = value.substring(0, 1).concat(".").concat(value.substring(1, 2).concat("k"));
        } else if (value.length() == 5) {
            value = value.substring(0, 2).concat(".").concat(value.substring(2, 3).concat("k"));
        } else if (value.length() == 6) {
            value = value.substring(0, 3).concat(".").concat(value.substring(3, 4)).concat("k");
        } else if (value.length() == 7) {
            value = value.substring(0, 1).concat(".").concat(value.substring(1, 2)).concat("M");
        } else if (value.length() == 8) {
            value = value.substring(0, 2).concat(".").concat(value.substring(2, 3)).concat("M");
        }
        return value;
    }


    @Override
    public void onBindViewHolder(@NonNull TimelinePostsAdapter.ViewHolder holder, int position) {
        functions = new Functions(holder.itemView.getContext());
        TimelineDataClass timelineDataClass = TimelineDataClass.get(position);
        holder.txtUsername.setText(timelineDataClass.getUsername());
        holder.txtUsername.setTag(timelineDataClass.getUserID());
        holder.txtTimelineDate.setText(timelineDataClass.getDate());
        holder.txtTimelineLikes.setText(trimValue(timelineDataClass.getLikes()));
        holder.txtTimelineCommentsCount.setText(trimValue(timelineDataClass.getComments_count()));
        functions.loadProfilePictureDrawableThumb(timelineDataClass.getAvatarLink(), holder.imgTimelinePostPicture);

        if (timelineDataClass.getContent().length() > PostLength) {
            holder.txtTimelineContent.setText(Html.fromHtml(timelineDataClass.getContent().substring(0, PostLength) + "<font color=\"#005DC1\"> <u>View More</u></font>"));
            //holder.txtTimelineContent.setText(Html.fromHtml(timelineDataClass.getContent().substring(0, PostLength)+"..."));
        } else {

            holder.txtTimelineContent.setText(timelineDataClass.getContent());
        }

        holder.txtTimelineContent.setTag(timelineDataClass.getPostID());
        holder.txtTimelineDate.setTag(timelineDataClass.getContent());
        holder.imgTimelinePostLike.setTag(timelineDataClass.getiLike());
        holder.imgTimelinePostPicture.setTag(timelineDataClass.getAvatarLink());


        holder.timelineMainLayout.setOnClickListener(view -> {
            Intent i = new Intent(holder.itemView.getContext(), showFullPost.class);
            HashMap<String, Object> timeLinePostDetails = new HashMap<>();
            timeLinePostDetails.put("content", holder.txtTimelineDate.getTag().toString());
            timeLinePostDetails.put("likes", holder.txtTimelineLikes.getText().toString());
            timeLinePostDetails.put("ilike", holder.imgTimelinePostLike.getTag().toString());
            timeLinePostDetails.put("username", holder.txtUsername.getText().toString());
            timeLinePostDetails.put("userID", holder.txtUsername.getTag().toString());
            timeLinePostDetails.put("postID", holder.txtTimelineContent.getTag().toString());
            timeLinePostDetails.put("date", holder.txtTimelineDate.getText().toString());
            timeLinePostDetails.put("avatarLink", holder.imgTimelinePostPicture.getTag().toString());
            timeLinePostDetails.put("comments_count", holder.txtTimelineCommentsCount.getText().toString());

            i.putExtra( "timeLinePostDetails", timeLinePostDetails);
            Timber.i(timeLinePostDetails.toString());
            holder.itemView.getContext().startActivity(i);
        });


        //Profile PICTURE click
        if (!this.isUserTimeline) {
            holder.imgTimelinePostPicture.setOnClickListener(view -> functions.loadTimeLineUserProfile(holder.txtUsername.getTag().toString(),activity,holder.itemView.getContext().getApplicationContext()));
            holder.txtUsername.setOnClickListener(view -> holder.imgTimelinePostPicture.callOnClick());
        }
        holder.imgTimelinePostLike.setLiked(holder.imgTimelinePostLike.getTag().toString().equals("1"));

        //If the timeLine Post is by Me!
        if (holder.txtUsername.getTag().toString().equals(Frontpage.userID)) {
            holder.txtTimelinePostDelete.setVisibility(View.VISIBLE);
        } else {
            holder.txtTimelinePostDelete.setVisibility(View.INVISIBLE);
        }



        //Timeline Delete Post

        holder.txtTimelinePostDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.itemView.getContext(), R.style.AlertDialogCustom);
            alertDialogBuilder.setIcon(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.warning));
            alertDialogBuilder.setMessage(holder.itemView.getContext().getString(string.deletePOST));
            alertDialogBuilder.setTitle(holder.itemView.getContext().getString(R.string.deletePostTitle));
            alertDialogBuilder.setPositiveButton(holder.itemView.getContext().getString(string.yes_delete),
                    (dialog, arg1) -> {

                        TimelineDataClass.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), TimelineDataClass.size());

                        postID = holder.txtTimelineContent.getTag().toString();
                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("userID", userID);
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
                    });


            alertDialogBuilder.setNegativeButton(holder.itemView.getContext().getString(string.no), (dialog, which) -> dialog.dismiss());


            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
        //like post
        holder.imgTimelinePostLike.setOnClickListener(new View.OnClickListener() {
            public void sendLike() {

                HashMap<String, String> postData = new HashMap<>();
                postID = holder.txtTimelineContent.getTag().toString();
                postData.put("userID", userID);
                postData.put("postID", postID);

                NetworkController networkController = new NetworkController(holder.itemView.getContext(), new NetworkController.IResult() {
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        Timber.i(response);
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
                //holder.imgTimelinePostLike.animate();
                int Likes = Integer.parseInt(holder.txtTimelineLikes.getText().toString());
                if (holder.imgTimelinePostLike.getTag().toString().equals("0")) {
                    holder.imgTimelinePostLike.setLiked(true);
                    holder.txtTimelineLikes.setText(String.valueOf(Likes + 1));
                } else {
                    holder.imgTimelinePostLike.setLiked(false);
                    if (Integer.parseInt(holder.txtTimelineLikes.getText().toString()) > 0)
                        holder.txtTimelineLikes.setText(String.valueOf(Likes - 1));
                }


                sendLike();
            }
        });
    }


    @Override
    public int getItemCount() {
        return TimelineDataClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtTimelineContent, txtTimelineDate, txtTimelineLikes, txtTimelineCommentsCount;
        ImageView imgTimelinePostPicture, imgTimelinePostComment;
        LikeButton imgTimelinePostLike;
        CardView cardView;
        TextView txtTimelinePostDelete,txtTimelineShowMore;
        ConstraintLayout timelineMainLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(id.postTimelineCardView);
            txtTimelineContent = itemView.findViewById(id.txtShowTimelinePostContent);
            txtUsername = itemView.findViewById(id.txtShowTimelinePostUsername);
            txtTimelineDate = itemView.findViewById(id.txtShowTimelinePostDate);
            txtTimelineLikes = itemView.findViewById(id.txtShowTimelinePostLikes);
            txtTimelineCommentsCount = itemView.findViewById(id.txtShowTimelinePostComments);
            imgTimelinePostPicture = itemView.findViewById(id.imgShowTimelinePostProfilePicture);
            txtTimelinePostDelete = itemView.findViewById(id.txtTimelinePostDelete);
            imgTimelinePostLike = itemView.findViewById(id.btnShowTimelinePostLike);
            imgTimelinePostComment = itemView.findViewById(id.imgShowTimelinePostComment);
            timelineMainLayout = itemView.findViewById(id.timelineMainConstraintLayout);



        }
    }
}
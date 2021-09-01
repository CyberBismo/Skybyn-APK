package social.app.wesocial;

import static social.app.wesocial.R.id;
import static social.app.wesocial.R.layout;
import static social.app.wesocial.R.string;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
    String postID;
    Frontpage frontpage;
    Integer PostLength = data.maxPostDisplayLength;

    public TimelinePostsAdapter(List<TimelineDataClass> timelineDataClass) {
        TimelineDataClass = timelineDataClass;
    }

    @NonNull
    @Override
    public TimelinePostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout.display_timeline, parent, false);

        return new ViewHolder(itemView);
    }


    public  String trimValue(String value) {
        if (value.length() == 4 || value.length() == 7 ) {
            //It is one thousand
            value = value.substring(0,1).concat(".").concat(value.substring(1,2).concat("k"));
        }else if(value.length() == 5){
            value = value.substring(0,2).concat(".").concat(value.substring(2,3).concat("k"));;
        }else if(value.length() == 6){
            value = value.substring(0,3).concat(".").concat(value.substring(3,4)).concat("k");
        } else if(value.length() == 7){
        value = value.substring(0,1).concat(".").concat(value.substring(1,2)).concat("M");
        } else if(value.length() == 8) {
            value = value.substring(0, 2).concat(".").concat(value.substring(2, 3)).concat("M");
        }else{
            value = value;
        }
        return value;
    }

    @Override
    public void onBindViewHolder(@NonNull TimelinePostsAdapter.ViewHolder holder, int position) {
        TimelineDataClass timelineDataClass = TimelineDataClass.get(position);

        holder.txtUsername.setText(timelineDataClass.getUsername());
        holder.txtUsername.setTag(timelineDataClass.getUserID());
        holder.txtTimelineDate.setText(timelineDataClass.getDate());
        holder.txtTimelineLikes.setText(trimValue(timelineDataClass.getLikes()));
        holder.txtTimelineCommentsCount.setText(trimValue(timelineDataClass.getComments_count()));

        if (timelineDataClass.getContent().length() > PostLength){
            holder.txtTimelineContent.setText(Html.fromHtml(timelineDataClass.getContent().substring(0, PostLength)+"<font color=\"#005DC1\"> <u>View More</u></font>"));
        }else{
            holder.txtTimelineContent.setText( timelineDataClass.getContent());
        }

        holder.txtTimelineContent.setTag(timelineDataClass.getPostID());
        holder.txtTimelineDate.setTag(timelineDataClass.getContent());
        holder.imgTimelinePostLike.setTag(timelineDataClass.getiLike().toString());
        holder.imgTimelinePostPicture.setTag(timelineDataClass.getAvatarLink());

        holder.txtTimelineContent.setOnClickListener(view -> {
            Intent i = new Intent(holder.itemView.getContext(),showFullPost.class);
            HashMap<String,Object> timeLinePostDetails= new HashMap<>();

            timeLinePostDetails.put("content",holder.txtTimelineDate.getTag().toString());
            timeLinePostDetails.put("comments_count",holder.txtTimelineCommentsCount.getText().toString());
            timeLinePostDetails.put("likes",holder.txtTimelineLikes.getText().toString());
            timeLinePostDetails.put("ilike",holder.imgTimelinePostLike.getTag().toString());
            timeLinePostDetails.put("username",holder.txtUsername.getText().toString());
            timeLinePostDetails.put("userID",holder.txtUsername.getTag().toString());
            timeLinePostDetails.put("postID",holder.txtTimelineContent.getTag().toString());
            timeLinePostDetails.put("date",holder.txtTimelineDate.getText().toString());
            timeLinePostDetails.put("avatarLink", holder.imgTimelinePostPicture.getTag().toString());
            timeLinePostDetails.put("comments_count", holder.txtTimelineCommentsCount.getText().toString());
            i.putExtra("timeLinePostDetails",timeLinePostDetails);
            holder.itemView.getContext().startActivity(i);

        });

        if (holder.imgTimelinePostLike.getTag().toString().equals("0")) {
            holder.imgTimelinePostLike.setLiked(false);
        } else {
            holder.imgTimelinePostLike.setLiked(true);

        }

        String userID = Frontpage.userID;
        //If the timeLine Post is by Me!
        if (holder.txtUsername.getTag().toString().equals(userID)) {
            holder.txtTimelinePostDelete.setVisibility(View.VISIBLE);
        }

        functions.loadProfilePictureThumb(timelineDataClass.getAvatarLink(), holder.imgTimelinePostPicture);
        holder.txtTimelinePostDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.itemView.getContext());
            alertDialogBuilder.setMessage(holder.itemView.getContext().getString(string.deletePOST));
            alertDialogBuilder.setTitle(holder.itemView.getContext().getString(R.string.deletePostTitle));
            alertDialogBuilder.setPositiveButton(holder.itemView.getContext().getString(string.yes_delete),
                    (dialog, arg1) -> {
                        TimelineDataClass.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), TimelineDataClass.size());

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


            alertDialogBuilder.setNegativeButton(holder.itemView.getContext().getString(string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            AlertDialog alertDialog = alertDialogBuilder.create();
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
                //holder.imgTimelinePostLike.animate();
                int Likes = Integer.valueOf(holder.txtTimelineLikes.getText().toString());
                if (holder.imgTimelinePostLike.getTag().toString().equals("0")) {
                    holder.imgTimelinePostLike.setLiked(true);
                    holder.txtTimelineLikes.setText(String.valueOf(Likes + 1));
                } else {
                    holder.imgTimelinePostLike.setLiked(false);
                    if (Integer.valueOf(holder.txtTimelineLikes.getText().toString()) > 0)
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtTimelineContent, txtTimelineDate, txtTimelineLikes, txtTimelineCommentsCount;
        ImageView imgTimelinePostPicture, imgTimelinePostComment;
        LikeButton imgTimelinePostLike;
        CardView cardView;
        TextView  txtTimelinePostDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(id.displayTimelineCardView);
            txtTimelineContent = itemView.findViewById(id.txtShowTimelinePostContent);
            txtUsername = itemView.findViewById(id.txtShowTimelinePostUsername);
            txtTimelineDate = itemView.findViewById(id.txtShowTimelinePostDate);
            txtTimelineLikes = itemView.findViewById(id.txtShowTimelinePostLikes);
            txtTimelineCommentsCount = itemView.findViewById(id.txtShowTimelinePostComments);
            imgTimelinePostPicture = itemView.findViewById(id.imgShowTimelinePostProfilePicture);
            txtTimelinePostDelete = itemView.findViewById(id.txtTimelinePostDelete);
            imgTimelinePostLike = itemView.findViewById(id.imgShowTimelinePostLike);
            imgTimelinePostComment = itemView.findViewById(id.imgShowTimelinePostComment);


        }
    }
}
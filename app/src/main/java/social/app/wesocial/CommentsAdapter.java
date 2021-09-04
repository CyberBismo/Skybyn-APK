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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.like.LikeButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private final List<CommentDataClass> CommentDataClass;
    Functions functions = new Functions();
    Data data = new Data();
    String postID;
    Frontpage frontpage;
    Integer PostLength = data.maxPostDisplayLength;

    public CommentsAdapter(List<CommentDataClass> commentDataClass) {
        CommentDataClass = commentDataClass;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout.displaycomments, parent, false);

        return new ViewHolder(itemView);
    }


    public String trimValue(String value) {
        if (value.length() == 4 || value.length() == 7) {
            //It is one thousand
            value = value.substring(0, 1).concat(".").concat(value.substring(1, 2).concat("k"));
        } else if (value.length() == 5) {
            value = value.substring(0, 2).concat(".").concat(value.substring(2, 3).concat("k"));
            ;
        } else if (value.length() == 6) {
            value = value.substring(0, 3).concat(".").concat(value.substring(3, 4)).concat("k");
        } else if (value.length() == 7) {
            value = value.substring(0, 1).concat(".").concat(value.substring(1, 2)).concat("M");
        } else if (value.length() == 8) {
            value = value.substring(0, 2).concat(".").concat(value.substring(2, 3)).concat("M");
        } else {
            value = value;
        }
        return value;
    }


    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        CommentDataClass commentDataClass = CommentDataClass.get(position);

        holder.txtCommentUsername.setText(commentDataClass.getUsername());
        holder.txtCommentUsername.setTag(commentDataClass.getUserID());
        holder.txtCommentDate.setText(commentDataClass.getDate());
        holder.txtCommentLikes.setText(trimValue(commentDataClass.getLikes()));
        holder.txtCommentContent.setTag(commentDataClass.getCommentID());
        holder.txtCommentContent.setText(commentDataClass.getContent());
        holder.btnCommentLike.setTag(commentDataClass.getiLike());
        holder.imgCommentPicture.setTag(commentDataClass.getAvatarLink());

        holder.txtCommentContent.setOnClickListener(view -> {

        });

        if (holder.btnCommentLike.getTag().toString().equals("0")) {
            holder.btnCommentLike.setLiked(false);
        } else {
            holder.btnCommentLike.setLiked(true);

        }

        String userID = Frontpage.userID;
        //If the timeLine Post is by Me!
        if (holder.txtCommentUsername.getTag().toString().equals(userID)) {
            holder.txtCommentDelete.setVisibility(View.VISIBLE);
        }

        functions.loadProfilePictureThumb(commentDataClass.getAvatarLink(), holder.imgCommentPicture);

        holder.txtCommentDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.itemView.getContext());
            alertDialogBuilder.setMessage(holder.itemView.getContext().getString(string.deleteComment));
            alertDialogBuilder.setTitle(holder.itemView.getContext().getString(string.deleteCommentTitle));
            alertDialogBuilder.setPositiveButton(holder.itemView.getContext().getString(string.yes_delete),
                    (dialog, arg1) -> {

                        postID = holder.txtCommentContent.getTag().toString();
                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("userID", Frontpage.userID);
                        postData.put("commentID", holder.txtCommentContent.getTag().toString());
                        Log.i("commentID",holder.txtCommentContent.getTag().toString());
                        NetworkController networkController = new NetworkController(holder.itemView.getContext(), new NetworkController.IResult() {
                            @Override
                            public void notifySuccess(String response) throws JSONException {
                                if (functions.isJsonObject(response)) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String responseCode = jsonObject.get("responseCode").toString();
                                    String message = jsonObject.get("message").toString();

                                    if (responseCode.equals("1")) {
                                        //REMOVE FROM RECYCLERVIEW
                                        CommentDataClass.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(), CommentDataClass.size());
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

                        networkController.PostMethod(data.deleteComment_Api, postData);
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
        holder.btnCommentLike.setOnClickListener(new View.OnClickListener() {

            public void sendLike() {
                HashMap<String, String> postData = new HashMap<>();
                String commentID = holder.txtCommentContent.getTag().toString();
                postID = holder.txtCommentContent.getTag().toString();
                postData.put("userID", Frontpage.userID);
                postData.put("postID", postID);
                postData.put("commentID", commentID);

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
                                holder.btnCommentLike.setLiked(true);
                                holder.btnCommentLike.setTag("1");
                            }

                            if (responseCode.equals("2")) {
                                likes = jsonObject.get("likes").toString();
                                holder.btnCommentLike.setLiked(false);
                                holder.btnCommentLike.setTag("0");
                            }
                            holder.txtCommentLikes.setText(likes);
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
                int Likes = Integer.valueOf(holder.txtCommentLikes.getText().toString());
                if (holder.btnCommentLike.getTag().toString().equals("0")) {
                    holder.btnCommentLike.setLiked(true);
                    holder.txtCommentLikes.setText(String.valueOf(Likes + 1));
                } else {
                    holder.btnCommentLike.setLiked(false);
                    if (Integer.valueOf(holder.txtCommentLikes.getText().toString()) > 0)
                        holder.txtCommentLikes.setText(String.valueOf(Likes - 1));
                }


                sendLike();
            }
        });
    }


    @Override
    public int getItemCount() {
        return CommentDataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCommentUsername, txtCommentContent, txtCommentDate, txtCommentLikes;
        ImageView imgCommentPicture;
        LikeButton btnCommentLike;
        CardView cardView;
        TextView txtCommentDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(id.displayCommentCardView);
            txtCommentContent = itemView.findViewById(id.txtCommentContent);
            txtCommentUsername = itemView.findViewById(id.txtCommentUsername);
            txtCommentDate = itemView.findViewById(id.txtCommentDate);
            txtCommentLikes = itemView.findViewById(id.txtCommentLikes);
            imgCommentPicture = itemView.findViewById(id.imgCommentProfilePicture);
            txtCommentDelete = itemView.findViewById(id.txtCommentDelete);
            btnCommentLike = itemView.findViewById(id.btnCommentLike);


        }
    }


}
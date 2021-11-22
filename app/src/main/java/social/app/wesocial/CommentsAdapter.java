package social.app.wesocial;


import static social.app.wesocial.R.id;
import static social.app.wesocial.R.layout;
import static social.app.wesocial.R.string;

import android.app.Activity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.like.LikeButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private final List<CommentDataClass> CommentDataClass;
    private final Activity activity;
    Functions functions;
    Data data = new Data();
    String postID;
    String userID;


    public CommentsAdapter(List<CommentDataClass> commentDataClass, String userID, Activity activity) {
        CommentDataClass = commentDataClass;
        this.userID = userID;
        this.activity = activity;
        functions = new Functions(activity.getApplicationContext());
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout.display_comments, parent, false);
        return new ViewHolder(itemView);
    }

    public String trimValue(String value) {
        if (value.length() == 4 || value.length() == 7) {
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
        } else {
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
        //GET COMMENT_ID
        holder.txtCommentContent.setTag(R.integer.commentIDTag, commentDataClass.getCommentID());
        holder.txtCommentContent.setText(commentDataClass.getContent());
        //GET POST_ID
        holder.txtCommentContent.setTag(R.integer.commentsPostIDTag, commentDataClass.getPostID());
        holder.btnCommentLike.setTag(commentDataClass.getiLike());
        holder.imgCommentPicture.setTag(commentDataClass.getAvatarLink());

        Linkify.addLinks(holder.txtCommentContent, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);

        //If the comment is by Me!
        if (commentDataClass.getUserID().equals(Frontpage.userID)) {
            holder.txtCommentDelete.setVisibility(View.VISIBLE);
            holder.txtCommentEdit.setVisibility(View.VISIBLE);
        } else {
            holder.txtCommentDelete.setVisibility(View.INVISIBLE);
            holder.txtCommentEdit.setVisibility(View.INVISIBLE);
        }

        holder.imgCommentPicture.setOnClickListener(view -> functions.loadTimeLineUserProfile(commentDataClass.getUserID(), activity, holder.itemView.getContext()));

        holder.txtCommentEdit.setOnClickListener(view -> {
            LayoutInflater li = LayoutInflater.from(holder.itemView.getContext());
            View editLayout = li.inflate(R.layout.editdialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    holder.itemView.getContext(), R.style.AlertDialogCustom);
            // set alert_dialog.xml to alertdialog builder
            alertDialogBuilder.setView(editLayout);

            final EditText txtContent = editLayout.findViewById(id.etUserInput);
            txtContent.setText(holder.txtCommentContent.getText().toString());
            txtContent.setSelection(txtContent.length());

            // set dialog message
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton(holder.itemView.getContext().getString(string.save),
                    (dialog, id) -> {

                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("userID", userID);
                        postData.put("postID", holder.txtCommentContent.getTag(R.integer.commentsPostIDTag).toString());
                        postData.put("commentID", holder.txtCommentContent.getTag(R.integer.commentIDTag).toString());
                        postData.put("content", txtContent.getText().toString());


                        NetworkController networkController = new NetworkController(holder.itemView.getContext(), new NetworkController.IResult() {
                            @Override
                            public void notifySuccess(String response) throws JSONException {
                                Log.i("response", response);

                                if (!functions.isJsonObject(response)) {
                                    Toast.makeText(holder.itemView.getContext(), holder.itemView.getContext().getString(string.something_wrong), Toast.LENGTH_SHORT).show();
                                }

                                if (functions.isJsonObject(response)) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String responseCode = jsonObject.get("responseCode").toString();
                                    String message = jsonObject.get("message").toString();
                                    String content = jsonObject.get("content").toString();
                                    if (responseCode.equals(data.requestSuccessful)) {
                                        holder.txtCommentContent.setText(content);
                                        Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                                    }

                                    if (responseCode.equals("0")) {
                                        Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                            @Override
                            public void notifyError(VolleyError error) {
                                //functions.showSnackBar(holder.itemView.getContext().getString(string.network_something_wrong),holder.itemView.findViewById(android.R.id.content), holder.itemView.getContext());
                            }
                        });
                        Toast.makeText(holder.itemView.getContext(), holder.itemView.getContext().getString(R.string.updating_comment), Toast.LENGTH_SHORT).show();
                        networkController.PostMethod(data.editComment_API, postData);


                        //SAVE EDIT
                    });
            alertDialogBuilder.setNegativeButton("Cancel",
                    (dialog, id) -> dialog.cancel());

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


        });// get alert_dialog.xml view

        holder.txtCommentContent.setOnClickListener(view -> {
        });

        holder.btnCommentLike.setLiked(!holder.btnCommentLike.getTag().toString().equals("0"));

        functions.loadProfilePictureDrawableThumb(commentDataClass.getAvatarLink(), holder.imgCommentPicture);

        holder.txtCommentDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.itemView.getContext(), R.style.AlertDialogCustom);
            alertDialogBuilder.setIcon(ContextCompat.getDrawable(holder.txtCommentContent.getContext(), R.drawable.warning));
            alertDialogBuilder.setMessage(holder.itemView.getContext().getString(string.deleteComment));
            alertDialogBuilder.setTitle(holder.itemView.getContext().getString(string.deleteCommentTitle));
            alertDialogBuilder.setNegativeButton(holder.itemView.getContext().getString(R.string.no), (dialog, which) -> dialog.dismiss());
            alertDialogBuilder.setPositiveButton(holder.itemView.getContext().getString(R.string.yes_delete),
                    (dialog, arg1) -> {
                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("userID", userID);
                        postData.put("postID", holder.txtCommentContent.getTag(R.integer.commentsPostIDTag).toString());
                        postData.put("commentID", holder.txtCommentContent.getTag(R.integer.commentIDTag).toString());


                        NetworkController networkController = new NetworkController(holder.itemView.getContext(), new NetworkController.IResult() {
                            @Override
                            public void notifySuccess(String response) throws JSONException {
                                Log.i("response", response);
                                if (functions.isJsonObject(response)) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String responseCode = jsonObject.get("responseCode").toString();
                                    String message = jsonObject.get("message").toString();
                                    if (responseCode.equals(data.requestSuccessful)) {
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

                        networkController.PostMethod(data.deleteComment_API, postData);
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialogBuilder.show();

        });

        ////like post///
        holder.btnCommentLike.setOnClickListener(new View.OnClickListener() {

            public void sendLike() {
                HashMap<String, String> postData = new HashMap<>();
                String commentID = holder.txtCommentContent.getTag(R.integer.commentIDTag).toString();
                postID = holder.txtCommentContent.getTag(R.integer.commentsPostIDTag).toString();
                postData.put("userID", userID);
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

                            if (responseCode.equals(data.requestSuccessful)) {
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
                networkController.PostMethod(data.like_API, postData);
            }

            @Override
            public void onClick(View view) {
                //holder.imgTimelinePostLike.animate();
                int Likes = Integer.parseInt(holder.txtCommentLikes.getText().toString());
                if (holder.btnCommentLike.getTag().toString().equals("0")) {
                    holder.btnCommentLike.setLiked(true);
                    holder.txtCommentLikes.setText(String.valueOf(Likes + 1));
                } else {
                    holder.btnCommentLike.setLiked(false);
                    if (Integer.parseInt(holder.txtCommentLikes.getText().toString()) > 0)
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCommentUsername, txtCommentContent, txtCommentDate, txtCommentLikes, txtCommentEdit;
        ImageView imgCommentPicture;
        LikeButton btnCommentLike;
        CardView cardView;
        TextView txtCommentDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(id.displayCommentCardView);
            txtCommentContent = itemView.findViewById(id.txtCommentContent);
            txtCommentEdit = itemView.findViewById(id.txtCommentEdit);
            txtCommentUsername = itemView.findViewById(id.txtCommentUsername);
            txtCommentDate = itemView.findViewById(id.txtCommentDate);
            txtCommentLikes = itemView.findViewById(id.txtCommentLikes);
            imgCommentPicture = itemView.findViewById(id.imgPagePicture);
            txtCommentDelete = itemView.findViewById(id.txtCommentDelete);
            btnCommentLike = itemView.findViewById(id.btnCommentLike);


        }
    }

}
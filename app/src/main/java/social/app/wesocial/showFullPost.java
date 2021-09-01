package social.app.wesocial;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.like.LikeButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class showFullPost extends AppCompatActivity {
    TextView txtShowTimelinePostUsername;
    TextView txtShowTimelinePostDate, txtShowTimelinePostContent, txtShowTimelinePostLikes, txtShowTimelinePostComments;
    ImageView imgShowTimelinePostProfilePicture;
    Functions functions = new Functions();
    Data data = new Data();
    Button btnSendTimelineComment;
    TextView txtPostComment, txtShowTimelinePostEdit, txtShowTimelinePostDelete;
    LottieAnimationView lottie;
    NetworkController networkController;
    LikeButton btnShowtimePostLike;
    String postID, posterUserID, postUsername, postContent, postLikes, postCommentsCount, userLikedPost, postDate;
    HashMap<String, Object> timelinePostDetails = new HashMap<>();


    private void sendLike() {

        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("postID", postID);
        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
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
                        btnShowtimePostLike.setLiked(true);
                        btnShowtimePostLike.setTag("1");
                        userLikedPost = "1";
                    }

                    if (responseCode.equals("2")) {
                        likes = jsonObject.get("likes").toString();
                        btnShowtimePostLike.setLiked(false);
                        btnShowtimePostLike.setTag("0");
                        userLikedPost = "0";
                    }
                    txtShowTimelinePostLikes.setText(likes);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        networkController.PostMethod(data.like_Api, postData);


    }

    public void getAllViewsbyID() {
        txtShowTimelinePostUsername = findViewById(R.id.txtShowTimelinePostUsername);
        txtShowTimelinePostDate = findViewById(R.id.txtShowTimelinePostDate);
        imgShowTimelinePostProfilePicture = findViewById(R.id.imgShowTimelinePostProfilePicture);
        txtShowTimelinePostContent = findViewById(R.id.txtShowTimelinePostContent);
        txtShowTimelinePostLikes = findViewById(R.id.txtShowTimelinePostLikes);
        txtShowTimelinePostComments = findViewById(R.id.txtShowTimelinePostComments);
        txtShowTimelinePostDelete = findViewById(R.id.txtShowTimelinePostDelete);
        txtShowTimelinePostEdit = findViewById(R.id.txtShowTielinePostEdit);
        btnSendTimelineComment = findViewById(R.id.btnSendTimelineComment);
        txtPostComment = findViewById(R.id.txtShowPostComment);
        lottie = findViewById(R.id.showTimelinePostProgressView);
        btnShowtimePostLike = findViewById(R.id.btnShowTimelinePostLike);
    }

    void verifyIfIamPoster() {
        //CHECK to see if Delete and Edit option should be visible
        if (posterUserID.equals(Frontpage.userID)) {
            txtShowTimelinePostDelete.setVisibility(View.VISIBLE);
            txtShowTimelinePostEdit.setVisibility(View.VISIBLE);
        } else {
            txtShowTimelinePostDelete.setVisibility(View.INVISIBLE);
            txtShowTimelinePostEdit.setVisibility(View.INVISIBLE);
        }

    }

    public void getPostIntentContent() {
        Intent i = getIntent();
        timelinePostDetails = (HashMap<String, Object>) i.getSerializableExtra("timeLinePostDetails");
        postUsername = timelinePostDetails.get("username").toString();
        posterUserID = timelinePostDetails.get("userID").toString();
        postID = timelinePostDetails.get("postID").toString();
        postDate = timelinePostDetails.get("date").toString();
        postContent = timelinePostDetails.get("content").toString();
        postLikes = timelinePostDetails.get("likes").toString();
        postID = timelinePostDetails.get("avatarLink").toString();
        postCommentsCount = timelinePostDetails.get("comments_count").toString();
        userLikedPost = timelinePostDetails.get("ilike").toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showfullpost);

        getAllViewsbyID();
        getPostIntentContent();
        verifyIfIamPoster();

        Log.i("Timeline", timelinePostDetails.toString());

        functions.loadProfilePictureThumb(postID, imgShowTimelinePostProfilePicture);
        txtShowTimelinePostUsername.setText(postUsername);
        txtShowTimelinePostDate.setText(postDate);
        txtShowTimelinePostContent.setText(postContent);
        txtShowTimelinePostLikes.setText(postLikes);
        txtShowTimelinePostComments.setText(postCommentsCount);
        btnShowtimePostLike.setTag(userLikedPost);

        if (userLikedPost.equals("1")) {
            btnShowtimePostLike.setLiked(true);
        } else {
            btnShowtimePostLike.setLiked(false);
        }
        //LIKE BUTTON
        btnShowtimePostLike.setOnClickListener(view -> {

            int postLikes = Integer.valueOf(txtShowTimelinePostLikes.getText().toString());

            //If user has not lieked the post before...
            if (userLikedPost.equals("0")) {
                btnShowtimePostLike.setLiked(true);
                txtShowTimelinePostLikes.setText(String.valueOf(postLikes + 1));
            } else {
                btnShowtimePostLike.setLiked(false);
                if (Integer.valueOf(txtShowTimelinePostLikes.getText().toString()) > 0)
                    txtShowTimelinePostLikes.setText(String.valueOf(postLikes - 1));
            }
            sendLike();
        });

        //DELETE POST BUTTON with AlertDialog Confirmation
        txtShowTimelinePostDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
            alertDialogBuilder.setMessage(getString(R.string.deletePOST));
            alertDialogBuilder.setTitle(getString(R.string.deletePostTitle));
            alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.setPositiveButton(getString(R.string.yes_delete),
                    (dialog, arg1) -> {

                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("userID", Frontpage.userID);
                        postData.put("postID", postID);

                        functions.showProgress(lottie);
                        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {

                            @Override
                            public void notifySuccess(String response) throws JSONException {
                                functions.hideProgress(lottie);
                                if (functions.isJsonObject(response)) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String responseCode = jsonObject.get("responseCode").toString();
                                    String message = jsonObject.get("message").toString();
                                    if (responseCode.equals("1")) {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    if (responseCode.equals("0")) {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                            @Override
                            public void notifyError(VolleyError error) {
                                functions.hideProgress(lottie);
                            }
                        });

                        networkController.PostMethod(data.deletePost_Api, postData);
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });


        //Send Comment
        btnSendTimelineComment.setOnClickListener(view -> {
            String content = txtPostComment.getText().toString();
            if (content.length() <= 0) {
                functions.showSnackBarError(getString(R.string.comment_too_short), findViewById(android.R.id.content), getApplicationContext());
                ;
                return; }


            if (content.length() > data.maxCommentPostLength) {
                functions.showSnackBarError(getString(R.string.comment_longer_than).concat(" ".concat(data.maxCommentPostLength.toString())), findViewById(android.R.id.content), getApplicationContext());
                ;
                return;
            }

            functions.showProgress(lottie);
            HashMap<String, String> postData = new HashMap<>();
            postData.put("userID", posterUserID);
            postData.put("postID", postID);
            postData.put("content", content);

            networkController = new NetworkController(this, new NetworkController.IResult() {
                @Override
                public void notifySuccess(String response) {
                    Log.i("Response", response.toString());
                    functions.hideProgress(lottie);
                    btnSendTimelineComment.setEnabled(true);
                }

                @Override
                public void notifyError(VolleyError error) {
                    functions.hideProgress(lottie);
                    btnSendTimelineComment.setEnabled(true);
                    functions.showSnackBarError(getString(R.string.error_commenting), findViewById(android.R.id.content), getApplicationContext());
                    ;

                }
            });
            networkController.PostMethod(data.send_comment_Api, postData);
            //Toast.makeText(getApplicationContext(),getString(R.string.posting_comment),Toast.LENGTH_SHORT).show();
            btnSendTimelineComment.setEnabled(false);
        });

        //CLOSE ON CREATE
    }


    @Override
    protected void onResume() {
        super.onResume();


    }
}
package social.app.wesocial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class showFullPost extends AppCompatActivity {
    TextView txtShowTimelinePostUsername;
    TextView txtShowTimelinePostDate,txtShowTimelinePostContent,txtShowTimelinePostLikes,txtShowTimelinePostComments;
    ImageView imgShowTimelinePostProfilePicture,imgShowTimelinePostEdit,imgShowTimelinePostDelete;
    Functions functions= new Functions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_full_post);
        txtShowTimelinePostUsername = findViewById(R.id.txtShowTimelinePostUsername);
        txtShowTimelinePostDate = findViewById(R.id.txtShowTimelinePostDate);
        imgShowTimelinePostProfilePicture = findViewById(R.id.imgShowTimelinePostProfilePicture);
        txtShowTimelinePostContent = findViewById(R.id.txtShowTimelinePostContent);
        txtShowTimelinePostLikes = findViewById(R.id.txtShowTimelinePostLikes);
        txtShowTimelinePostComments = findViewById(R.id.txtShowTimelinePostComments);
        imgShowTimelinePostDelete = findViewById(R.id.imgShowTimelinePostDelete);
        imgShowTimelinePostEdit = findViewById(R.id.imgShowTimelinePostEdit);

        Intent i = getIntent();
        HashMap<String,Object> timelinePostDetails = new HashMap<>();
        timelinePostDetails = (HashMap<String, Object>) i.getSerializableExtra("timeLinePostDetails");
        String username = timelinePostDetails.get("username").toString();
        String userID = timelinePostDetails.get("userID").toString();
        String Date = timelinePostDetails.get("date").toString();
        String Content = timelinePostDetails.get("content").toString();
        String Likes = timelinePostDetails.get("likes").toString();
        String avatarLink = timelinePostDetails.get("avatarLink").toString();
        String Comments_count = timelinePostDetails.get("comments_count").toString();

        if (userID.equals(Frontpage.userID)){
            imgShowTimelinePostDelete.setVisibility(View.VISIBLE);
            imgShowTimelinePostEdit.setVisibility(View.VISIBLE);
        }else{
            imgShowTimelinePostDelete.setVisibility(View.INVISIBLE);
            imgShowTimelinePostEdit.setVisibility(View.INVISIBLE);
        }
        functions.loadProfilePictureThumb(avatarLink,imgShowTimelinePostProfilePicture);
        txtShowTimelinePostUsername.setText(username);
        txtShowTimelinePostDate.setText(Date);
        txtShowTimelinePostContent.setText(Content);
        txtShowTimelinePostLikes.setText(Likes);
        txtShowTimelinePostComments.setText(Comments_count);






    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
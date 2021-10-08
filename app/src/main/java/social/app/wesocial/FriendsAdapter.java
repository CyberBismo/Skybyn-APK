package social.app.wesocial;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private final List <FriendsDataClass> FriendsDataClass;
    Functions functions =new Functions();
    Data data =new Data();
    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_friends, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        Activity activity = (Activity) holder.itemView.getContext();
        LottieAnimationView lottie = activity.findViewById(R.id.frontpageProgressView);

        FriendsDataClass friendsDataClass = FriendsDataClass.get(position);
        holder.txtFriendUsername.setText(friendsDataClass.getFriendUsername());
        //SAVE THE User's ID INSIDE VIEW'S Tag
        holder.txtFriendUsername.setTag(friendsDataClass.getFriendID());
        holder.txtFriendNickname.setText(friendsDataClass.getFriendNickname());
        //SAVE THE AVATAR LINK INSIDE VIEW'S Tag
        holder.imgFriendProfilePicture.setTag(friendsDataClass.getFriendAvatarLink());
        functions.loadProfilePictureDrawableThumb(holder.imgFriendProfilePicture.getTag().toString(), holder.imgFriendProfilePicture);

        switch (friendsDataClass.getFriendOnline()) {
            case "1":
                holder.imgFriendOnlineStatus.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.online_icon));
                break;
            case "2":
                holder.imgFriendOnlineStatus.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.offline_icon));
                break;
        }

        Drawable drawable = holder.imgFriendOnlineStatus.getDrawable();

        holder.imgFriendProfilePicture.setOnClickListener(view -> functions.loadTimeLineUserProfile(friendsDataClass.getFriendID(),activity,holder.itemView.getContext().getApplicationContext()));

        holder.btnMessageFriend.setOnClickListener(view -> {
            Fragment fragmentShowFullChat = social.app.wesocial.showFullChat.newInstance(holder.txtFriendUsername.getText().toString(), drawable,friendsDataClass.getFriendAvatarLink(), friendsDataClass.getFriendID());
            functions.LoadFragment(fragmentShowFullChat,"",(Activity) holder.itemView.getContext(),true,false);
        });

        //BUTTON TO REMOVE FRIEND
        holder.btnRemoveFriend.setOnClickListener(view -> {
            HashMap<String,String> postData = new HashMap<>();
            postData.put("friendID",holder.txtFriendUsername.getTag().toString());
            postData.put("userID",Frontpage.userID);

            NetworkController networkController = new NetworkController(holder.itemView.getContext(), new NetworkController.IResult() {
                @Override
                public void notifySuccess(String response) throws JSONException {
                    Timber.i(response);
                    functions.hideProgress(lottie);

                    if (functions.isJsonObject(response)) {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.get("message").toString();
                        String responseCode = jsonObject.get("responseCode").toString();

                        if (responseCode.equals("1")) {
                            FriendsDataClass.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(),FriendsDataClass.size());

                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext()).create();
                        alertDialog.setTitle("");
                        alertDialog.setMessage(message);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                    }
                }
                @Override
                public void notifyError(VolleyError error) {

                }
            });

            functions.showProgress(lottie);

            networkController.PostMethod(data.remove_friend_Api,postData);


        });


        //BUTTON TO BLOCK FRIEND
        holder.btnBlockFriend.setOnClickListener(view -> {
            HashMap<String,String> postData = new HashMap<>();
            postData.put("friendID",holder.txtFriendUsername.getTag().toString());
            postData.put("userID",Frontpage.userID);

            NetworkController networkController = new NetworkController(holder.itemView.getContext(), new NetworkController.IResult() {
                @Override
                public void notifySuccess(String response) throws JSONException {
                    Timber.i(response);
                    functions.hideProgress(lottie);

                    if (functions.isJsonObject(response)) {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.get("message").toString();
                        String responseCode = jsonObject.get("responseCode").toString();

                        if (responseCode.equals("1")) {
                            FriendsDataClass.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(),FriendsDataClass.size());

                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext()).create();
                        alertDialog.setTitle("");
                        alertDialog.setMessage(message);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                    }
                }
                @Override
                public void notifyError(VolleyError error) {

                }
            });

            functions.showProgress(lottie);

            networkController.PostMethod(data.block_friend_Api,postData);


        });


    }
    public FriendsAdapter(List <FriendsDataClass> FriendsDataClass){
        this.FriendsDataClass = FriendsDataClass;
    }

    @Override
    public int getItemCount() {
        return FriendsDataClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFriendUsername, txtFriendNickname;
        ImageView imgFriendProfilePicture,imgFriendOnlineStatus;
        CardView friendCardView;
        Button btnBlockFriend , btnRemoveFriend,btnMessageFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFriendUsername = itemView.findViewById(R.id.txtFriendUsername);
            txtFriendNickname = itemView.findViewById(R.id.txtFriendNickname);
            imgFriendProfilePicture = itemView.findViewById(R.id.imgFriendProfilePicture);
            btnRemoveFriend = itemView.findViewById(R.id.btnRemoveFriend);
            btnBlockFriend = itemView.findViewById(R.id.btnBlockFriend);
            btnMessageFriend = itemView.findViewById(R.id.btnMessageFriend);
            friendCardView=itemView.findViewById(R.id.friendCardView);
            imgFriendOnlineStatus= itemView.findViewById(R.id.imgFriendOnlineStatus);
        }
    }
}
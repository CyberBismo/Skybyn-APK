package social.app.wesocial;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;


public class BlockedFriendsAdapter extends RecyclerView.Adapter<BlockedFriendsAdapter.ViewHolder> {
    private final List <FriendsDataClass> FriendsDataClass;
    Functions functions =new Functions();
    Data data =new Data();
    @NonNull
    @Override
    public BlockedFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_blocked_friends, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedFriendsAdapter.ViewHolder holder, int position) {
        Activity activity = (Activity) holder.itemView.getContext();
        LottieAnimationView lottie = activity.findViewById(R.id.frontpageProgressView);

        FriendsDataClass friendsDataClass = FriendsDataClass.get(position);
        holder.txtBlockedFriendUsername.setText(friendsDataClass.getFriendUsername());
        //SAVE THE User's ID INSIDE VIEW'S Tag
        holder.txtBlockedFriendUsername.setTag(friendsDataClass.getFriendID());
        holder.txtBlockedFriendNickname.setText(friendsDataClass.getFriendNickname());
        //SAVE THE AVATAR LINK INSIDE VIEW'S Tag
        holder.imgBlockedFriendProfilePicture.setTag(friendsDataClass.getFriendAvatarLink());
        functions.loadProfilePictureDrawableThumb(holder.imgBlockedFriendProfilePicture.getTag().toString(), holder.imgBlockedFriendProfilePicture);

            holder.btnUnblockFriend.setOnClickListener(view -> {
                HashMap<String,String> postData = new HashMap<>();
                postData.put("friend_id",friendsDataClass.getFriendID());
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
                        functions.hideProgress(lottie);
                    }
                });
                functions.showProgress(lottie);
                networkController.PostMethod(data.unblock_friend_Api,postData);

            });
    }
    public BlockedFriendsAdapter(List <FriendsDataClass> FriendsDataClass){
        this.FriendsDataClass = FriendsDataClass;
    }

    @Override
    public int getItemCount() {
        return FriendsDataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtBlockedFriendUsername, txtBlockedFriendNickname;
        ImageView imgBlockedFriendProfilePicture;
        CardView BlockedFriendCardView;
        Button btnUnblockFriend;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBlockedFriendUsername = itemView.findViewById(R.id.txtBlockedFriendUsername);
            txtBlockedFriendNickname = itemView.findViewById(R.id.txtBlockedFriendNickname);
            imgBlockedFriendProfilePicture = itemView.findViewById(R.id.imgBlockedFriendProfilePicture);
            btnUnblockFriend = itemView.findViewById(R.id.btnUnblockFriend);
            BlockedFriendCardView =itemView.findViewById(R.id.blockedFriendCardView);
        }
    }
}
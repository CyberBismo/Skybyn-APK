package social.app.wesocial;

import static android.view.View.INVISIBLE;
import static social.app.wesocial.R.id;
import static social.app.wesocial.R.layout;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;


class userSearchAdapter extends RecyclerView.Adapter<userSearchAdapter.ViewHolder> {
    private final List<UserDataClass> UserDataClass;
    Functions functions = new Functions();
    Data data = new Data();

    userStatusEnum userStatus;

    enum userStatusEnum {friend, received, sent, iam_blocked, notFriends, iBlockedUser}

    public userSearchAdapter(List<UserDataClass> userDataClass) {
        UserDataClass = userDataClass;
    }

    @NonNull
    @Override
    public userSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout.display_users_search, parent, false);

        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull userSearchAdapter.ViewHolder holder, int position) {
        UserDataClass userDataClass = UserDataClass.get(position);
        holder.txtUserSearchUsername.setText(userDataClass.getUsername());
        holder.txtUserSearchUsername.setTag(R.integer.integer_key_zero, userDataClass.getUserID());
        holder.txtUserSearchNickname.setText(userDataClass.getUserNickname());

        functions.loadProfilePictureDrawableThumb(userDataClass.getUserAvatarLink(), holder.imgUserSearchProfilePicture);
        switch (userDataClass.getIsFriend()) {
            case "1":
                holder.btnUserSearchAddFriend.setText(holder.itemView.getContext().getString(R.string.message));
                userStatus = userStatusEnum.friend;
                break;
            case "2:":
                userStatus = userStatusEnum.received;
                holder.btnUserSearchAddFriend.setText("Request pending");
                holder.btnUserSearchAddFriend.setEnabled(false);
                break;
            case "3":
                userStatus = userStatusEnum.sent;
                holder.btnUserSearchAddFriend.setText("Cancel request");
                break;
            case "4":
                userStatus = userStatusEnum.iam_blocked;
                holder.btnUserSearchAddFriend.setVisibility(INVISIBLE);
                break;
            case "5":
                userStatus = userStatusEnum.iBlockedUser;
                holder.btnUserSearchAddFriend.setText("Unblock");
                break;
            default:
                userStatus = userStatusEnum.notFriends;
                holder.btnUserSearchAddFriend.setText("Add friend");
                break;
        }
        //SEND FRIEND REQUEST
        holder.btnUserSearchAddFriend.setOnClickListener(view -> {
            String link = "";
            switch (userStatus) {
                case friend:
                    Fragment fragmentShowFullChat = social.app.wesocial.showFullChat.newInstance(userDataClass.getUsername(), null, userDataClass.getUserAvatarLink(), userDataClass.getUserID());
                    functions.LoadFragment(fragmentShowFullChat, "", (Activity) holder.itemView.getContext(), false, false);
                    break;
                case received:
                    Fragment friendFragment = Friends.newInstance(data.accept_friend_action);
                    functions.LoadFragment(friendFragment, data.accept_friend_action, (Activity) holder.itemView.getContext(), false, false);
                    break;
                case sent:
                    link = data.cancel_friend_Api;
                    break;
                case iam_blocked:
                    break;
                case notFriends:
                    link = data.add_friend_Api;
                    break;
                case iBlockedUser:
                    link = data.unblock_friend_Api;
                    break;
            }

            HashMap<String, String> postData = new HashMap<>();
            postData.put("friendID", userDataClass.getUserID());
            postData.put("userID", Frontpage.userID);
            UserDataClass.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            notifyItemRangeChanged(holder.getAdapterPosition(), UserDataClass.size());

            NetworkController networkController = new NetworkController(holder.itemView.getContext(), new NetworkController.IResult() {
                @Override
                public void notifySuccess(String response) throws JSONException {
                    Timber.i(response);
                    //functions.hideProgress(lottie);
                    if (functions.isJsonObject(response)) {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.get("message").toString();
                        String responseCode = jsonObject.get("responseCode").toString();
                        AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext()).create();
                        alertDialog.setTitle("");
                        alertDialog.setMessage(message);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> dialog.dismiss());
                        alertDialog.show();

                    } else {
                        Timber.i(response);
                    }
                }

                @Override
                public void notifyError(VolleyError error) {
                    //functions.hideProgress(lottie);
                }
            });
            //functions.showProgress(lottie);
            networkController.PostMethod(link, postData);
        });

        if (userDataClass.getUserID().equals(Frontpage.userID)) {
            holder.btnUserSearchAddFriend.setVisibility(INVISIBLE);
        }

        if (userDataClass.getIsOnline().equals("0")) {
            holder.imgUserSearchProfilePicture.setBackgroundResource(0);
            holder.txtUserSearchOnline.setText(holder.itemView.getContext().getString(R.string.offline));
            holder.txtUserSearchOnline.setTextColor(holder.itemView.getContext().getColor(R.color.red));
        } else {
            holder.txtUserSearchOnline.setBackgroundResource(R.drawable.online_status);
        }

    }

    @Override
    public int getItemCount() {
        return UserDataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtUserSearchUsername, txtUserSearchNickname;
        Button btnUserSearchAddFriend;
        TextView txtUserSearchOnline;
        CircularImageView imgUserSearchProfilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(id.userSearchCardView);
            txtUserSearchUsername = itemView.findViewById(id.txtFriendUsername);
            txtUserSearchNickname = itemView.findViewById(id.txtFriendNickname);
            txtUserSearchOnline = itemView.findViewById(id.txtFriendOnline);
            imgUserSearchProfilePicture = itemView.findViewById(id.imgFriendProfilePicture);
            btnUserSearchAddFriend = itemView.findViewById(id.btnBlockFriend);

        }
    }
}
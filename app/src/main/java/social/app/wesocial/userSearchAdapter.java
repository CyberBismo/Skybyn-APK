package social.app.wesocial;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static social.app.wesocial.R.id;
import static social.app.wesocial.R.layout;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
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

        if (userDataClass.getIsFriend().equals(1)) {
            holder.btnUserSearchAddFriend.setVisibility(INVISIBLE);
        } else {
            holder.btnUserSearchAddFriend.setVisibility(VISIBLE);
        }


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

        //SEND FRIEND REQUEST
        holder.btnUserSearchAddFriend.setOnClickListener(view -> {

                    holder.btnUserSearchAddFriend.setEnabled(false);
                    holder.btnUserSearchAddFriend.setText("Sent");

                    HashMap<String, String> postData = new HashMap<>();
                    postData.put("userID", Frontpage.userID);
                    postData.put("friendID", (String) holder.txtUserSearchUsername.getTag(R.integer.integer_key_zero));
                    postData.put("action",data.add_friend_action);
                    NetworkController networkController = new NetworkController(holder.itemView.getContext(),new NetworkController.IResult() {
                        @Override
                        public void notifySuccess(String response) throws JSONException {
                            if (functions.isJsonObject(response)) {
                                Timber.i(response.toString());
                                JSONObject jsonObject = new JSONObject(response);
                                String responseCode = jsonObject.get("responseCode").toString();
                                String message = jsonObject.get("message").toString();
                                Toast.makeText(holder.itemView.getContext(),message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void notifyError(VolleyError error) {

                        }
                    });
                    networkController.PostMethod(data.add_friend_Api, postData);
                }
        );

    }


    @Override
    public int getItemCount() {
        return UserDataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtUserSearchUsername,txtUserSearchNickname;
        Button btnUserSearchAddFriend;
        TextView txtUserSearchOnline;
        CircularImageView imgUserSearchProfilePicture;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(id.userSearchCardView);
            txtUserSearchUsername = itemView.findViewById(id.txtUserSearchUsername);
            txtUserSearchNickname = itemView.findViewById(id.txtUserSearchNickname);
            txtUserSearchOnline = itemView.findViewById(id.txtUserSearchOnline);
            imgUserSearchProfilePicture = itemView.findViewById(id.imgUserSearchProfilePicture);
            btnUserSearchAddFriend = itemView.findViewById(id.btnUserSearchAddFriend);

        }
    }
}
package social.app.wesocial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private final List <FriendsDataClass> FriendsDataClass;
    Functions functions =new Functions();
    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_friends, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        FriendsDataClass friendsDataClass = FriendsDataClass.get(position);
        holder.txtFriendUsername.setText(friendsDataClass.getFriendUsername());
        //SAVE THE User's ID INSIDE VIEW'S Tag
        holder.txtFriendUsername.setTag(friendsDataClass.getFriendID());
        holder.txtFriendNickname.setText(friendsDataClass.getFriendNickname());
        //SAVE THE AVATAR LINK INSIDE VIEW'S Tag
        holder.imgFriendProfilePicture.setTag(friendsDataClass.getFriendAvatarLink());
        functions.loadProfilePictureDrawableThumb(holder.imgFriendProfilePicture.getTag().toString(), holder.imgFriendProfilePicture);

    }
    public FriendsAdapter(List <FriendsDataClass> FriendsDataClass){
        this.FriendsDataClass = FriendsDataClass;
    }

    @Override
    public int getItemCount() {
        return FriendsDataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFriendUsername, txtFriendNickname;
        ImageView imgFriendProfilePicture;
        CardView friendCardView;
        Button btnBlockFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFriendUsername = itemView.findViewById(R.id.txtUserSearchUsername);
            txtFriendNickname = itemView.findViewById(R.id.txtUserSearchNickname);
            imgFriendProfilePicture = itemView.findViewById(R.id.imgUserSearchProfilePicture);
            btnBlockFriend = itemView.findViewById(R.id.btnUserSearchAddFriend);
            friendCardView=itemView.findViewById(R.id.friendCardView);
        }
    }
}
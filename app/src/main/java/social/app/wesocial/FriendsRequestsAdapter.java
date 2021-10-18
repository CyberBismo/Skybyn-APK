package social.app.wesocial;

import android.app.Activity;
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


public class FriendsRequestsAdapter extends RecyclerView.Adapter<FriendsRequestsAdapter.ViewHolder> {

    private final List <FriendsDataClass> FriendsDataClass;

    Functions functions;
    Data data =new Data();

    @NonNull
    @Override
    public FriendsRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_friends_requests, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRequestsAdapter.ViewHolder holder, int position) {
         functions=new Functions(holder.itemView.getContext());
        Activity activity = (Activity) holder.itemView.getContext();
        LottieAnimationView lottie = activity.findViewById(R.id.frontpageProgressView);

        FriendsDataClass friendsDataClass = FriendsDataClass.get(position);
        holder.txtFriendRequestsUsername.setText(friendsDataClass.getFriendUsername());
        //SAVE THE User's ID INSIDE VIEW'S Tag
        holder.txtFriendRequestsUsername.setTag(friendsDataClass.getFriendID());
        holder.txtFriendRequestsNickname.setText(friendsDataClass.getFriendNickname());
        //SAVE THE AVATAR LINK INSIDE VIEW'S Tag
        holder.imgFriendRequestsProfilePicture.setTag(friendsDataClass.getFriendAvatarLink());

        functions.loadProfilePictureDrawableThumb(holder.imgFriendRequestsProfilePicture.getTag().toString(), holder.imgFriendRequestsProfilePicture);

        //BUTTON CLICK TO ACCEPT REQUEST
            holder.btnAcceptFriendRequest.setOnClickListener(view -> {
                HashMap<String,String> postData = new HashMap<>();
                postData.put("friendID",holder.txtFriendRequestsUsername.getTag().toString());
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
                                //message = message + holder.txtFriendRequestsUsername.getText().toString();


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

                networkController.PostMethod(data.accept_friend_Api,postData);

            });

        //BUTTON CLICK TO DECLINE REQUEST
            holder.btnDeclineFriendRequest.setOnClickListener(view -> {
            HashMap<String,String> postData = new HashMap<>();
            postData.put("friendID",holder.txtFriendRequestsUsername.getTag().toString());
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

            networkController.PostMethod(data.decline_friend_Api,postData);

        });


            //BUTTON CLICK TO CANCEL REQUEST
        holder.btnCancelFriendRequest.setOnClickListener(view -> {
            HashMap<String,String> postData = new HashMap<>();
            postData.put("friendID",holder.txtFriendRequestsUsername.getTag().toString());
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

            networkController.PostMethod(data.cancel_friend_Api,postData);

        });

    }


    public FriendsRequestsAdapter(List <FriendsDataClass> FriendsDataClass){
        this.FriendsDataClass = FriendsDataClass;
    }

    @Override
    public int getItemCount() {
        return FriendsDataClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFriendRequestsUsername, txtFriendRequestsNickname;
        ImageView imgFriendRequestsProfilePicture;
        CardView friendCardView;
        Button btnAcceptFriendRequest,btnDeclineFriendRequest,btnCancelFriendRequest;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFriendRequestsUsername = itemView.findViewById(R.id.txtFriendRequestsUsername);
            txtFriendRequestsNickname = itemView.findViewById(R.id.txtFriendRequestsNickname);
            imgFriendRequestsProfilePicture = itemView.findViewById(R.id.imgFriendRequestsProfilePicture);
            btnAcceptFriendRequest = itemView.findViewById(R.id.btnAcceptFriendRequest);
            btnDeclineFriendRequest = itemView.findViewById(R.id.btnDeclineFriendRequest);
            btnDeclineFriendRequest = itemView.findViewById(R.id.btnDeclineFriendRequest);
            btnCancelFriendRequest = itemView.findViewById(R.id.btnCancelFriendRequest);
            friendCardView=itemView.findViewById(R.id.friendCardView);
        }
    }
}
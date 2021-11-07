package social.app.wesocial;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import java.util.List;


public class BrowsePageAdapter extends RecyclerView.Adapter<BrowsePageAdapter.ViewHolder> {
    private final List<BrowsePageDataClass> BrowsePageDataClass;
    Functions functions;
    Data data = new Data();

    public BrowsePageAdapter(List<BrowsePageDataClass> BrowsePageDataClass) {
        this.BrowsePageDataClass = BrowsePageDataClass;
    }

    @NonNull
    @Override
    public BrowsePageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_pages, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BrowsePageAdapter.ViewHolder holder, int position) {
        Activity activity = (Activity) holder.itemView.getContext();
        LottieAnimationView lottie = activity.findViewById(R.id.frontpageProgressView);
        functions = new Functions(holder.itemView.getContext());
        BrowsePageDataClass browsePageDataClass = BrowsePageDataClass.get(position);
        holder.txtPageDesc.setText(browsePageDataClass.getPageDesc());
        holder.txtPageName.setText(browsePageDataClass.getPageName());
        functions.loadPageDrawableThumb(browsePageDataClass.getPageAvatarLink(), holder.imgPageAvatar);
        if (browsePageDataClass.getPageLock().equals("")) {
            functions.loadProfilePictureDrawableThumb(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.unlocked), holder.imgPageLock);
        } else {
            functions.loadProfilePictureDrawableThumb(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.lock), holder.imgPageLock);
        }
    }

    @Override
    public int getItemCount() {
        return BrowsePageDataClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPageName, txtPageDesc;
        ImageView imgPageAvatar, imgPageLock;
        ;
        CardView displayPageCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPageName = itemView.findViewById(R.id.txtDisplayPageName);
            txtPageDesc = itemView.findViewById(R.id.txtDisplayPageDesc);
            imgPageAvatar = itemView.findViewById(R.id.imgPagePicture);
            imgPageLock = itemView.findViewById(R.id.imgDisplayPageLock);
            displayPageCardView = itemView.findViewById(R.id.displayPageCardView);

        }
    }
}
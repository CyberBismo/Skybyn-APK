package social.app.wesocial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class EmptyAdapter extends RecyclerView.Adapter<EmptyAdapter.ViewHolder> {
    private final List<MessageListDataClass> MessageListDataClass;

    Data data = new Data();



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_message, parent, false);
        return new ViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageListDataClass messageListDataClass = MessageListDataClass.get(position);

    }

    public EmptyAdapter(List<MessageListDataClass> MessageListDataClass) {
        this.MessageListDataClass = MessageListDataClass;

    }

    @Override
    public int getItemCount() {
        return MessageListDataClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView  Content, date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Content = itemView.findViewById(R.id.lblNotificationContent);
            date = itemView.findViewById(R.id.lblNotificationDate);

        }
    }
}
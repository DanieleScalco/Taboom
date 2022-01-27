package it.bastoner.taboom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bastoner.taboom.R;
import it.bastoner.taboom.objects.Card;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private List<Card> cardList;

    public RecyclerViewAdapter(List<Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(cardList.get(position).getTitle());
        holder.tabooWord1.setText(cardList.get(position).getTabooWords().get(0));
        holder.tabooWord2.setText(cardList.get(position).getTabooWords().get(1));
        holder.tabooWord3.setText(cardList.get(position).getTabooWords().get(2));
        holder.tabooWord4.setText(cardList.get(position).getTabooWords().get(3));
        holder.tabooWord5.setText(cardList.get(position).getTabooWords().get(4));
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "ViewHolder";

        private TextView title;
        private TextView tabooWord1;
        private TextView tabooWord2;
        private TextView tabooWord3;
        private TextView tabooWord4;
        private TextView tabooWord5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            tabooWord1 = itemView.findViewById(R.id.taboo_word_1);
            tabooWord2 = itemView.findViewById(R.id.taboo_word_2);
            tabooWord3 = itemView.findViewById(R.id.taboo_word_3);
            tabooWord4 = itemView.findViewById(R.id.taboo_word_4);
            tabooWord5 = itemView.findViewById(R.id.taboo_word_5);

        }
    }
}

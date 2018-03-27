package vn.ngh.epubreader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.ngh.epubreader.core.TableOfContent;

/**
 *
 */
public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.MyViewHolder> {
    private List<TableOfContent.Chapter> mListTitle = new ArrayList<>();
    private RecyclerViewClickListener mListener;

    public ChapterAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recyclerview, parent, false);
        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String title = mListTitle.get(position).getTitle();
        holder.title.setText(title == null ? "" : title);
    }

    @Override
    public int getItemCount() {
        return mListTitle.size();
    }

    public void addListTitle(List<TableOfContent.Chapter> list) {
        if (list != null) {
            mListTitle.addAll(list);
        }
    }

    public void clear() {
        mListTitle.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            title = (TextView) view.findViewById(R.id.text_title);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
}

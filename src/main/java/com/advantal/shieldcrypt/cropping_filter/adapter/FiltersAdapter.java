package com.advantal.shieldcrypt.cropping_filter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.cropping_filter.model.FilterData;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.ViewHolder>{
    private ArrayList<FilterData> filtersList;
    private Context context;
    private Bitmap bitmap;
    private Callback callback;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtFilterName;
        private final PhotoEditorView photoEditorView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            photoEditorView = (PhotoEditorView) view.findViewById(R.id.photoEditorView);
            txtFilterName = (TextView) view.findViewById(R.id.txtFilterName);
        }

        public TextView getTextView() {
            return txtFilterName;
        }

        public PhotoEditorView getImageView() {
            return photoEditorView;
        }
    }

    public FiltersAdapter(Context context, ArrayList<FilterData> filtersList, Bitmap bitmap, Callback callback) {
        this.context = context;
        this.filtersList = filtersList;
        this.bitmap = bitmap;
        this.callback = callback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_filter_view, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(""+filtersList.get(position).getFilterName());
       // viewHolder.getImageView().setImageResource(R.drawable.cat_small);
        viewHolder.getImageView().getSource().setImageBitmap(bitmap);
        viewHolder.getImageView().setFilterEffect(filtersList.get(position).getFilterType());
        viewHolder.setIsRecyclable(false);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback!=null){
                    callback.onFilterClicked(bitmap, filtersList.get(position).getFilterType());
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filtersList.size();
    }

    public interface Callback{
        void onFilterClicked(Bitmap bitmap, PhotoFilter photoFilter);
    }
}

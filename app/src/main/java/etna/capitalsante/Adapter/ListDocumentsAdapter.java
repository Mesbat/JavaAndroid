package etna.capitalsante.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.ArrayList;
import java.util.List;

import etna.capitalsante.R;
import etna.capitalsante.model.Datum;
import etna.capitalsante.model.DocumentLister;
import etna.capitalsante.view.DocumentFilter;
import etna.capitalsante.view.FicheDoc;

public class ListDocumentsAdapter extends RecyclerView.Adapter<ListDocumentsAdapter.ViewHolder> {

    private List<Datum> dataset;
    private Context context;

    public ListDocumentsAdapter(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new ViewHolder(view);
    }

    public void clear() {
        int size = this.dataset.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.dataset.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Datum p = dataset.get(position);
        holder.filename.setText(p.getName());
        int id = this.context.getResources().getIdentifier(p.getExtension().substring(1), "drawable", this.context.getPackageName());
        if (id != 0)
            holder.icon.setImageResource(id);
        else
                Glide.with(context)
                .load("http://icons.iconarchive.com/icons/zhoolego/material/512/Filetype-Docs-icon.png")
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.icon);

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) context;
                v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.image_click));
                Intent i = new Intent(v.getContext(), FicheDoc.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Datum", p);
                i.putExtras(bundle);
                v.getContext().startActivity(i);
                activity.overridePendingTransition(R.anim.slide_right, R.anim.def);
            }
        });

        // IF GLIDE USE -------------------------------
//        String URI;
//
//        switch (p.getExtension())
//        {
//            case ".jpg":
//                URI = "";
//                break;
//            case ".png":
//                URI = "";
//                break;
//            default:
//                URI = "http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/file-text-icon.png";
//        }
//
//        Glide.with(context)
//                .load(URI)
//                .centerCrop()
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void AddListDocument(List<Datum> Liste) {
        dataset.addAll(Liste);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton icon;
        private TextView filename;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = (ImageButton) itemView.findViewById(R.id.icon);
            filename = (TextView) itemView.findViewById(R.id.filename);
        }
    }
}
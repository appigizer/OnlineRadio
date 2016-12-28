package com.example.mjstudio.internetradio;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;

import io.realm.Realm;
import io.realm.RealmResults;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {


    public interface OnItemClickListener extends ArrayStationAdapter.OnItemClickListener {
        void startService();
    }

    Realm realm;
    RealmResults<CategoryEntity> categoryList;
    Context context;



    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    String categoryIndex,categoryImage,categoryTitle;
    public OnItemClickListener listenerCategory;
    public AdapterCategory(Activity activity) {
       imageLoader = ImageLoader.getInstance();
        realm = Realm.getDefaultInstance();
        context = activity;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.backgroundicon)
                .showImageForEmptyUri(R.drawable.backgroundicon)
                .showImageOnFail(R.drawable.backgroundicon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        updateData();

    }
        public void updateData(){
        categoryList = realm.where(CategoryEntity.class).findAllSorted("title");
        notifyDataSetChanged();
    }

    @Override
    public AdapterCategory.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category, viewGroup, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterCategory.ViewHolder viewHolder, int i) {
        final CategoryEntity categoryEntity = categoryList.get(i);
        viewHolder.bind(categoryEntity, listenerCategory);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 categoryIndex = categoryEntity.getId().toString();
                categoryImage = categoryEntity.getCategoryimage();
                categoryTitle = categoryEntity.getTitle();
                context.startActivity(new Intent(context,StreamList.class).putExtra("Categoryindex", categoryIndex)
                       .putExtra("Categoryname", categoryImage).putExtra("title",categoryTitle));
                listenerCategory.startService();

            }
        });

    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewforCategoryName;
        private ImageView imgageForCategory;
        public ViewHolder(View view) {
            super(view);

            textViewforCategoryName = (TextView)view.findViewById(R.id.textViewfor_categoriesname);
            imgageForCategory = (ImageView) view.findViewById(R.id.imageForCategory);
        }
        public  void bind(final CategoryEntity categoryEntity, final ArrayStationAdapter.OnItemClickListener listener) {

           textViewforCategoryName.setText(categoryEntity.getTitle());


            if (categoryEntity!= null) {
                final String url = categoryEntity.getCategoryimage();
                if (url == null) {
                    URI defaultUri;
                    try {
                        defaultUri = new URI("https://cdn.devality.com/station/38392/logo.gif");
                        Picasso.with(itemView.getContext()).
                                load(String.valueOf(defaultUri)).into(imgageForCategory);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        URI uri = new URI(categoryEntity.getCategoryimage());
                        URI defaultUrl =  new URI("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQp74qov_puy-0apTKqr6TrZvwGMnrAgfR0ovtzqPVmAir_3LOY");
                       imageLoader.displayImage(url,imgageForCategory,options);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
        String url;
        public ViewHolder(View view) {
            super(view);

            textViewforCategoryName = (TextView)view.findViewById(R.id.textViewfor_categoriesname);
            imgageForCategory = (ImageView) view.findViewById(R.id.imageForCategory);
        }
        public  void bind(final CategoryEntity categoryEntity, final ArrayStationAdapter.OnItemClickListener listener) {

           textViewforCategoryName.setText(categoryEntity.getTitle());


            if (categoryEntity!= null) {
                url = categoryEntity.getCategoryimage();
                if (url == null) {

                    String defaulturi = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQCxumxjDnEtPCvN-_gVUbcELLnEj36_BJGJk5KsWTH5itj1saK";
                    imageLoader.displayImage(defaulturi, imgageForCategory, options);
                } else {

                    imageLoader.displayImage(url, imgageForCategory, options, new ImageLoadingListener() {
                        public void onLoadingStarted(String imageUri, View view) {
                            imgageForCategory.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            imgageForCategory.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            imgageForCategory.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            imgageForCategory.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        }
                    });


                }
            }
            }
        }
    }


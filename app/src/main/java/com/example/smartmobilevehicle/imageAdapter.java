package com.example.smartmobilevehicle;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class imageAdapter extends RecyclerView.Adapter<imageAdapter.ViewHolder>  {

    private String[]captions;
    private  int[] imageIds;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;

        public ViewHolder(CardView v){

            super(v);
            cardView=v;

        }
    }

    public imageAdapter(String[] captions,int[] imageIds){

        this.captions=captions;
        this.imageIds=imageIds;
    }

    @Override
    public int getItemCount() {
        return captions.length;
    }

    @NonNull
    @Override
    public imageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardView cv=(CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_captioned_image,parent,false);

        return new ViewHolder(cv);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;
        ImageView imageView = (ImageView) cardView.findViewById(R.id.info_image);

        Drawable drawable = ContextCompat.getDrawable(cardView.getContext(), imageIds[position]);
        imageView.setImageDrawable(drawable);
        imageView.setContentDescription(captions[position]);
        TextView textView = (TextView) cardView.findViewById(R.id.info_text);
        textView.setText(captions[position]);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(position==3){
                    Intent intent=new Intent(cardView.getContext(),MapActivity.class);
                    cardView.getContext().startActivity(intent);
                }

                if(position==4){
                    Intent intent=new Intent(cardView.getContext(),PatrolShedMap.class);
                    cardView.getContext().startActivity(intent);
                }

                if(position==0){

                    Intent intent=new Intent(cardView.getContext(),EmergencyActivity.class);
                    cardView.getContext().startActivity(intent);
                }

                if(position==1){
                    Intent intent=new Intent(cardView.getContext(),MapReciveActivity.class);
                    cardView.getContext().startActivity(intent);

                }

                if(position==2){
                    Intent intent=new Intent(cardView.getContext(),InsueranceActivity.class);
                    cardView.getContext().startActivity(intent);

                }



            }
        });

    }
}

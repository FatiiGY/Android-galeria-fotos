package com.androidcodeman.simpleimagegallery.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.androidcodeman.simpleimagegallery.R;
import com.androidcodeman.simpleimagegallery.utils.imageIndicatorListener;
import com.androidcodeman.simpleimagegallery.utils.pictureFacer;
import com.androidcodeman.simpleimagegallery.utils.recyclerViewPagerImageIndicator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

import static androidx.core.view.ViewCompat.setTransitionName;


/**
 * Author: FTYGY
 *
 * este fragmento controla la exploración de todas las imágenes en un ArrayList de pictureFacer pasado en el constructor
 * las imágenes se cargan en un ViewPager y un RecyclerView se utiliza como indicador de buscapersonas para
 * cada imagen en el ViewPager
 */
public class pictureBrowserFragment extends Fragment implements imageIndicatorListener {

    private  ArrayList<pictureFacer> allImages = new ArrayList<>();
    private int position;
    private Context animeContx;
    private ImageView image;
    private ViewPager imagePager;
    private RecyclerView indicatorRecycler;
    private int viewVisibilityController;
    private int viewVisibilitylooper;
    private ImagesPagerAdapter pagingImages;
    private int previousSelected = -1;

    public pictureBrowserFragment(){

    }

    public pictureBrowserFragment(ArrayList<pictureFacer> allImages, int imagePosition, Context anim) {
        this.allImages = allImages;
        this.position = imagePosition;
        this.animeContx = anim;
    }

    public static pictureBrowserFragment newInstance(ArrayList<pictureFacer> allImages, int imagePosition, Context anim) {
        pictureBrowserFragment fragment = new pictureBrowserFragment(allImages,imagePosition,anim);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.picture_browser, container, false);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inicialización de los enteros de control de visibilidad recyclerView
        viewVisibilityController = 0;
        viewVisibilitylooper = 0;

        //configurar viewPager con imágenes
        imagePager = view.findViewById(R.id.imagePager);
        pagingImages = new ImagesPagerAdapter();
        imagePager.setAdapter(pagingImages);
        imagePager.setOffscreenPageLimit(3);
        imagePager.setCurrentItem(position);//mostrar la imagen en la posición actual pasada por la actividad ImageDisplay


        //configurar el indicador de vista del reciclador para viewPager
        indicatorRecycler = view.findViewById(R.id.indicatorRecycler);
        indicatorRecycler.hasFixedSize();
        indicatorRecycler.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));
        RecyclerView.Adapter indicatorAdapter = new recyclerViewPagerImageIndicator(allImages,getContext(),this);
        indicatorRecycler.setAdapter(indicatorAdapter);

        //ajustando el indicador recyclerView a la posición actual del viewPager,
        // //también resalta la imagen en recyclerView con respecto a la posición del viewPager
        allImages.get(position).setSelected(true);
        previousSelected = position;
        indicatorAdapter.notifyDataSetChanged();
        indicatorRecycler.scrollToPosition(position);


        // este agente de escucha controla la visibilidad de la indicación recyclerView
        //y su posición actual con respecto a la imagen ViewPager
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(previousSelected != -1){
                    allImages.get(previousSelected).setSelected(false);
                    previousSelected = position;
                    allImages.get(position).setSelected(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                    indicatorRecycler.scrollToPosition(position);
                }else{
                    previousSelected = position;
                    allImages.get(position).setSelected(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                    indicatorRecycler.scrollToPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        indicatorRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //descomente la siguiente condición para controlar recyclerVer visibilidad automáticamente
                // cuando se hace clic en la imagen también descomente la condición establecida en onClickListener de la
                // imagen en el adaptador ImagesPagerAdapter

                /*if(viewVisibilityController == 0){
                    indicatorRecycler.setVisibility(View.VISIBLE);
                    visibiling();
                }else{
                    viewVisibilitylooper++;
                }*/
                return false;
            }
        });

    }


    //este método de la interfaz imageIndicatorListerner ayuda en la comunicación entre el fragmento y el
    // adaptador recyclerView cada vez que se hace clic en un iten del adaptador, la posición de ese elemento
    // se comunica en el fragmento y la posición del viewPager se ajusta de la siguiente manera
    // @param ImagePosition La posición de un elemento de imagen en el adaptador RecyclerView

    @Override
    public void onImageIndicatorClicked(int ImagePosition) {

//         las siguientes líneas de código resaltan la imagen seleccionada actualmente en el
//         indicadorRecycler con respecto a la posición viewPager
        if(previousSelected != -1){
            allImages.get(previousSelected).setSelected(false);
            previousSelected = ImagePosition;
            indicatorRecycler.getAdapter().notifyDataSetChanged();
        }else{
            previousSelected = ImagePosition;
        }

        imagePager.setCurrentItem(ImagePosition);
    }

   //el adaptador de imageViewPager
    private class ImagesPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return allImages.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup containerCollection, int position) {
            LayoutInflater layoutinflater = (LayoutInflater) containerCollection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutinflater.inflate(R.layout.picture_browser_pager,null);
               image = view.findViewById(R.id.image);

            setTransitionName(image, String.valueOf(position)+"picture");

            pictureFacer pic = allImages.get(position);
            Glide.with(animeContx)
                    .load(pic.getPicturePath())
                    .apply(new RequestOptions().fitCenter())
                    .into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(indicatorRecycler.getVisibility() == View.GONE){
                        indicatorRecycler.setVisibility(View.VISIBLE);
                    }else{
                        indicatorRecycler.setVisibility(View.GONE);
                    }

                    // descomente la siguiente condición y comente la anterior para controlar
                    // recyclerView visibilidad automáticamente cuando se hace clic en la imagen

                    /*if(viewVisibilityController == 0){
                     indicatorRecycler.setVisibility(View.VISIBLE);
                     visibiling();
                 }else{
                     viewVisibilitylooper++;
                 }*/

                }
            });



            ((ViewPager) containerCollection).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup containerCollection, int position, Object view) {
            ((ViewPager) containerCollection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View) object);
        }
    }

    //   función para controlar la visibilidad del indicador recyclerView
    private void visibiling(){
        viewVisibilityController = 1;
        final int checker = viewVisibilitylooper;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(viewVisibilitylooper > checker){
                   visibiling();
                }else{
                   indicatorRecycler.setVisibility(View.GONE);
                   viewVisibilityController = 0;

                   viewVisibilitylooper = 0;
                }
            }
        }, 4000);
    }

}

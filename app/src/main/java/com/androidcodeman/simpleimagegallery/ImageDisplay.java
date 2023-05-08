package com.androidcodeman.simpleimagegallery;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.Fade;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.androidcodeman.simpleimagegallery.fragments.pictureBrowserFragment;
import com.androidcodeman.simpleimagegallery.utils.MarginDecoration;
import com.androidcodeman.simpleimagegallery.utils.PicHolder;
import com.androidcodeman.simpleimagegallery.utils.itemClickListener;
import com.androidcodeman.simpleimagegallery.utils.pictureFacer;
import com.androidcodeman.simpleimagegallery.utils.picture_Adapter;
import java.util.ArrayList;

/**
 * Author FTYGY
 *
 Esta actividad obtiene una ruta a una carpeta que contiene imágenes de la intención de MainActivity y muestra todas
 las imágenes de la carpeta dentro de un RecyclerView
 */

public class ImageDisplay extends AppCompatActivity implements itemClickListener {

    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;
    ProgressBar load;
    String foldePath;
    TextView folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        folderName = findViewById(R.id.foldername);
        folderName.setText(getIntent().getStringExtra("folderName"));

        foldePath =  getIntent().getStringExtra("folderPath");
        allpictures = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);


        if(allpictures.isEmpty()){
            load.setVisibility(View.VISIBLE);
            allpictures = getAllImagesByFolder(foldePath);
            imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,this));
            load.setVisibility(View.GONE);
        }else{

        }
    }

   //      * @param titular El ViewHolder para la imagen en la que se ha hecho clic
   //     * @param posición La posición en la cuadrícula de la imagen en la que se hizo clic
   //     * @param fotos Una ArrayList de todos los elementos en el adaptador
    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {
        pictureBrowserFragment browser = pictureBrowserFragment.newInstance(pics,position,ImageDisplay.this);

        //  Tenga en cuenta que necesitamos la verificación de la versión de la API aquí porque las clases de transición
        //  reales (por ejemplo, Fade) no están en la biblioteca de soporte y solo están disponibles en API 21+. Los métodos
        //  a los que llamamos en el Fragmento ESTÁN disponibles en la biblioteca de soporte (aunque no hacen nada en API < 21)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //browser.setEnterTransition(new Slide());
            //browser.setExitTransition(new Slide()); uncomment this to use slide transition and comment the two lines below
            browser.setEnterTransition(new Fade());
            browser.setExitTransition(new Fade());
        }

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.picture, position+"picture")
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) {

    }

    //Este método obtiene todas las imágenes de las rutas de carpeta pasadas como una
    // cadena al método y devuelve y ArrayList de pictureFacer un objeto personalizado
    // que contiene datos de una imagen determinada @param ruta de acceso una cadena
    // correspondiente a una ruta de carpeta en el almacenamiento externo del dispositivo

    public ArrayList<pictureFacer> getAllImagesByFolder(String path){
        ArrayList<pictureFacer> images = new ArrayList<>();
        Uri allVideosuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = ImageDisplay.this.getContentResolver().query( allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, null);
        try {
            cursor.moveToFirst();
            do{
                pictureFacer pic = new pictureFacer();

                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                images.add(pic);
            }while(cursor.moveToNext());
            cursor.close();
            ArrayList<pictureFacer> reSelection = new ArrayList<>();
            for(int i = images.size()-1;i > -1;i--){
                reSelection.add(images.get(i));
            }
            images = reSelection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }


}

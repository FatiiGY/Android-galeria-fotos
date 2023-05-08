package com.androidcodeman.simpleimagegallery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.androidcodeman.simpleimagegallery.utils.MarginDecoration;
import com.androidcodeman.simpleimagegallery.utils.PicHolder;
import com.androidcodeman.simpleimagegallery.utils.imageFolder;
import com.androidcodeman.simpleimagegallery.utils.itemClickListener;
import com.androidcodeman.simpleimagegallery.utils.pictureFacer;
import com.androidcodeman.simpleimagegallery.utils.pictureFolderAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Author FTYGY
 *
 * La actividad principal se inicia y carga todas las carpetas que contienen imágenes en
 * un RecyclerView estas carpetas se obtienen del MediaStore mediante el método getPicturePaths()
 *
 */
public class MainActivity extends AppCompatActivity implements itemClickListener {

    RecyclerView folderRecycler;
    TextView empty;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * Se solicita el permido para acceder a archivos multimedia y leer imagenes del dispositivo
     * esto sirve para api 21 y superior, si no se comprueba la app fallara
     *
     * Se configura el RecyclerView y se obtienen las carpetas que contienen imagenes en dispositivo
     * el getPicturePaths() regresa un ArrayList de objetos imageFolder que luego se usa para
     * crear un RecyclerView Adapter que se configura con RecyclerView
     *
     * @param savedInstanceState se guarda el estado del activity
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        //____________________________________________________________________________________

        empty =findViewById(R.id.empty);

        folderRecycler = findViewById(R.id.folderRecycler);
        folderRecycler.addItemDecoration(new MarginDecoration(this));
        folderRecycler.hasFixedSize();
        ArrayList<imageFolder> folds = getPicturePaths();

        if(folds.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            RecyclerView.Adapter folderAdapter = new pictureFolderAdapter(folds,MainActivity.this,this);
            folderRecycler.setAdapter(folderAdapter);
        }

        changeStatusBarColor();
    }

   //obtiene todas las carpetas con imágenes en el dispositivo y carga cada una de ellas en un objeto personalizado imageFolder
   // devuelve un objeto ArrayList de estos objetos personalizados
    private ArrayList<imageFolder> getPicturePaths(){
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = this.getContentResolver().query(allImagesuri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do{
                imageFolder folds = new imageFolder();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //String folderpaths =  datapath.replace(name,"");
                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder+"/"));
                folderpaths = folderpaths+folder+"/";
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths);

                    folds.setPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(datapath);//Si la carpeta sólo tiene una imagen, esta línea ayuda a establecerla como primera para evitar la imagen en blanco en ItemView
                    folds.addpics();
                    picFolders.add(folds);
                }else{
                    for(int i = 0;i<picFolders.size();i++){
                        if(picFolders.get(i).getPath().equals(folderpaths)){
                            picFolders.get(i).setFirstPic(datapath);
                            picFolders.get(i).addpics();
                        }
                    }
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0;i < picFolders.size();i++){
            Log.d("picture folders",picFolders.get(i).getFolderName()+" and path = "+picFolders.get(i).getPath()+" "+picFolders.get(i).getNumberOfPics());
        }

        //orden inverso ArrayList
       /* ArrayList<imageFolder> reverseFolders = new ArrayList<>();

        for(int i = picFolders.size()-1;i > reverseFolders.size()-1;i--){
            reverseFolders.add(picFolders.get(i));
        }*/

        return picFolders;
    }


    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

    }

    // Cada vez que se hace clic en un elemento de RecyclerView se ejecuta este método desde
    // la implementación del transitListerner en esta actividad, esto es posible porque esta
    // clase se pasa como parámetro en la creación del adaptador de RecyclerView, consulte
    // la clase de adaptador para comprender mejor lo que está sucediendo aquí
    // @param pictureFolderPath una cadena correspondiente a una ruta de carpeta en el almacenamiento
    // externo del dispositivo
    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) {
        Intent move = new Intent(MainActivity.this,ImageDisplay.class);
        move.putExtra("folderPath",pictureFolderPath);
        move.putExtra("folderName",folderName);

        //move.putExtra("recyclerItemSize",getCardsOptimalWidth(4));
        startActivity(move);
    }


    // Altura de la barra de estado predeterminada 24dp, con código API nivel 24
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor()
    {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.black));

    }

    private String ruta;
    //Para reproducir el video desde la app
    public void btnDesdeAppClick ( View v ) {
        // Reproducir un video incluido en la app en la carpeta raw
      //  ruta = "android.resource://" + this.getPackageName() + "/" + R.raw.videointro;
        lanzarVideoActiviy ();
    }

    private void  lanzarVideoActiviy () {
        Intent intent = new Intent ( this, VideoActivity.class ) ;
        intent.putExtra ( "rutaVideo", ruta );
        startActivity ( intent );
    }
    String nombre;
    public void addAlbum(View v){

        AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle("Ingrese el nombre del album");
        final EditText texto = new EditText(MainActivity.this);
        texto.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogo.setView(texto);

        dialogo.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               nombre = texto.getText().toString();

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                     //   File.separator + "/DCIM/Camera/" +File.separator + nombre);
                File.separator + "/DCIM/" +File.separator + nombre);

                file.mkdirs();
            }
        });
        dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialogo.show();
    }

    public void anterior(View v){
        Intent inte = new Intent(this,CamaraActivity.class);
        startActivity(inte);
        finish();
    }
}

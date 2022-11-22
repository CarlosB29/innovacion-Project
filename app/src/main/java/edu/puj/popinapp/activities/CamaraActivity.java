package edu.puj.popinapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import edu.puj.popinapp.R;


public class CamaraActivity extends AppCompatActivity {
    // Setup del logger para esta clase
    private static final String TAG = CamaraActivity.class.getName();
    private Logger logger = Logger.getLogger(TAG);

    //Definir el id del permiso
    private final int CAMERA_PERMISSION_ID = 101;
    private final int GALLERY_PERMISSION_ID = 102;
    String cameraPerm = Manifest.permission.CAMERA;

    //Para mostrar imagen de la camara
    ImageView imageView;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);
        //Pedir el permiso cuando la aplicacion inicie.
        logger.info("Voy a solicitar el permiso");
        requestPermission(this, cameraPerm, "Permiso para utilizar la camara", CAMERA_PERMISSION_ID);
        initView();
        imageView = findViewById(R.id.imageView3);
    }

    private void requestPermission(Activity context, String permission, String justification, int id){
        // Verificar si no hay permisos
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show();
            }
            // request the permission.
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }

    private void initView(){
        if (ContextCompat.checkSelfPermission(this, cameraPerm)
                != PackageManager.PERMISSION_GRANTED){
            logger.warning("No se pudo obtener el permiso de la camara");
        }else {
            logger.info("Permiso concedido");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_ID){
            initView();
        }
    }

    public void startCamera(View view){
        if (ContextCompat.checkSelfPermission(this, cameraPerm)
                == PackageManager.PERMISSION_GRANTED){
            openCamera();
        }else {
            logger.warning("Fallo al conseguir el permiso");
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                logger.warning(ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_PERMISSION_ID);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "lo_que_sea",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        logger.info("Ruta:"+currentPhotoPath);
        return image;
    }

    public void startGallery(View view){
        Intent pickGalleryImage = new Intent(Intent.ACTION_PICK);
        pickGalleryImage.setType("image/*");
        startActivityForResult(pickGalleryImage, GALLERY_PERMISSION_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case CAMERA_PERMISSION_ID:
                    imageView.setImageURI(Uri.parse(currentPhotoPath));
                    logger.info("Se capturo la imagen");
                    break;
                case GALLERY_PERMISSION_ID:
                    Uri imageUri = data.getData();
                    imageView.setImageURI(imageUri);
                    logger.info("Imagen cargada exitosamente");
                    break;
            }
        }
    }
}
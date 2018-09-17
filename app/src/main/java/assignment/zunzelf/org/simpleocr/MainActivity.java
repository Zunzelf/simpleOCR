package assignment.zunzelf.org.simpleocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import assignment.zunzelf.org.simpleocr.image.Processor;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    ImageView i1, i2;
    Uri imageURI;
    Bitmap bitmap, mBm;
    Processor proc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proc = new Processor();
        i1 = findViewById(R.id.imageView);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void openGallery(){
        Intent gallery =  new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK && requestCode == PICK_IMAGE){
            // Load Image File
            imageURI = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
//                i1.setImageBitmap(bitmap);
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                mBm = proc.createBlackAndWhite(bitmap);
                String res = proc.seekObjects(mBm);
                i1.setImageBitmap(mBm);
                TextView txt = (TextView) findViewById(R.id.txt1);
                txt.setText(res);
                Log.d("main","done!");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}

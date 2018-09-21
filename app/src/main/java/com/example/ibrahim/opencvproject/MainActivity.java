package com.example.ibrahim.opencvproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("jniLips");
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    rgba=new Mat();
                    grayMat=new Mat();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    Mat rgba;
    Mat grayMat;
    private Button mBtnGray;
    private ImageView mImageView;
    private Bitmap mGrayBitmap,mImagBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenCVLoader.initDebug();


        mImageView=findViewById(R.id.imgGallary);
        findViewById(R.id.btnGallary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,100);
            }
        });
        findViewById(R.id.btnGray).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertToGray(view);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK && data!=null){

            Uri imgUri=data.getData();
            try {
                mImagBitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgUri);

            }catch (IOException e){
                e.printStackTrace();
            }
            mImageView.setImageBitmap(mImagBitmap);
        }
    }
    public void convertToGray(View v){
         rgba=new Mat();
         grayMat=new Mat();
        BitmapFactory.Options o=new BitmapFactory.Options();
        o.inDither=false;
        o.inSampleSize=4;

        int width=mImagBitmap.getWidth();
        int height=mImagBitmap.getHeight();
        mGrayBitmap=Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        Utils.bitmapToMat(mImagBitmap,rgba);
        Imgproc.cvtColor(rgba,grayMat,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(grayMat,mGrayBitmap);
        mImageView.setImageBitmap(mGrayBitmap);

    }

    @Override
    protected void onResume() {
        super.onResume();

        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}

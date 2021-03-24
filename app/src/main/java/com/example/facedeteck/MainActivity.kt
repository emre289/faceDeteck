package com.example.facedeteck

import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var imageView:ImageView?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView=findViewById(R.id.imageView)
       var chose:Button=findViewById(R.id.button)
        chose.setOnClickListener {
            var i:Intent= Intent();
            i.type="image/*"
            i.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i,"Chose image"),121)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==121){
            imageView?.setImageURI(data?.data)
            var bmp:Bitmap?=null
            try {
                bmp=MediaStore.Images.Media.getBitmap(this.contentResolver,data?.data!!)

            } catch (e: IOException){
                e.printStackTrace()
            }
            val mutableBmp=bmp!!.copy(Bitmap.Config.ARGB_8888,true)
            val canvas=Canvas(mutableBmp)

            val image:FirebaseVisionImage
            try {
                image = FirebaseVisionImage.fromFilePath(applicationContext,data?.data!!)
                val options=FirebaseVisionFaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build()
                val detector=FirebaseVision.getInstance()
                    .getVisionFaceDetector(options)


                var result=detector.detectInImage(image)
                    .addOnSuccessListener {
                        for (face in it) {
                            val bounds = face.boundingBox
                            var p:Paint=Paint()
                            p.color=Color.YELLOW
                            p.style=Paint.Style.STROKE
                            canvas.drawRect(bounds,p)
                            imageView?.setImageBitmap(mutableBmp)
                            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                            // nose available):
                            val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                            leftEar?.let {
                                val leftEarPos = leftEar.position

                            }

                            // If contour detection was enabled:
                            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                            val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM).points

                            // If classification was enabled:
                            if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                val smileProb = face.smilingProbability
                            }
                            if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                val rightEyeOpenProb = face.rightEyeOpenProbability
                            }

                            // If face tracking was enabled:
                            if (face.trackingId != null) {
                                val id = face.trackingId
                            }
                        }


                    }
                    .addOnFailureListener {

                    }

            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }

}
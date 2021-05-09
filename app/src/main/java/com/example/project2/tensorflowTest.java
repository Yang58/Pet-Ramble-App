package com.example.project2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.project2.Main.MainActivity;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.TensorFlowLite;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.label.TensorLabel;
import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tensorflowTest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tensorflowTest extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static View v;
    private static final int REQUEST_IMAGE_1 = 1;
    private static Bitmap selectImage;

    public tensorflowTest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tensorflowTest.
     */
    // TODO: Rename and change types and number of parameters
    public static tensorflowTest newInstance(String param1, String param2) {
        tensorflowTest fragment = new tensorflowTest();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tensorflow_test, container, false);
        v=view;
        TextView tv_output = view.findViewById(R.id.tf_result);
        Button b = view.findViewById(R.id.tf_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_1);

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(v.getContext().getContentResolver(), uri);
                selectImage=bitmap;
                // Log.d(TAG, String.valueOf(bitmap));
                int cx=224, cy=224;
                Bitmap scaeldBitmap = Bitmap.createScaledBitmap(selectImage,cx,cy,false);
                int pixels[] = new int[cx*cy];
                scaeldBitmap.getPixels(pixels,0,cx,0,0,cx,cy);
                ByteBuffer inputImage=getInputImage_2(pixels,cx,cy);
                Interpreter tf_lite = getTfliteInterpreter("graph.tflite");
                float[][] pred = new float[1][121];
                tf_lite.run(inputImage, pred);
                TextView tv = v.findViewById(R.id.tf_result);

                ArrayList<Float> topList= new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                String labStr = "affenpinscher, Afghan_hound, African_hunting_dog, Airedale, American_Staffordshire_terrier, Appenzeller, Australian_terrier, basenji, basset, beagle, Bedlington_terrier, Bernese_mountain_dog, black-and-tan_coonhound, Blenheim_spaniel, bloodhound, bluetick, Border_collie, Border_terrier, borzoi, Boston_bull, Bouvier_des_Flandres, boxer, Brabancon_griffon, briard, Brittany_spaniel, bull_mastiff, cairn, Cardigan, Chesapeake_Bay_retriever, Chihuahua, chow, clumber, cocker_spaniel, collie, curly-coated_retriever, Dandie_Dinmont, dhole, dingo, Doberman, English_foxhound, English_setter, English_springer, EntleBucher, Eskimo_dog, flat-coated_retriever, French_bulldog, German_shepherd, German_short-haired_pointer, giant_schnauzer, golden_retriever, Gordon_setter, Greater_Swiss_Mountain_dog, Great_Dane, Great_Pyrenees, groenendael, Ibizan_hound, Irish_setter, Irish_terrier, Irish_water_spaniel, Irish_wolfhound, Italian_greyhound, Japanese_spaniel, keeshond, kelpie, Kerry_blue_terrier, komondor, kuvasz, Labrador_retriever, Lakeland_terrier, Leonberg, Lhasa, malamute, malinois, Maltese_dog, Mexican_hairless, miniature_pinscher, miniature_poodle, miniature_schnauzer, Newfoundland, Norfolk_terrier, Norwegian_elkhound, Norwich_terrier, Old_English_sheepdog, otterhound, papillon, Pekinese, Pembroke, Pomeranian, pug, redbone, Rhodesian_ridgeback, Rottweiler, Saint_Bernard, Saluki, Samoyed, schipperke, Scotch_terrier, Scottish_deerhound, Sealyham_terrier, Shetland_sheepdog, shiba_inu, Shih-Tzu, Siberian_husky, silky_terrier, soft-coated_wheaten_terrier, Staffordshire_bullterrier, standard_poodle, standard_schnauzer, Sussex_spaniel, Tibetan_mastiff, Tibetan_terrier, toy_poodle, toy_terrier, vizsla, Walker_hound, Weimaraner, Welsh_springer_spaniel, West_Highland_white_terrier, whippet, wire-haired_fox_terrier, Yorkshire_terrier";
                for(String i : labStr.split(",")){
                    labels.add(i);
                }
                tv.setText("");
                for (int i = 0; i<pred[0].length; i++){
                    if(pred[0][i]*100>5)
                        tv.setText(tv.getText() + labels.get(i)+"\t"+String.valueOf(pred[0][i]*100)+"%\n");
                }
                ImageView imageView = (ImageView) v.findViewById(R.id.tf_img);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ByteBuffer getInputImage_2(int[] pixels, int cx, int cy) {
        ByteBuffer input_img = ByteBuffer.allocateDirect(cx * cy * 3 * 4);
        input_img.order(ByteOrder.nativeOrder());

        for (int i = 0; i < cx * cy; i++) {
            int pixel = pixels[i];        // ARGB : ff4e2a2a

            input_img.putFloat(((pixel >> 16) & 0xff) / (float) 255);
            input_img.putFloat(((pixel >> 8) & 0xff) / (float) 255);
            input_img.putFloat(((pixel >> 0) & 0xff) / (float) 255);
        }

        return input_img;
    }
    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile((MainActivity)v.getContext(), modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public void callresult(){

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                        .build();

        TensorImage tImage = new TensorImage(DataType.UINT8);

        tImage.load(selectImage);
        tImage = imageProcessor.process(tImage);

        TensorBuffer probabilityBuffer =
                TensorBuffer.createFixedSize(new int[]{1, 1001}, DataType.UINT8);

        Interpreter tflite = null;
        try{
            MappedByteBuffer tfliteModel
                    = FileUtil.loadMappedFile(v.getContext(),
                    "converted_model.tflite");
            tflite = new Interpreter(tfliteModel);
        } catch (IOException e){
            Log.e("tfliteSupport", "Error reading model", e);
        }

        if(null != tflite) {
            tflite.run(tImage.getBuffer(), probabilityBuffer.getBuffer());
        }

        final String ASSOCIATED_AXIS_LABELS = "labels.txt";
        List associatedAxisLabels = null;

        try {
            associatedAxisLabels = FileUtil.loadLabels(v.getContext(),ASSOCIATED_AXIS_LABELS);
        } catch (IOException e) {
            Log.e("tfliteSupport", "Error reading label file", e);
        }

        TensorProcessor probabilityProcessor =
                new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();

        if (null != associatedAxisLabels) {
            // Map of labels and their corresponding probability
            TensorLabel labels = new TensorLabel(associatedAxisLabels,
                    probabilityProcessor.process(probabilityBuffer));
            Log.wtf("",labels.getCategoryList().toString());

            // Create a map to access the result based on label
            Map floatMap = labels.getMapWithFloatValue();
        }
    }
}
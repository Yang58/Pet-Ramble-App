package com.example.project2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tensorflowTest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tensorflowTest extends AppCompatActivity {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static View v;
    private static final int REQUEST_IMAGE_1 = 1;
    private static ArrayList<TextView> listBtn = new ArrayList<>();
    private static ArrayList<TextView> text = new ArrayList<>();

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
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tensorflow_test);
        //종류 표시될 버튼들
        if(listBtn.size()==0) {
            listBtn.add(findViewById(R.id.tf_btn_1));
            listBtn.add(findViewById(R.id.tf_btn_2));
            listBtn.add(findViewById(R.id.tf_btn_3));
            listBtn.add(findViewById(R.id.tf_btn_4));
            listBtn.add(findViewById(R.id.tf_btn_5));

            text.add(findViewById(R.id.t1));
            text.add(findViewById(R.id.t2));
            text.add(findViewById(R.id.t3));
            text.add(findViewById(R.id.t4));
            text.add(findViewById(R.id.t5));
            text.add(findViewById(R.id.t6));

            for(TextView b : listBtn){
                b.setVisibility(View.GONE);
            }
            for(TextView a : text){
                a.setVisibility(View.GONE);
            }
        }

        ImageView imageView = (ImageView) findViewById(R.id.tf_img);
        imageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                selfPermissionCheck2();
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });

        Button b = findViewById(R.id.tf_btn_getImg);
        b.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                selfPermissionCheck();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_1);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf("단계","onActivityResult, "+resultCode);
        if (resultCode == RESULT_OK) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap=null;
                if(requestCode == 12) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                }else if(requestCode == REQUEST_IMAGE_1){
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                }

                ImageView imageView = (ImageView) findViewById(R.id.tf_img);
                imageView.setImageBitmap(bitmap);

                Bitmap selectImage=bitmap;
                // Log.d(TAG, String.valueOf(bitmap));
                int cx=224, cy=224;
                Bitmap scaeldBitmap = Bitmap.createScaledBitmap(selectImage,cx,cy,false);
                int pixels[] = new int[cx*cy];
                scaeldBitmap.getPixels(pixels,0,cx,0,0,cx,cy);
                ByteBuffer inputImage=getInputImage_2(pixels,cx,cy);
                Interpreter tf_lite = null;
                tf_lite = getTfliteInterpreter("graph.tflite");
                float[][] pred = new float[1][121];
                tf_lite.run(inputImage, pred);

                //요기에 견종, 그 견종일 확률 저장됨
                HashMap<String, Float> topList = new HashMap<>();
                //견종 목록인데 나중에 labels.txt 등으로 외부에 뺄 예정
                String labStr = "아펜핀셔, 아프간 하운드, 아프리카 사냥개, 에어데일 테리어, 아메리칸 스태퍼드셔 테리어, 아펜젤러 세넨훈드, 오스트레일리안 테리어, 바센지, 바셋하운드, 비글, 베들링턴 테리어, 버니즈 마운틴 도그, 블랙 앤 탄 쿤하운드, 킹 찰스 스패니얼, 블러드 하운드, 블루틱 쿤하운드, 보더콜리, 보더 테리어, 보르조이, 보스턴 테리어, 부비에 데 플랑드르, 복서, 브뤼셀 그리펀, 브리아드 , 브리트니 스파니엘, 불마스티프, 케언 테리어, 카디건 웰시 코기, 체서피크 베이 리트리버, 치와와, 차우차우, 클럼버 스파니엘, 코커 스파니엘, 콜리, 컬리 코티드 리트리버, 댄디 딘몬트 테리어, 승냥이, 딩고, 도베르만, 잉글리시 폭스하운드, 르웰린, 잉글리시 스프링어 스패니얼, 엔틀버쳐, 에스키모, 플랫 코티드 리트리버, 프렌치 불도그, 저먼 셰퍼드, 저먼 쇼트헤어드 포인터, 자이언트 슈나우저, 골든 리트리버, 고든 세터, 그레이터 스위스 마운틴 도그, 그레이트 데인, 그레이트 피레니즈, 그루넨달, 이비전 하운드, 아이리시 세터, 아이리시 테리어, 아이리시 워터 스파니엘, 아이리시 울프하운드, 이탈리안 그레이하운드, 재패니즈 스파니엘, 키스혼드, 켈피, 케리 블루 테리어, 코몬도르, 쿠바츠, 래브라도 리트리버, 레이클랜드 테리어, 레온베르거," +
                        " 라사압소, 말라뮤트, 말리노이즈, 말티즈, 멕시칸 헤어리스, 미니어처 핀셔, 미니어처 푸들, 미니어처 슈나우저, 뉴펀들랜드, 노퍽 테리어, 노르웨이언 엘크하운드, 노리치 테리어, 올드 잉글리시 쉽독, 오터 하운드, 파피용 , 페키니즈, 펨브록 웰시 코기, 포메라니안, 퍼그, 레드본 쿤하운드, 로디지안 리지백, 로트바일러, 세인트버나드, 살루키, 사모예드, 스키퍼키, 스코티시 테리어, 스코티시 디어하운드, 실리엄 테리어, 셔틀랜드 쉽독, 시바견, 시츄, 시베리안 허스키, 오스트레일리안 실키 테리어, 아이리쉬 소프트코티드 휘튼 테리어, 스타포드셔 불 테리어, 푸들, 스탠더드 슈나우저, 서식스 스파니엘, 티베탄 마스티프, 티베탄 테리어, 토이 푸들, 토이 테리어, 비즐라, 트리잉 워커 쿤하운드, 와이머라너, 웰시 스프링어 스파니엘, 웨스트 하일랜드 화이트 테리어, 휘핏, 와이어 폭스 테리어, 요크셔 테리어";
                //임시로 견종 목록 정리
                ArrayList<String> labels = new ArrayList<>();
                for(String i : labStr.split(",")){
                    labels.add(i);
                }
                //실제로 사용할 견종, 확률
                for (int i = 0; i<pred[0].length; i++){
                    topList.put(labels.get(i),  pred[0][i]*100);
                    Log.wtf(labels.get(i),pred[0][i]*100+"");
                }
                //내림차순 정렬
                List<String> keySetList = new ArrayList<>();
                keySetList.addAll(topList.keySet());
                Collections.sort(keySetList, (o1, o2) -> (topList.get(o2).compareTo(topList.get(o1))));

                for(TextView a : text){
                    a.setVisibility(View.GONE);
                }
                for(TextView b : listBtn){
                    b.setVisibility(View.GONE);
                }

                int cnt=0;
                for(String key : keySetList) {
                    if(cnt>listBtn.size()-1) break;
                    Log.wtf("키", key);
                    TextView a = text.get(cnt);
                    TextView b = listBtn.get(cnt);
                    a.setVisibility(View.VISIBLE);
                    b.setVisibility(View.VISIBLE);
                    b.setText("");
                    b.setText(key+"\n"+String.format("%.2f",topList.get(key))+"%");
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listBtn=new ArrayList<>();
                            text=new ArrayList<>();
                            Intent intent = new Intent();
                            intent.putExtra("my_data",key);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    });
                    cnt++;
                }

                tf_lite.close();
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
            return new Interpreter(loadModelFile(this, modelPath));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selfPermissionCheck(){
        String tmp = "";
        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        tmp+=Manifest.permission.READ_EXTERNAL_STORAGE+" ";
        ActivityCompat.requestPermissions(this,tmp.trim().split(" "),1);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selfPermissionCheck2(){
        String tmp = "";
        checkSelfPermission(Manifest.permission.CAMERA);
        tmp+=Manifest.permission.CAMERA+" ";
        ActivityCompat.requestPermissions(this,tmp.trim().split(" "),1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        listBtn=new ArrayList<>();
        text=new ArrayList<>();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bitmap selectImage = null;
        ArrayList<TextView> listBtn = new ArrayList<>();
        ArrayList<TextView> text = new ArrayList<>();
        for(TextView b : listBtn){
            b.setVisibility(View.GONE);
        }
        for(TextView a : text){
            a.setVisibility(View.GONE);
        }
    }
}
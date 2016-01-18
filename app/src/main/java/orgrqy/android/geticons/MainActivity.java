package orgrqy.android.geticons;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{

    PackageManager pm = null;
    TextView textView =null;
    Spinner spinner = null;

    Button button = null;


    List<ApplicationInfo> applicationInfos = null;
    int index =0;
    int size=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        pm = getPackageManager();
        applicationInfos  = pm.getInstalledApplications(0);
        List<String> list = new ArrayList<>();
        size = applicationInfos.size();
        for(ApplicationInfo info :applicationInfos){
            String lable = info.packageName;

            list.add(lable);
        }

        imageView = (ImageView) findViewById(R.id.img);
        textView = (TextView) findViewById(R.id.textinfo);
        textView.setOnClickListener(this);
        imageView.setOnClickListener(this);
        spinner= (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.get);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int posision  = spinner.getSelectedItemPosition();
                ApplicationInfo info = applicationInfos.get(posision);
                Drawable d = pm.getApplicationIcon(info);
                Bitmap bitmap = drawableToBitMap(d);
                Bitmap roundColorBitMap = getRoundedCornerBitmap(bitmap,20f);
                File file= new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                +"/"+info.packageName+".png");
                FileOutputStream fos;
                imageView.setBackground(new BitmapDrawable(roundColorBitMap));

                try {
                     fos = new FileOutputStream(file);

                    roundColorBitMap.compress(Bitmap.CompressFormat.PNG,100,fos);

                    fos.flush();
                    fos.close();


                } catch (FileNotFoundException e) {
                    Log.d("rongqingyu","FileNotFoundException");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("rongqingyu","IOException");

                    e.printStackTrace();
                }


            }
        });






        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ApplicationInfo applicationinfo =applicationInfos.get(position);
                Drawable d = pm.getApplicationIcon(applicationinfo);
                imageView.setBackground(d);
                textView.setText(pm.getApplicationLabel(applicationinfo));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Log.d("rongqingyu","(w,h):("+w+","+h+")");
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xffFFFFFF;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0,0 , 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }




    private Bitmap drawableToBitMap(Drawable d){
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), d.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        d.draw(canvas);
        return bitmap;

    }


    @Override
    public void onClick(View v) {
        Drawable d =null;
        int  i = index++ % size;
        spinner.setSelection(i);
        ApplicationInfo applicationinfo =applicationInfos.get(i );
        d = pm.getApplicationIcon(applicationinfo);
        imageView.setBackground(d);
        textView.setText(pm.getApplicationLabel(applicationinfo));
    }

    ImageView imageView =null;




}

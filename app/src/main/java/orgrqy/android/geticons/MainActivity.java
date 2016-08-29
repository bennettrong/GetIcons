package orgrqy.android.geticons;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
    TextView radiotip =null;
    Button button = null;
    SeekBar mSeekBar;
    List<ApplicationInfo> applicationInfos = null;
    int index =0;
    int size=0;
    float ratio=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSeekBar.setMax(100);
        pm = getPackageManager();
        applicationInfos  = pm.getInstalledApplications(0);
        List<String> list = new ArrayList<>();
        size = applicationInfos.size();
        for(ApplicationInfo info :applicationInfos){
            String lable = info.packageName;

            list.add(lable);
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                ratio= i/100.0f;
                Log.d("rongqingyu","seekbar i :"+i+"ratio:"+ratio);
                radiotip.setText("ratio:"+ratio);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        imageView = (ImageView) findViewById(R.id.img);
        textView = (TextView) findViewById(R.id.textinfo);
        textView.setOnClickListener(this);
        imageView.setOnClickListener(this);
        spinner= (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.get);
        radiotip = (TextView) findViewById(R.id.radiotip);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int posision  = spinner.getSelectedItemPosition();
                ApplicationInfo info = applicationInfos.get(posision);
                Drawable d = pm.getApplicationIcon(info);

                Bitmap bitmap = drawableToBitMap(d);


                Bitmap roundColorBitMap = getRoundedCornerBitmap(bitmap,ratio);
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

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.photo1);
        List<int[]> result = new ArrayList<>();
        try {
            result = MMCQ.compute(icon, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        icon.recycle();

        int[] dominantColor = result.get(0);
        findViewById(R.id.dominant_color).setBackgroundColor(
                Color.rgb(dominantColor[0], dominantColor[1], dominantColor[2]));

        LinearLayout palette = (LinearLayout) findViewById(R.id.palette);
        for (int i = 1; i < result.size(); i++) {
            View swatch = new View(this);
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            int swatchWidth = (int) (48 * displayMetrics.density + 0.5f);
            int swatchHeight = (int) (48 * displayMetrics.density + 0.5f);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(swatchWidth, swatchHeight);
            int margin = (int) (4 * displayMetrics.density + 0.5f);
            layoutParams.setMargins(margin, 0, margin, 0);
            swatch.setLayoutParams(layoutParams);

            int[] color = result.get(i);
            int rgb = Color.rgb(color[0], color[1], color[2]);
            swatch.setBackgroundColor(rgb);
            palette.addView(swatch);
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float ratio) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xffFFFFFF;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0,0 , 0, 0);
        paint.setColor(color);


        int roundPxy= (int) (ratio* Math.max(w,h));
        Log.d("rongqingyu","(w,h):("+w+","+h+")"+" ratio:"+ratio +" roundPxy:"+roundPxy);
        canvas.drawRoundRect(rectF, roundPxy, roundPxy, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }




    private Bitmap drawableToBitMap(Drawable d){
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(),  d.getIntrinsicHeight(), d.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
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

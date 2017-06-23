package com.skycaster.geomapper.activity;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.skycaster.geomapper.R;

public class SurfaceDraw  extends Activity {
    private SurfaceView sf;
    private SurfaceHolder  sfh;   //surfaceView的 控制器
    private boolean isContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view_test);
        sf = (SurfaceView) this.findViewById(R.id.SurfaceView01);
        //得到控制器
        sfh = sf.getHolder();
        //对 surfaceView 进行操作
        sfh.addCallback(new DoThings());// 自动运行surfaceCreated以及surfaceChanged
    }


    private class DoThings implements SurfaceHolder.Callback{
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            //在surface的大小发生改变时激发
            System.out.println("surfaceChanged");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder){
            isContinue=true;
            new Thread(){
                public void run() {
                    while(isContinue){
                        try{
                            //1.这里就是核心了， 得到画布 ，然后在你的画布上画出要显示的内容
                            Canvas c = sfh.lockCanvas(new Rect(0, 0, 200, 200));
                            //2.开画
                            Paint p =new Paint();
                            p.setColor(Color.rgb( (int)(Math.random() * 255),
                                    (int)(Math.random() * 255) ,  (int)(Math.random() * 255)));
                            Rect aa  =  new Rect( (int)(Math.random() * 100) ,
                                    (int)(Math.random() * 100)
                                    ,(int)(Math.random() * 500)
                                    ,(int)(Math.random() * 500) );
                            c.drawRect(aa, p);
                            //3. 解锁画布   更新提交屏幕显示内容
                            sfh.unlockCanvasAndPost(c);
                            try {
                                Thread.sleep(1000);

                            } catch (Exception e) {
                            }

                        }catch (NullPointerException e){
                            isContinue=false;
                            break;
                        }
                    }
                }
            }.start();

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //销毁时激发，一般在这里将画图的线程停止、释放。
            System.out.println("surfaceDestroyed==");
            isContinue=false;
        }
    }
}

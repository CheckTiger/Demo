package cn.sxh.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther snowTiger
 * @mail SnowTigerSong@gmail.com
 * @time 2017/3/29 20:07
 */

public class NineView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Point[][] points = new Point[3][3];
    private boolean isInit,isSelect,isFinish,movingNoPoint;
    private float width,height,offsetsX,offsetsY,movingX,movingY;//宽高
    private float bitmapR;//图片资源的半径
    private Bitmap pointNormal;//正常的点
    private Bitmap pointPressed;//按下的点
    private Bitmap pointError;//错误的点
    private Bitmap LineError;//错误的线
    private Bitmap LineNormal;//正常的线

    private Matrix matrix = new Matrix();
    private List<Point> pointList = new ArrayList<>();
    public NineView(Context context) {
        super(context);
    }

    public NineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initPoints();
        }
        pointsCanvas(canvas);
    }

    /**
     * 绘制到画布
     * @param canvas
     */
    private void pointsCanvas(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (point.state == Point.STATE_PRESSED) {
                    canvas.drawBitmap(pointPressed, point.x - bitmapR, point.y - bitmapR, paint);
                } else if (point.state == Point.STATE_ERROR) {
                    canvas.drawBitmap(pointError, point.x - bitmapR, point.y - bitmapR, paint);
                } else {
                    canvas.drawBitmap(pointNormal, point.x - bitmapR, point.y - bitmapR, paint);
                }
            }
        }
    }

    private void LineCanvas(Canvas canvas,Point a,Point b){
        if (a.state == Point.STATE_PRESSED) {
            matrix.setScale(22,1);
        } else {

        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        movingX = event.getX();
        movingY = event.getY();
        movingNoPoint = false;
        Point point = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetPoint();
                point = checkSelectPoint();
                if (point != null) {
                    isSelect = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSelect) {
                    point = checkSelectPoint();
                    if (point == null) {
                        movingNoPoint = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isFinish = true;
                isSelect = false;
                break;
        }
        //
        if (!isFinish && isSelect && point != null) {
            if (crassPoint(point)) {
                movingNoPoint = true;
            } else {
                point.state = Point.STATE_PRESSED;
                pointList.add(point);
            }
        }
        if (isFinish) {
            if (pointList.size() == 1) {
                resetPoint();
            } else if (pointList.size() < 5 && pointList.size() > 2) {
                errorPoint();
            }
        }
        //刷新绘制页面
        postInvalidate();
        return true;
    }

    public boolean crassPoint(Point point){
        if (pointList.contains(point)) {
            return true;
        } else {
            return false;
        }
    }
    public void  resetPoint(){
        pointList.clear();
    }
    //绘制错误
    public void errorPoint(){
        for (Point point : pointList) {
            point.state = Point.STATE_ERROR;
        }
    }
    private Point checkSelectPoint(){
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (Point.with(point.x,point.y,bitmapR,movingX,movingY)) {
                    return point;
                }
            }
        }
        return null;
    }
    /**
     * 初始化点
     */
    private void initPoints() {
        //获取布局宽高
        width = getWidth();
        height = getHeight();
        //横屏
        if (width > height) {
            offsetsX = (width-height)/2;
            width = height;
        } else {
            offsetsY = (height-width)/2;
            height = width;
        }
        //图片资源
        pointNormal = BitmapFactory.decodeResource(getResources(), R.drawable.nomal);
        pointPressed = BitmapFactory.decodeResource(getResources(), R.drawable.press);
        pointError = BitmapFactory.decodeResource(getResources(), R.drawable.error);
        LineNormal = BitmapFactory.decodeResource(getResources(), R.drawable.line_nomal);
        LineError = BitmapFactory.decodeResource(getResources(), R.drawable.line_error);

        points[0][0] = new Point(offsetsX + width / 4, offsetsY + width / 4);
        points[0][1] = new Point(offsetsX + width / 2, offsetsY + width / 4);
        points[0][2] = new Point(offsetsX + width - width / 4, offsetsY + width / 4);

        points[1][0] = new Point(offsetsX + width / 4,offsetsY + width / 2);
        points[1][1] = new Point(offsetsX + width / 2,offsetsY + width / 2);
        points[1][2] = new Point(offsetsX + width - width / 4,offsetsY + width / 2);

        points[2][0] = new Point(offsetsX + width / 4,offsetsY + width - width / 4);
        points[2][1] = new Point(offsetsX + width / 2,offsetsY + width - width / 4);
        points[2][2] = new Point(offsetsX + width - width / 4, offsetsY + width - width / 4);
        //图片资源的半径
        bitmapR = pointNormal.getHeight()/2;
    }


    public static class  Point{
        //正常
        public static int STATE_NORMAL = 0;
        //选中
        public static int STATE_PRESSED = 1;
        //错误
        public static int STATE_ERROR = 2;

        public float x ,y;
        public int index = 0,state = 0;

        public Point(float x,float y){
            this.x = x;
            this.y = y;
        }


        /**
         *
         * @param pointX 参考点的X
         * @param pointY 参考点的Y
         * @param r      圆的半径
         * @param movingX 移动点的X
         * @param movingY 移动点的Y
         * @return
         */
        public static boolean with(float pointX,float pointY,float r,float movingX,float movingY){
            return Math.sqrt((pointX - movingX) * (pointX - movingX) + (pointY - movingY) * (pointY - movingY)) < r;
        }

        public static double distance(Point a,Point b){
            return Math.sqrt(Math.abs(a.x - b.x) * Math.abs(a.x - b.x) + Math.abs(a.y - b.y) * Math.abs(a.y - b.y));
        }

    }
}

// DoodleView.java
// Main View for the Doodlz app.
package com.fernando.jogo_adivinha_figura;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;


public class DoodleView extends View {
    // used to determine whether user moved a finger enough to draw again
    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap; // drawing area for displaying or saving
    private Canvas bitmapCanvas; // used to to draw on the bitmap
    private final Paint paintScreen; // used to draw bitmap onto screen
    private final Paint paintLine; // used to draw lines onto bitmap
    private final Paint paintText;


    private int totalHeight;
    private int totalWidth;
    private int centerx1, centerx2, centery1, centery2; //pontos centrais dos quadrantes

    Region squareR  = null;
    Region circleR  = null;
    Region triangleR  = null;
    Region rectangleR  = null;


    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs); // pass context to View's constructor

        paintScreen = new Paint(); // used to display bitmap onto screen


        // set the initial display settings for the painted line
        paintLine = new Paint();
        paintLine.setAntiAlias(true); // smooth edges of drawn line
        paintLine.setColor(Color.BLACK); // default color is black
        paintLine.setStyle(Paint.Style.STROKE); // solid line
        paintLine.setStrokeWidth(30); // set the default line width
        paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends

        paintText = new Paint();
        paintText.setColor(Color.BLACK);
    }

    public LinkedHashSet QuadrantRandom(){

        LinkedHashSet hashSet = new LinkedHashSet();

        while (hashSet.size() < 4) {

            int number = (int)(Math.random()*4);
            hashSet.add(number);
        }
        return hashSet;
    }

    public String ShapeRandom(){

        String shape = "";
        int number = (int)(Math.random()*4);

        switch (number) {

            case 0:
                shape = "square";
                break;
            case 1:
                shape = "circle";
                break;
            case 2:
                shape = "rectangle";
                break;
            case 3:
                shape = "triangle";
                break;
        }
        return shape;
    }

    public void Dimensions() {
        totalHeight = getHeight();
        totalWidth = getWidth();
        centerx1 = totalWidth/4;
        centerx2 = (totalWidth/4)*3;
        centery1 = totalHeight/3;
        centery2 = (totalHeight/3)*2;
    }


    // creates Bitmap and Canvas based on View's size
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE); // erase the Bitmap with white
    }

    // set the painted line's color
    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    // return the painted line's color
    public int getDrawingColor() {
        return paintLine.getColor();
    }

    // set the painted line's width
    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    // return the painted line's width
    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

    // perform custom drawing when the DoodleView is refreshed on screen
    @Override
    protected void onDraw(Canvas canvas) {
        // draw the background screen

        Dimensions();

        String shape = ShapeRandom();

        HashSet quadrantSet;
        HashSet circle;
        HashSet triangule;

        //circle coordinates
        int cx=0, cy=0, radius=0;

        //triangle coordinates
        Point vh = null;
        Point vb1 = null;
        Point vb2 = null;
        Path path = new Path();

        //Quadrants list in random order
        quadrantSet = QuadrantRandom();
        Iterator i = quadrantSet.iterator();

        while (i.hasNext()) {

            canvas.drawRect(Square((int)i.next()), paintLine);
            circle = Circle((int)i.next());
            Iterator c = circle.iterator();

            while(c.hasNext()) {
                cx = (int) c.next();
                cy = (int) c.next();
                radius = (int) c.next();
                canvas.drawCircle(cx, cy, radius, paintLine);
            }

            canvas.drawRect(Rectangle((int)i.next()), paintLine);

            triangule = Triangule((int)i.next());
            Iterator t = triangule.iterator();

            while(t.hasNext()) {

                vh = (Point) t.next();
                vb1 = (Point) t.next();
                vb2 = (Point) t.next();
            }

            path.moveTo(vh.x, vh.y);
            path.lineTo(vb1.x, vb1.y);
            path.lineTo(vb2.x, vb2.y);
            path.lineTo(vh.x, vh.y);
            path.close();
            canvas.drawPath(path, paintLine);
        }
    }

    // handle touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); // event type
        int actionIndex = event.getActionIndex(); // pointer (i.e., finger)

        // determine whether touch started, ended or is moving
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        }
        else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        }
        else {
            touchMoved(event);
        }

        invalidate(); // redraw
        return true;
    }

    // called when the user touches the screen
    private void touchStarted(float x, float y, int lineID) {

        Point point = new Point();

        point.set((int)x, (int)y);

        if (squareR.contains((int)x, (int)y))
            Toast.makeText(getContext(), "Tocou no quadrado", Toast.LENGTH_SHORT).show();

        else if (rectangleR.contains((int)x, (int)y))
            Toast.makeText(getContext(), "Tocou no retângulo", Toast.LENGTH_SHORT).show();
        else if (triangleR.contains((int)x, (int)y))
            Toast.makeText(getContext(), "Tocou no triângulo", Toast.LENGTH_SHORT).show();
        else if (circleR.contains((int)x, (int)y))
            Toast.makeText(getContext(), "Tocou no círculo", Toast.LENGTH_SHORT).show();
    }

    // called when the user drags along the screen
    private void touchMoved(MotionEvent event) {

    }

    // called when the user finishes a touch
    private void touchEnded(int lineID) {

    }


    public Rect Square(int quadrant) {

        Rect square = new Rect();

        int left= 0, top= 0, right= 0, bottom = 0, side;

        side = calculateSquareSide();

        switch (quadrant) {

            case 0:

                left = centerx1 - side/2;
                top = centery1 - side/2;
                right = left + side;
                bottom = top + side;
                break;

            case 1:

                left = centerx2 - side/2;
                top = centery1 - side/2;
                right = left + side;
                bottom = top + side;

                break;

            case 2:

                left = centerx1 - side/2;
                top = centery2 - side/2;
                right = left + side;
                bottom = top + side;

                break;

            case 3:
                left = centerx2 - side/2;
                top = centery2 - side/2;
                right = left + side;
                bottom = top + side;
                break;
        }

        square.set(left, top, right, bottom);
        squareR = new Region(left, top, right, bottom);

        return square;

    }
    private int calculateSquareSide() {

        return (totalWidth/2 - totalWidth/8);
    }

    public Rect Rectangle(int quadrant) {

        Rect rectangle = new Rect();

        int left= 0, top= 0, right= 0, bottom = 0, side ;

        side = calculateRectangleSide();

        switch (quadrant) {

            case 0:

                left = centerx1 - side/2;
                top = centery1 - side/4;
                right = left + side;
                bottom = top + side/2;
                break;

            case 1:

                left = centerx2 - side/2;
                top = centery1 - side/4;
                right = left + side;
                bottom = top + side/2;

                break;

            case 2:

                left = centerx1 - side/2;
                top = centery2 - side/4;
                right = left + side;
                bottom = top + side/2;

                break;

            case 3:
                left = centerx2 - side/2;
                top = centery2 - side/4;
                right = left + side;
                bottom = top + side/2;
                break;
        }

        rectangle.set(left, top, right, bottom);
        rectangleR = new Region(left, top, right, bottom);

        return rectangle;

    }
    private int calculateRectangleSide() {

        return (totalWidth/2 - totalWidth/8);
    }


    public LinkedHashSet Triangule(int quadrant) {

        LinkedHashSet triangule = new LinkedHashSet();

        Point vb1 = null;
        Point vb2 = null;
        Point vh = null;
        Point extra1 = null;

        int height;

        height = calculateSquareSide();

        switch (quadrant) {

            case 0:

                vh = new Point(centerx1, centery1-height/2);
                vb1 = new Point(centerx1-height/4, centery1+height/2);
                vb2 = new Point(centerx1+height/4, centery1+height/2);
                extra1 =new Point(centerx1-height/4, centery1-height/2);
                break;

            case 1:

                vh = new Point(centerx2, centery1-height/2);
                vb1 = new Point(centerx2-height/4, centery1+height/2);
                vb2 = new Point(centerx2+height/4, centery1+height/2);
                extra1 = new Point(centerx2-height/4, centery1-height/2);
                break;

            case 2:

                vh = new Point(centerx1, centery2-height/2);
                vb1 = new Point(centerx1-height/4, centery2+height/2);
                vb2 = new Point(centerx1+height/4, centery2+height/2);
                extra1 =new Point(centerx1-height/4, centery2-height/2);
                break;

            case 3:
                vh = new Point(centerx2, centery2-height/2);
                vb1 = new Point(centerx2-height/4, centery2+height/2);
                vb2 = new Point(centerx2+height/4, centery2+height/2);
                extra1 =new Point(centerx2-height/4, centery2-height/2);
                break;
        }

        triangule.add(vh);
        triangule.add(vb1);
        triangule.add(vb2);

        triangleR = new Region(extra1.x, extra1.y, extra1.x+height/2, extra1.y+height);

        return triangule;
    }

    public LinkedHashSet Circle(int quadrant) {

        LinkedHashSet lh = new LinkedHashSet();


        int cx= 0, cy= 0, radius;

        radius = calculateCircleRadius();

        switch (quadrant) {

            case 0:

                cx = centerx1;
                cy = centery1;
                break;

            case 1:

                cx = centerx2;
                cy = centery1;
                break;

            case 2:

                cx = centerx1;
                cy = centery2;
                break;

            case 3:
                cx = centerx2;
                cy = centery2;
                break;
        }

        lh.add(cx);
        lh.add(cy);
        lh.add(radius);

        circleR = new Region(cx-radius, cy-radius, cx+radius*2, cy+radius*2);

        return lh;


    }

    public int calculateCircleRadius() {
        return (totalWidth/2 - totalWidth/8)/2;
    }

}
//    public class Shape extends Canvas {
//
//        private int quadrant;
//
//
//        public Shape(int quadrant) {
//            this.quadrant = quadrant;
//
//        }
//    }
//
//    public class ShapeSquare extends Shape {
//
//        private int side;
//
//                public ShapeSquare(int quadrant) {
//            super(quadrant);
//
//            side = calculateSide();
//
//        }
//
//
//
//        private int calculateSide() {
//
////            return (totalWidth/2 - totalWidth/12);
////        }
//
//
//
//
//
//
//
//
//
//    }





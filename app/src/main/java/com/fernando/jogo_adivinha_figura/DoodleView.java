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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

// custom View for drawing
public class DoodleView extends View {
    // used to determine whether user moved a finger enough to draw again
    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap; // drawing area for displaying or saving
    private Canvas bitmapCanvas; // used to to draw on the bitmap
    private final Paint paintScreen; // used to draw bitmap onto screen
    private final Paint paintLine; // used to draw lines onto bitmap
    private final Paint paintText;

    // Maps of current Paths being drawn and Points in those Paths
    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap =  new HashMap<>();

    private int totalHeight;
    private int totalWidth;
    private int marginX, marginY;
    private int quadrant;
    private int centerx1, centerx2, centery1, centery2;

    // DoodleView constructor initializes the DoodleView
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

        //variáveis que serão utilizadas para calcular o tamanho das figuras,
        // margens e posicionamento
        // em relação ao espaço disponível
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

    // clear the painting
    public void clear() {
        pathMap.clear(); // remove all paths
        previousPointMap.clear(); // remove all previous points
        bitmap.eraseColor(Color.WHITE); // clear the bitmap
        invalidate(); // refresh the screen
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

        HashSet hs = new HashSet();

        HashSet circle = new HashSet();

        HashSet triangule = new HashSet();

        int cx=0, cy=0, radius=0;

        Point vh = null;
        Point vb1 = null;
        Point vb2 = null;

        Path path = new Path();

        hs = QuadrantRandom();

        Iterator i = hs.iterator();


        while (i.hasNext()) {
            //Toast.makeText(getContext(), "O novo número sorteado é: " + i.next().toString(), Toast.LENGTH_LONG).show();
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
        Path path; // used to store the path for the given touch id
        Point point; // used to store the last point in path

        // if there is already a path for lineID
        if (pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID); // get the Path
            path.reset(); // resets the Path because a new touch has started
            point = previousPointMap.get(lineID); // get Path's last point
        }
        else {
            path = new Path();
            pathMap.put(lineID, path); // add the Path to Map
            point = new Point(); // create a new Point
            previousPointMap.put(lineID, point); // add the Point to the Map
        }

        // move to the coordinates of the touch
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    // called when the user drags along the screen
    private void touchMoved(MotionEvent event) {
        // for each of the pointers in the given MotionEvent
        for (int i = 0; i < event.getPointerCount(); i++) {
            // get the pointer ID and pointer index
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            // if there is a path associated with the pointer
            if (pathMap.containsKey(pointerID)) {
                // get the new coordinates for the pointer
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                // get the path and previous point associated with
                // this pointer
                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                // calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // if the distance is significant enough to matter
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // move the path to the new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2,
                            (newY + point.y) / 2);

                    // store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    // called when the user finishes a touch
    private void touchEnded(int lineID) {
        Path path = pathMap.get(lineID); // get the corresponding Path
        bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
        path.reset(); // reset the Path
    }


    public Rect Square(int quadrant) {

        Rect square = new Rect();

        int left= 0, top= 0, right= 0, bottom = 0, side ;

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


        int height;

        height = calculateSquareSide();

        switch (quadrant) {

            case 0:

                vh = new Point(centerx1, centery1-height/2);
                vb1 = new Point(centerx1-height/4, centery1+height/2);
                vb2 = new Point(centerx1+height/4, centery1+height/2);


                break;

            case 1:

                vh = new Point(centerx2, centery1-height/2);
                vb1 = new Point(centerx2-height/4, centery1+height/2);
                vb2 = new Point(centerx2+height/4, centery1+height/2);

                break;

            case 2:

                vh = new Point(centerx1, centery2-height/2);
                vb1 = new Point(centerx1-height/4, centery2+height/2);
                vb2 = new Point(centerx1+height/4, centery2+height/2);
                break;

            case 3:
                vh = new Point(centerx2, centery2-height/2);
                vb1 = new Point(centerx2-height/4, centery2+height/2);
                vb2 = new Point(centerx2+height/4, centery2+height/2);
                break;
        }

        triangule.add(vh);
        triangule.add(vb1);
        triangule.add(vb2);

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





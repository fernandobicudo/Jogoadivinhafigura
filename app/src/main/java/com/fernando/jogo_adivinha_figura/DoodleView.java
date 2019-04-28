// DoodleView.java
// Main View for the Doodlz app.
package com.fernando.jogo_adivinha_figura;
import android.content.Context;
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

    private static final float TOUCH_TOLERANCE = 10;

    private String shape;
    private final Paint paintLine;
    private int totalHeight;
    private int totalWidth;
    private int centerx1, centerx2, centery1, centery2;

    Region squareR  = null;
    Region circleR  = null;
    Region triangleR  = null;
    Region rectangleR  = null;

    int points = 0;
    int count=0;

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(30);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
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

    @Override
    protected void onDraw(Canvas canvas) {

        Dimensions();

        if (count == 0)         {
            shape = ShapeRandom();
            Toast.makeText(getContext(), "Shape: " + shape + ".   " + points + " points.  " + count + " times.", Toast.LENGTH_SHORT).show();
            count++;
            Toast.makeText(getContext(),"Stating " + count + " time.", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Click on the " + shape, Toast.LENGTH_SHORT).show();
        }
        else if (count%4 !=0)
        {
            count++;
            Toast.makeText(getContext(), "Try " + count + " time.", Toast.LENGTH_SHORT).show();
        }

        HashSet quadrantSet;
        HashSet circle;
        HashSet triangle;

        int cx=0, cy=0, radius=0;

        Point vh = null;
        Point vb1 = null;
        Point vb2 = null;
        Path path = new Path();

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

            triangle = Triangule((int)i.next());
            Iterator t = triangle.iterator();

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();


        if (action == MotionEvent.ACTION_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex));
        }
        return true;
    }


    private void touchStarted(float x, float y) {

        Point point = new Point();
        point.set((int)x, (int)y);

        if (squareR.contains((int)x, (int)y))
        {
            Toast.makeText(getContext(), "You touched the square", Toast.LENGTH_SHORT).show();
            if (shape.equals("square"))            {
                points++;
                Toast.makeText(getContext(), "You won " + points + " points", Toast.LENGTH_SHORT).show();
            }
        }

        else if (rectangleR.contains((int)x, (int)y))   {
            Toast.makeText(getContext(), "You touched the rectangle", Toast.LENGTH_SHORT).show();
            if (shape.equals("rectangle"))            {
                points++;
                Toast.makeText(getContext(), "You won " + points + " points", Toast.LENGTH_SHORT).show();
            }
        }

        else if (triangleR.contains((int)x, (int)y))    {
            Toast.makeText(getContext(), "You touched the triangle", Toast.LENGTH_SHORT).show();
            if (shape.equals("triangle"))            {
                points++;
                Toast.makeText(getContext(), "You won " + points + " points", Toast.LENGTH_SHORT).show();
            }
        }


        else if (circleR.contains((int)x, (int)y))      {
            Toast.makeText(getContext(), "You touched the circle", Toast.LENGTH_SHORT).show();
            if (shape.equals("circle"))            {
                points++;
                Toast.makeText(getContext(), "You won " + points + " points", Toast.LENGTH_SHORT).show();
            }
        }

        if (count%4==0)        {
            Toast.makeText(getContext(), "END OF GAME: YOU WON " + points + " POINTS. RESTART PLAYING...", Toast.LENGTH_LONG).show();
            count = 0;
            points = 0;
        }
        invalidate();
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






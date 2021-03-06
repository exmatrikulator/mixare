package org.mixare.gui.opengl;

import android.graphics.Color;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by MelanieW on 27.02.2016.
 */
public class RouteSegment {

    String routeColorString = "#ADFF2F";
    int walkedColor = (0x00ffffff &Color.parseColor(routeColorString)| alpha);

    private FloatBuffer rectVerticesBuffer;
    private FloatBuffer rectVerticesBuffer2;
    private ShortBuffer rectTrianglesBuffer;

    private float startX;
    private float startY;
    private MyVector startVector = new MyVector();

    public void setStartVector(MyVector startVector) {
        this.startVector = startVector;
    }

    public void setEndVector(MyVector endVector) {
        this.endVector = endVector;
    }

    private MyVector endVector = new MyVector();
    private float endX;
    private float endY;

    public boolean isNearestRouteSegment() {
        return isNearestRouteSegment;
    }

    public void setIsNearestRouteSegment(boolean isNearestRouteSegment) {
        this.isNearestRouteSegment = isNearestRouteSegment;
    }

    boolean isNearestRouteSegment = false;


    float directionX;
    float directionY;

    float resultTemp1;

    private int color = Color.argb(128, 255, 0, 255); //128, 51, 153, 255= light blue
    private static final int alpha=Color.argb(128, 0, 0, 0);

    MyVector leftStartVector = new MyVector();
    MyVector rightStartVector = new MyVector();
    MyVector leftEndVector = new MyVector();
    MyVector rightEndVector = new MyVector();
    MyVector leftMidVector = new MyVector();
    MyVector rightMidVector = new MyVector();

    public MyVector getIntersectionPoint() {
        return intersectionPoint;
    }

    public void setIntersectionPoint(MyVector intersectionPoint) {
        this.intersectionPoint = intersectionPoint;
    }

    private MyVector intersectionPoint = null;



    MyVectorOperations myVectorOperations = new MyVectorOperations();

    short[] rectTriangles = new short[]{
            0, 1, 2,
            2, 3, 0
    };

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = (0x00ffffff & color) | alpha; //remove alpha from argb color, then combine rgb with alpha
    }

    public RouteSegment(float startX, float startY, float endX, float endY){

        startVector.setXCoordinate(startX);
        startVector.setYCoordinate(startY);
        endVector.setXCoordinate(endX);
        endVector.setYCoordinate(endY);

        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;


        update();

    }

    public void update() {

        Log.d("RS", "segment (" + startX + "," + startY + ") => (" + endX + "," + endY + ")");

        calculateRecVertices();

        if( leftMidVector != null ) {
            //if( false ) {

            Log.d( "RS", "segment mid ("+intersectionPoint.getXCoordinate()+","+intersectionPoint.getYCoordinate()+")" );

            float rectVertices[]= {
                    leftStartVector.getXCoordinate(),leftStartVector.getYCoordinate(),0,
                    rightStartVector.getXCoordinate(), rightStartVector.getYCoordinate(),0,
                    rightMidVector.getXCoordinate(), rightMidVector.getYCoordinate(),0,
                    leftMidVector.getXCoordinate(), leftMidVector.getYCoordinate(),0,
            };

            float rectVertices2[]= {
                    leftMidVector.getXCoordinate(),leftMidVector.getYCoordinate(),0,
                    rightMidVector.getXCoordinate(), rightMidVector.getYCoordinate(),0,
                    rightEndVector.getXCoordinate(), rightEndVector.getYCoordinate(),0,
                    leftEndVector.getXCoordinate(), leftEndVector.getYCoordinate(),0,
            };

            rectVerticesBuffer
                    = getDirectFloatBuffer(rectVertices);

            rectVerticesBuffer2
                    = getDirectFloatBuffer(rectVertices2);

            rectTrianglesBuffer
                    = getDirectShortBuffer(rectTriangles);

        } else {

            float rectVertices[]= {
                    leftStartVector.getXCoordinate(),leftStartVector.getYCoordinate(),0,
                    rightStartVector.getXCoordinate(), rightStartVector.getYCoordinate(),0,
                    rightEndVector.getXCoordinate(), rightEndVector.getYCoordinate(),0,
                    leftEndVector.getXCoordinate(), leftEndVector.getYCoordinate(),0,
            };

            rectVerticesBuffer
                    = getDirectFloatBuffer(rectVertices);

            rectTrianglesBuffer
                    = getDirectShortBuffer(rectTriangles);

            rectVerticesBuffer2 = null;

        }



    }



    public void calculateRecVertices(){

        MyVector directionVector= myVectorOperations.getDirectionVector(startVector, endVector);
        MyVector orthogonalDirectionVector = myVectorOperations.getOrthogonalDirectionVector(directionVector);



        resultTemp1 = myVectorOperations.getDirectionVectorLength(directionVector);

        //directionX = startX-endX;
        //directionY = startY-endY;

        //resultTemp1 = (float)Math.sqrt(Math.pow(-directionY,2) + Math.pow(directionX,2));



        leftStartVector.setXCoordinate(orthogonalDirectionVector.getXCoordinate() / resultTemp1);
        leftStartVector.setYCoordinate((orthogonalDirectionVector.getYCoordinate() / resultTemp1));


        rightStartVector.setXCoordinate(-orthogonalDirectionVector.getXCoordinate() / resultTemp1);
        rightStartVector.setYCoordinate(-orthogonalDirectionVector.getYCoordinate() / resultTemp1);

        leftEndVector.setXCoordinate(leftStartVector.getXCoordinate()+directionVector.getXCoordinate());
        leftEndVector.setYCoordinate(leftStartVector.getYCoordinate() + directionVector.getYCoordinate());

        rightEndVector.setXCoordinate(rightStartVector.getXCoordinate()+directionVector.getXCoordinate());
        rightEndVector.setYCoordinate(rightStartVector.getYCoordinate()+directionVector.getYCoordinate());

        if( getIntersectionPoint() != null ) {
            //if( false ) {

            //two rectangles
            MyVector directionVectorMid = myVectorOperations.getDirectionVector( startVector, getIntersectionPoint() );

            leftMidVector = new MyVector();
            rightMidVector = new MyVector();

            leftMidVector.setXCoordinate(leftStartVector.getXCoordinate() + directionVectorMid.getXCoordinate());
            leftMidVector.setYCoordinate(leftStartVector.getYCoordinate() + directionVectorMid.getYCoordinate());

            rightMidVector.setXCoordinate(rightStartVector.getXCoordinate() + directionVectorMid.getXCoordinate());
            rightMidVector.setYCoordinate(rightStartVector.getYCoordinate() + directionVectorMid.getYCoordinate());

        } else {

            leftMidVector = null;
            rightMidVector = null;

        }


    }

    public void draw (GL10 gl) {


        // Counter-clockwise winding.
        gl.glFrontFace(GL10.GL_CCW);

        gl.glEnable(GL10.GL_CULL_FACE);
        // What faces to remove with the face culling.
        gl.glCullFace(GL10.GL_BACK);

        // Enabled the vertices buffer for writing and to be used during
        // rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // Specifies the location and data format of an array of vertex
        // coordinates to use when rendering.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0,
                rectVerticesBuffer);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA) ;

        gl.glColor4f(Color.red(color) / 255.0f, Color.green(color) / 255.0f, Color.blue(color) / 255.0f, Color.alpha(color) / 255.0f);

        gl.glDrawElements(GL10.GL_TRIANGLES, rectTriangles.length,
                GL10.GL_UNSIGNED_SHORT, rectTrianglesBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);

        if( rectVerticesBuffer2 != null ) {

            //Log.d( "RS", "draw in two parts" );

            gl.glEnable(GL10.GL_CULL_FACE);
            // What faces to remove with the face culling.
            gl.glCullFace(GL10.GL_BACK);

            // Enabled the vertices buffer for writing and to be used during
            // rendering.
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            // Specifies the location and data format of an array of vertex
            // coordinates to use when rendering.
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0,
                    rectVerticesBuffer2 );

          gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA) ;

            gl.glColor4f(Color.red(walkedColor) / 255.0f, Color.green(walkedColor) / 255.0f, Color.blue(walkedColor) / 255.0f, Color.alpha(walkedColor) / 255.0f);

            gl.glDrawElements(GL10.GL_TRIANGLES, rectTriangles.length,
                    GL10.GL_UNSIGNED_SHORT, rectTrianglesBuffer);


            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisable(GL10.GL_CULL_FACE);

        }

    }

    private static FloatBuffer getDirectFloatBuffer(float[] array) {
        int len = array.length * (Float.SIZE/8);
        ByteBuffer storage = ByteBuffer.allocateDirect(len);
        storage.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = storage.asFloatBuffer();
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    private static ShortBuffer getDirectShortBuffer(short[] array) {
        int len = array.length * (Short.SIZE/8);
        ByteBuffer storage = ByteBuffer.allocateDirect(len);
        storage.order(ByteOrder.nativeOrder());
        ShortBuffer buffer = storage.asShortBuffer();
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    public MyVector getStartVector(){
        return startVector;
    }

    public MyVector getEndVector(){
        return  endVector;
    }
}

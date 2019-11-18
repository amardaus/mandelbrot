package sample;

import complex.Complex;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Controller {
    public TextField setR;
    public TextField setP;
    public TextField setWidth;
    public TextField setHeight;
    public TextField xMin;
    public TextField xMax;
    public TextField yMin;
    public TextField yMax;


    int R = 4;
    private GraphicsContext gc;
    public Canvas canvas;
    int iterMax = 100;
    int scale = 256;
    private double mouseAx, mouseAy, mouseBx, mouseBy;
    private double aX, bX, aY, bY;

    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        clear(gc);

        aX = -1;
        bX = 1;
        aY = 1;
        bY = -1;
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void mouseMoves(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        gc.setGlobalBlendMode(BlendMode.DIFFERENCE);
        gc.setStroke(Color.WHITE);
        rect(gc);
        mouseBx = x;
        mouseBy = y;
        rect(gc);
    }

    private void rect(GraphicsContext gc) {
        double x = mouseAx;
        double y = mouseAy;
        double w = mouseBx - mouseAx;
        double h = mouseBy - mouseAy;

        if(w < 0){
            x = mouseBx;
            w = -w;
        }

        if(h < 0){
            y = mouseBy;
            h = -h;
        }

        /*if(w > h){
            mouseBy += (w-h);
        }
        else{
            mouseBx += (h-w);
        }*/

        gc.strokeRect(x + 0.5, y + 0.5, w, h);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        mouseAx = mouseEvent.getX();
        mouseAy = mouseEvent.getY();
        mouseBx = mouseAx;
        mouseBy = mouseAy;
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
        rect(gc);

        double w = mouseBx - mouseAx;
        double h = mouseBy - mouseAy;
        if(w > h){
            mouseBy += (w-h);
        }
        else{
            mouseBx += (h-w);
        }

        zoom();

        System.out.format("mouseAx:%f mouseAy:%f mouseBx:%f mouseBy:%f\n", mouseAx, mouseAy, mouseBx, mouseBy);
    }

    int mandelbrot(Complex p){
        Complex z = (Complex)p.clone();

        for(int i = 0; i < iterMax; i++){
            z = (z.mul(z)).add(p);
            if(z.sqrAbs() > R) return i;
        }

        return iterMax;
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public void draw(ActionEvent actionEvent) {
        final int height = 512;
        final int width = 512;

        double ix = (bX - aX)/width;
        double iy = (bY - aY)/height;

        WritableImage wr = new WritableImage(width, height);
        PixelWriter pw = wr.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double re = aX + x * ix;
                double im = aY + y * iy;

                Complex c = new Complex(re, im);

                int iterCount = mandelbrot(c);
                if(iterCount == iterMax){
                    pw.setPixels(x, y, 1, 1, PixelFormat.getIntArgbInstance(), new int[] { 0xFF000000 }, 0, 1);
                }
                else{
                    pw.setPixels(x, y, 1, 1, PixelFormat.getIntArgbInstance(), new int[] {getIntFromColor(0, 255*iterCount/100, 0)}, 0, 1);
                }
            }
        }
        pw.setArgb(0, 0, 0xFFFF0000);
        gc.drawImage(wr, 0,0, width, height);
    }

    public void zoom() {
        /*double tmpMAx = mouseAx, tmpMAy = mouseAy, tmpMBx = mouseBx, tmpMBy = mouseBy;
        mouseAx = Math.min(mouseAx, mouseBx);
        mouseBx = Math.max(mouseAx, mouseBx);
        mouseAy = Math.min(mouseAy, mouseBy);
        mouseBy = Math.max(mouseAy, mouseBy);*/

        final int height = 512;
        final int width = 512;
        double w = Math.max(Math.abs(mouseAx-mouseBx), Math.abs(mouseAy-mouseBy));
        double h = w;

        System.out.println("w:" + w + "h:" + h);

        double newAx, newAy, newBx, newBy;
        newAx = ((bX - aX)/512)*mouseAx + aX;
        newAy = ((bY - aY)/512)*mouseAy + aY;
        newBx = ((bX - aX)/512)*mouseBx + aX;
        newBy = ((bY - aY)/512)*mouseBy + aY;

        double ix = (newBx - newAx)/width;
        double iy = (newBy - newAy)/height;

        WritableImage wr = new WritableImage(width, height);
        PixelWriter pw = wr.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                double re = newAx + x * ix;
                double im = newAy + y * iy;

                Complex c = new Complex(re, im);
                int iterCount = mandelbrot(c);

                if(iterCount == iterMax){
                    pw.setPixels(x, y, 1, 1, PixelFormat.getIntArgbInstance(), new int[] { 0xFF000000 }, 0, 1);
                }
                else{
                    pw.setPixels(x, y, 1, 1, PixelFormat.getIntArgbInstance(), new int[] {getIntFromColor(0, 255*iterCount/100, 0)}, 0, 1);
                }
            }
        }
        pw.setArgb(0, 0, 0xFFFF0000);
        gc.drawImage(wr, 0,0, width, height);

        aX = newAx;
        aY = newAy;
        bX = newBx;
        bY = newBy;
    }

    public void setR(ActionEvent actionEvent) {
        try {
            R = Integer.parseInt(setR.getText());
        } catch (Exception e) {
            System.out.println("Not a number");
        }
    }

    public void setP(ActionEvent actionEvent) {
        try{
            aX = Integer.parseInt(xMin.getText());
            aY = Integer.parseInt(yMax.getText());
            bX = Integer.parseInt(xMax.getText());
            bY = Integer.parseInt(yMin.getText());
        }
        catch (Exception e){
            System.out.println("Not a number");
        }
    }
}

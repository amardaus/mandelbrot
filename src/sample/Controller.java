package sample;

import complex.Complex;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Controller {
    private GraphicsContext gc;
    public Canvas canvas;
    int iterMax = 100;

    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        clear(gc);
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void mouseMoves(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    int mandelbrot(Complex p){
        Complex z = (Complex)p.clone();

        for(int i = 0; i < iterMax; i++){
            z = (z.mul(z)).add(p);
            if(z.sqrAbs() > 4) return i;
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
        double xc = 0;
        double yc = 0;


        final int height = 512;
        final int width = 512;
        int n = 512;

        WritableImage wr = new WritableImage(width, height);
        PixelWriter pw = wr.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double x0 = (x - width / 2.0)/170;
                double y0 = (y - height / 2.0)/170;
                //System.out.println("x0=" + x0 + "\t " + "y0=" + y0);

                Complex c = new Complex(x0, y0);
                int iterCount = mandelbrot(c);
                if(iterCount != 0) {
                    //System.out.println("x0=" + x0 + "y0=" + y0 +"itercount=" + iterCount);
                }
                //pw.setArgb(x, y, (iterCount == iterMax ? 0xFFFF00FF : 0xFFFFFFFF));
                //pw.setPixels(x, y, 1, 1, PixelFormat.getIntArgbInstance(), new int[] { (iterCount == iterMax ? 0xFFFF00FF : 0xFFFFFFFF) }, 0, 1);
                if(iterCount == iterMax){
                    pw.setArgb(x, y, 0xFF000000
                    );
                }
                else{
                    pw.setPixels(x, y, 1, 1, PixelFormat.getIntArgbInstance(), new int[] {getIntFromColor(0, 255*iterCount/100, 0)}, 0, 1);
                }
            }
        }
        //gc.drawImage(wr, 80,0, width, height);
        gc.drawImage(wr, 0,0, width, height);
    }
}

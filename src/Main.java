import java.util.*;

public class Main implements Runnable{

    Gui gui = new Gui();

    public static void main(String[] args) {
        Thread t = new Thread(new Main());
        t.start();
    }

    @Override
    public void run() {
        while (true) {
            gui.repaint();
        }
    }
}

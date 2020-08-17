import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class Gui extends JFrame{
    public final int space = 5;
    //public final int size = 30;

    // clicking/ moving coordinates
    public int xCord;
    public int yCord;

    // dragging coordinates
    public int movingX;
    public int movingY;

    // arrays to make walls
    boolean[][] boolArr = new boolean[42][25];
    boolean[][] dragArr = new boolean[42][25];

    // choosing Algorithm
    String algorithm = null;
    boolean startAlgo = false;

    // bfs
    boolean[][] bfs = new boolean[42][25];
    boolean[][] finished = new boolean[42][25];
    boolean once = false;

    // resetting boolean
    boolean noReset = false;

    // path finding algorithm effect colors
    Queue<int[]> colorQueue = new LinkedList<>();
    Queue<int[]> colorQueue2 = new LinkedList<>();
    boolean[][] colorChanging = new boolean[42][25];
    boolean[][] colorChanging2 = new boolean[42][25];
    boolean colorChanging2Finished = false;

    // displaying shortest path
    public Node last = null;
    boolean[][] pathing = new boolean[42][25];
    boolean pathOnce = false;

    public Gui() {
        this.setTitle("Path Finding Visualizer");
        this.setSize(1280, 829);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        Board b = new Board();
        this.setContentPane(b);

        Move m = new Move();
        this.addMouseMotionListener(m);

        Clicker c = new Clicker();
        this.addMouseListener(c);
    }

    public class Board extends JPanel {
        public void paintComponent(Graphics g) {

            g.setColor(Color.black);
            g.fillRect(0,0, 1280, 800);


            for (int x = 0 ; x < 42; x ++) {
                for (int y = 0; y < 25; y++) {

                    g.setColor(Color.lightGray);

                    if (xCord >= x * 30 + space + space + 2 && xCord < x * 30 + 30 + space + space + 1 &&
                            yCord >= space + y * 30 + 30 + 30 && yCord < space + y * 30 + 30 + 30 + 30 - space) {
                        g.setColor(Color.gray);
                    }

                    if (movingX >= x * 30 + space + space + 2 && movingX < x * 30 + 30 + space + space + 1 &&
                            movingY >= space + y * 30 + 30 + 30 && movingY < space + y * 30 + 30 + 30 + 30 - space) {

                        if (x != 34 && y != 11 ) {
                            dragArr[x][y] = true;
                        }
                    }

                    if (dragArr[x][y]) {
                        g.setColor(Color.black);
                    }

                    if (boolArr[x][y]) {
                        g.setColor(Color.green);
                    }

                    if ( (x == 6 || x == 34) && (y == 11 )) {
                        g.setColor(Color.pink);
                    }

                    // path finding colors
                    if (finished[x][y]) {
                        g.setColor(Color.yellow);
                    }

                    if (colorChanging[x][y]) {
                        g.setColor(Color.green);
                    }

                    if (colorChanging2[x][y]) {
                        g.setColor(Color.magenta);
                    }

                    if (pathing[x][y]) {
                        g.setColor(Color.cyan);
                    }

                    g.fillRect(space + x * 30, space + y * 30 + 30, 30 - space, 30 - space);
                }
            }
            // Algorithm text
            g.setColor(Color.white);
            g.fillRect(5,5, 160 , 25);
            g.setColor(Color.red);
            g.setFont(new Font("TimesRoman", Font.BOLD, 20));
            g.drawString("Algorithms ►", 25, 25);
            // bfs text
            g.setColor(Color.white);
            if (xCord >= 178 && xCord < 297 && yCord >= 36 && yCord < 61 ) {
                g.setColor(Color.DARK_GRAY);
            }
            g.fillRect(170, 5, 120, 25);
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            g.drawString("BFS", 200, 25);
            // dfs text
            g.setColor(Color.white);
            if (xCord >= 303 && xCord < 423 && yCord >= 36 && yCord < 61) {
                g.setColor(Color.DARK_GRAY);
            }
            g.fillRect(295, 5, 120, 25);
            g.setColor(Color.red);
            g.drawString("DFS", 330, 25);

            // visualize text
            g.setColor(Color.orange);
            g.fillRect(870, 5, 200, 25);
            g.setColor(Color.red);
            g.setFont(new Font("TimesRoman", Font.BOLD, 20));
            if (algorithm == null) {
                g.drawString("Visualize", 930, 25 );
            } else {
                g.drawString("Visualize " + algorithm + "!", 900, 25);
            }

            g.setColor(Color.white);
            if (xCord >= 1098 && xCord < 1258 && yCord >= 36 && yCord < 61) {
                g.setColor(Color.lightGray);
            }
            g.fillRect(1090, 5, 160, 25);
            g.setColor(Color.red);
            g.setFont(new Font("TimesRoman", Font.BOLD, 20));
            g.drawString("RESET ⟳", 1130, 25);
            g.setColor(Color.black);
            g.drawLine(180, 390, 210, 378);
            g.drawLine(181, 391, 211, 379);
            g.drawLine(179, 389, 209, 377);
            g.drawLine(180, 366, 210, 378);
            g.drawLine(181, 367, 211, 379);
            g.drawLine(179, 365, 209, 377);
            g.setColor(Color.black);
            g.fillOval(1027,367,20,20);
            g.setColor(Color.pink);
            g.fillOval(1032, 371, 11, 11);

            if (!once && startAlgo) {

                if (algorithm.equals("BFS")) {
                    Thread t = new Thread(new runBFS());
                    t.start();
                } else if (algorithm.equals("DFS")) {
                    Thread t = new Thread(new runDFS());
                    t.start();
                }
                once = true;
            }
            Thread t2 = new Thread(new colorChanging());
            t2.start();
            Thread t3 = new Thread(new colorChanging2());
            t3.start();
            if (last != null && once && !pathOnce) {
                Thread t4 = new Thread(new makePath());
                t4.start();
                pathOnce = true;
            }
        }

        public class makePath implements Runnable {

            @Override
            public void run() {
                Stack<Node> stk = new Stack<>();
                Node current = last;
                while (current != null && !current.first) {
                    stk.push(current);
                    current = current.prev;
                }
                stk.push(current);
                while (!stk.isEmpty()) {
                    Node temp = stk.pop();
                    pathing[temp.x][temp.y] = true;
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public class colorChanging implements Runnable {

            @Override
            public void run() {
                while (!colorQueue.isEmpty()) {
                    int[] temp = colorQueue.remove();
                    colorQueue2.add(temp);
                    int x = temp[0];
                    int y = temp[1];

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    colorChanging[x][y] = true;
                }
            }
        }

        public class colorChanging2 implements Runnable {

            @Override
            public void run() {
                while (!colorQueue2.isEmpty()) {
                    int[] temp = colorQueue2.remove();
                    int x = temp[0];
                    int y = temp[1];

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    colorChanging2[x][y] = true;
                }
            }
        }

        public class runDFS implements Runnable {
            @Override
            public void run() {
                Node[][] nodeArr = new Node[42][25];
                for (int i = 0; i < 42; i++) {
                    for (int j = 0; j < 25; j++) {
                        nodeArr[i][j] = new Node(i,j, false);
                        if (i == 6 && j == 11) {
                            nodeArr[i][j].first = true;
                        }
                    }
                }


                noReset = true;
                bfs[34][11] = true;
                boolean[][] visited = new boolean[42][25];
                Stack<int[]> queue = new Stack<>();
                queue.push(new int[]{6,11});

                while (!queue.isEmpty()) {
                    int[] current = queue.pop();
                    int x = current[0];
                    int y = current[1];

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (bfs[x][y]) {
                        last = nodeArr[x][y];
                        break;
                    }

                    if (x - 1 >= 0 && !visited[x-1][y] && !dragArr[x-1][y]) {
                        queue.push(new int[]{x-1, y});
                        nodeArr[x-1][y].prev = nodeArr[x][y];
                        visited[x-1][y] = true;
                    }
                    if (x + 1 < 42 && !visited[x+1][y] && !dragArr[x+1][y]) {
                        queue.push(new int[]{x+1, y});
                        nodeArr[x+1][y].prev = nodeArr[x][y];
                        visited[x+1][y] = true;
                    }
                    if (y - 1 >= 0 && !visited[x][y-1] && !dragArr[x][y-1]) {
                        queue.push(new int[]{x, y-1});
                        nodeArr[x][y-1].prev = nodeArr[x][y];
                        visited[x][y-1] = true;
                    }
                    if (y + 1 < 25 && !visited[x][y+1] && !dragArr[x][y+1]) {
                        queue.push(new int[]{x, y+1});
                        nodeArr[x][y+1].prev = nodeArr[x][y];
                        visited[x][y+1] = true;
                    }
                    // finished processing [x][y]
                    finished[x][y] = true;

                    // color changing effect variables
                    colorQueue.add(new int[]{x,y});

                }
                noReset = false;
            }
        }

        public class runBFS implements Runnable {

            @Override
            public void run() {

                Node[][] nodeArr = new Node[42][25];
                for (int i = 0; i < 42; i++) {
                    for (int j = 0; j < 25; j++) {
                        nodeArr[i][j] = new Node(i,j, false);
                        if (i == 6 && j == 11) {
                            nodeArr[i][j].first = true;
                        }
                    }
                }


                noReset = true;
                bfs[34][11] = true;
                boolean[][] visited = new boolean[42][25];
                Queue<int[]> queue = new LinkedList<>();
                queue.add(new int[]{6,11});

                while (!queue.isEmpty()) {
                    int[] current = queue.remove();
                    int x = current[0];
                    int y = current[1];

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (bfs[x][y]) {
                        last = nodeArr[x][y];
                        break;
                    }

                    if (x - 1 >= 0 && !visited[x-1][y] && !dragArr[x-1][y]) {
                        queue.add(new int[]{x-1, y});
                        nodeArr[x-1][y].prev = nodeArr[x][y];
                        visited[x-1][y] = true;
                    }
                    if (x + 1 < 42 && !visited[x+1][y] && !dragArr[x+1][y]) {
                        queue.add(new int[]{x+1, y});
                        nodeArr[x+1][y].prev = nodeArr[x][y];
                        visited[x+1][y] = true;
                    }
                    if (y - 1 >= 0 && !visited[x][y-1] && !dragArr[x][y-1]) {
                        queue.add(new int[]{x, y-1});
                        nodeArr[x][y-1].prev = nodeArr[x][y];
                        visited[x][y-1] = true;
                    }
                    if (y + 1 < 25 && !visited[x][y+1] && !dragArr[x][y+1]) {
                        queue.add(new int[]{x, y+1});
                        nodeArr[x][y+1].prev = nodeArr[x][y];
                        visited[x][y+1] = true;
                    }
                    // finished processing [x][y]
                    finished[x][y] = true;

                    // color changing effect variables
                    colorQueue.add(new int[]{x,y});

                }
                noReset = false;
            }
        }
    }

    public class Move implements MouseMotionListener{

        @Override
        public void mouseDragged(MouseEvent e) {
            movingX = e.getX();
            movingY = e.getY();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            xCord = e.getX();
            yCord = e.getY();
        }
    }

    public class Clicker implements MouseListener {

        public int getX() {
            for (int x = 0; x < 42 ; x++) {
                for (int y = 0; y < 25; y++) {
                    if (xCord >= x * 30 + space + space + 2 && xCord < x * 30 + 30 + space + space + 1) {
                        return x;
                    }
                }
            }
            return -1;
        }

        public int getY() {
            for (int x = 0; x < 42 ; x++) {
                for (int y = 0; y < 25; y++) {
                    if (yCord >= space + y * 30 + 30 + 30 && yCord < space + y * 30 + 30 + 30 + 30 - space) {
                        return y;
                    }
                }
            }
            return -1;
        }

        public void reset() {
            for (int x = 0 ; x < 42; x ++) {
                for (int y = 0; y < 25; y++) {
                    dragArr[x][y] = false;
                    boolArr[x][y] = false;
                    finished[x][y] = false;
                    bfs[x][y] = false;
                    colorChanging[x][y] = false;
                    colorChanging2[x][y] = false;
                    pathOnce = false;
                    pathing[x][y] = false;
                    last = null;

                }
            }
        }

        public boolean insideReset() {
            if (xCord >= 1098 && xCord < 1258 && yCord >= 36 && yCord < 61) {
                return true;
            }
            return false;
        }

        public boolean insideBFS() {
            if (xCord >= 178 && xCord < 297 && yCord >= 36 && yCord < 61 ) {
                return true;
            }
            return false;
        }

        public boolean insideDFS() {
            if (xCord >= 303 && xCord < 423 && yCord >= 36 && yCord < 61) {
                return true;
            }
            return false;
        }

        public boolean insideVisualize() {
            if (xCord >= 877 && xCord <1078 && yCord >= 36 && yCord < 61) {
                return true;
            }
            return false;
        }


        @Override
        public void mouseClicked(MouseEvent e) {

           // System.out.println(xCord + " : " + yCord);

            if (getX() != -1 && getY() != -1) {
                boolArr[getX()][getY()] = !boolArr[getX()][getY()];
            }

            if (insideBFS()) {
                algorithm = "BFS";
            } else if (insideDFS()) {
                algorithm = "DFS";
            }

            if (insideVisualize()) {
                startAlgo = true;
            }

            if (insideReset() && !noReset) {
                reset();
                once = false;
                startAlgo = false;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

}

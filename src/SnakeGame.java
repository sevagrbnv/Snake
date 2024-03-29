import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class SnakeGame
{
    final String TITLE_OF_PROGRAM = "SnakeGame";
    final String GAME_OVER_MSG = "YOU ARE DEAD!";
    final int POINT_SIZE = 20;//размер квадрата сетки поля
    final int FIELD_WEIGHT =20; //points
    final int FIELD_WIDTH =30;//points
    final int FIELD_DX = 15;
    final int FIELD_DY = 38;
    final int START_LOCATION = 200;
    final int START_SNAKE_SIZE = 6;
    final int START_SNAKE_X = 10;
    final int START_SNAKE_Y = 10;
    final int SHOW_DELAY = 100;
    final int LEFT = 37;
    final int RIGHT = 39;
    final int UP = 38;
    final int DOWN = 40;
    final int START_DIRECTION = RIGHT;
    final Color DEFAULT_COLOR = Color.black;
    final Color FOOD_COLOR = Color.green;
    //final Color POISON_COLOR = Color.red;
    Snake snake;
    Food food;
    JFrame frame;
    Canvas canvasPanel;
    Random random = new Random();
    boolean gameOver = false;

    public static void main(String[] args) {
        HelloWorld helloWorld = new HelloWorld();
        HW hw = new HW();
        helloWorld.get();
        new SnakeGame().go();
    }

    void go()
    {
        frame = new JFrame(TITLE_OF_PROGRAM + " : " + START_SNAKE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH * POINT_SIZE + FIELD_DX, FIELD_WEIGHT * POINT_SIZE + FIELD_DY);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);

        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                snake.setDirection(e.getKeyCode());
            }
        });

        frame.setVisible(true);

        snake = new Snake(START_SNAKE_X, START_SNAKE_Y, START_SNAKE_SIZE, START_DIRECTION);
        food = new Food();
        while (!gameOver)
        {
            snake.move();
            if (food.isEaten())
                food.next();
            canvasPanel.repaint();
            try
            {
                Thread.sleep(SHOW_DELAY);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
    }

    class Snake
    {
        ArrayList<Point> snake = new ArrayList<>();
        int direction;

        public Snake(int x, int y, long length, int direction)
        {
            for (int i = 0; i < length; i++)
            {
                Point point = new Point(x-i, y);
                snake.add(point);
            }
            this.direction = direction;
        }

        boolean isInsideSnake(int x, int y)
        {
            for (Point point : snake)
            {
                if (point.getX() == x && point.getY() == y)
                    return true;
            }
            return false;
        }

        boolean isFood(Point food)
        {
            return ((snake.get(0).getX() == food.getX()) && (snake.get(0).getY() == food.getY()));
        }
        void move()
        {
            int x = snake.get(0).getX();
            int y = snake.get(0).getY();
            if (direction == LEFT) x--;
            if (direction == RIGHT) x++;
            if (direction == UP) y--;
            if (direction == DOWN) y++;
            if (x > FIELD_WIDTH - 1) x = 0;
            if (x < 0) x = FIELD_WIDTH - 1;
            if (y > FIELD_WEIGHT - 1) y = 0;
            if (y < 0) y = FIELD_WEIGHT - 1;
            gameOver = isInsideSnake(x, y);
            snake.add(0, new Point(x, y));
            if (isFood(food))
            {
                food.eat();
                frame.setTitle(TITLE_OF_PROGRAM + " : " + snake.size());
            }
            else snake.remove(snake.size() - 1);
        }

        void setDirection(int direction)
        {
            if (direction >= LEFT && direction <= DOWN)
            {
                if (Math.abs(this.direction - direction) != 2)
                    this.direction = direction;
            }
        }

        void paint(Graphics g)
        {
            for (Point point : snake)
            {
                point.paint(g);
            }
        }
    }

    class Food extends Point
    {
        public Food()
        {
            super(-1, -1);
            this.color = FOOD_COLOR;
        }

        void eat()
        {
            this.setXY(-1, -1);
        }

        boolean isEaten()
        {
            return this.getX() == -1;
        }

        void next()
        {
            int x, y;
            do
            {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_WEIGHT);
            } while (snake.isInsideSnake(x, y));
            this.setXY(x, y);
        }
    }

    class Point
    {
        int x,y;
        Color color = DEFAULT_COLOR;

        public Point(int x, int y)
        {
            this.setXY(x, y);
        }

        void paint(Graphics g)
        {
            g.setColor(color);
            g.fillRect(x * POINT_SIZE, y * POINT_SIZE, POINT_SIZE, POINT_SIZE);
        }

        int getX()
        {
            return x;
        }

        int getY()
        {
            return y;
        }

        void setXY(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    public class Canvas extends JPanel
    {
        @Override
        public void paint(Graphics g)
        {
            super.paint(g);
            snake.paint(g);
            food.paint(g);
            if (gameOver)
            {
                g.setColor(Color.red);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(GAME_OVER_MSG,
                        (FIELD_WIDTH * POINT_SIZE + FIELD_DX - fm.stringWidth(GAME_OVER_MSG))/2,
                        (FIELD_WEIGHT * POINT_SIZE + FIELD_DY)/2);
            }
        }
    }
}
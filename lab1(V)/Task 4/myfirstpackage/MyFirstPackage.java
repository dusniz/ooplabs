package myfirstpackage;

public class MySecondClass {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int a) {
        x = a;
    }

    public void setY(int a) {
        y = a;
    }

    public MySecondClass(int a, int b) {
        x = a;
        y = b;
    }

    public int get_max() {
        return x > y ? x : y;
    }
}
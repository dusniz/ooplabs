package myfirstpackage;

public class MyFirstPackage {

    private int a, b;

    public MyFirstPackage(int a, int b) { this.a = a; this.b = b; }

    public int getA() { return a; }
    public int getB() { return b; }

    public void setA(int a) { this.a = a; }
    public void setB(int b) { this.b = b; }

    public int sum() { return a + b; }
}
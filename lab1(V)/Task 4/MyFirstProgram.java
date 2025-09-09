import myfirstpackage.*;

class MyFirstClass {
    public static void main(String[] args) {
        MySecondClass o = new MySecondClass(5, 2);
        System.out.println(o.get_max());
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                o.setX(i);
                o.setY(j);
                System.out.print(o.get_max());
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}

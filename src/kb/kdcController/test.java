package kb.kdcController;

public class test {


    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println(String.valueOf(System.currentTimeMillis()).substring(9, 13));
    }
}
class A {
    private static int a;

    public A() {
        a = 5;
    }

    public static int getA() {
        return a;
    }
}

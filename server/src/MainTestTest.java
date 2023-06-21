import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;

class A {
    public int p1;
    public int p2;
    public String p3;

    public A() {
    }

    public A(int p1, int p2, String p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
}

public class MainTestTest {
    public static void main(String[] args) {

        A a = new A(1, 1, "abc");
        String s = Utils.toJson(a);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    A a1 = new A(1, 1, "abc");
                    String s = Utils.toJson(a);
                    A a2 = Utils.fromJson(s, A.class);
                    System.out.println(s);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).run();
    }
}

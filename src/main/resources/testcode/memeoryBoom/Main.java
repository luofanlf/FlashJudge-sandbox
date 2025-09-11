import java.util.List;
import java.util.ArrayList;

public class Main {
    
    public static void main(String[] args) {
        List<byte[]> list = new ArrayList<>();
        while (true) {
            // 每次申请 1MB 内存
            list.add(new byte[1024 * 1024]);
            System.out.println("Allocated " + list.size() + " MB");
            try {
                Thread.sleep(100); // 慢一点，避免瞬间 OOM
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

import java.io.IOException;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("开始测试 exec 命令...");
        
        try {
            // 尝试执行系统命令
            Process process = Runtime.getRuntime().exec("ls -la");
            System.out.println("exec 命令执行成功！");
            
            // 读取命令输出
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("SecurityException: " + e.getMessage());
            System.out.println("Security Manager 成功阻止了 exec 命令！");
        } catch (Exception e) {
            System.out.println("其他异常: " + e.getMessage());
        }
        
        System.out.println("测试完成");
    }
}

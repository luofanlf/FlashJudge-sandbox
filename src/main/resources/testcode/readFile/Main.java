import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main {
    
    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/application.yml";
        List<String> allines = Files.readAllLines(Paths.get(filePath));
        System.out.println(String.join("\n",allines));
    }
}

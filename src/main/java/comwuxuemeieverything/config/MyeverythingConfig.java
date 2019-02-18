//配置
package comwuxuemeieverything.config;
import lombok.Getter;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter  //get方法生成完成
public class  MyeverythingConfig {
    private static volatile MyeverythingConfig config; // volatile保证可见性，一致性，所有线程中都是一致的

    /**
     * 建立索引的路径
     */
    private Set<String> includePath = new HashSet<>();
    /**
     * 排除索引文件的路径
     */
    private Set<String> excludePath = new HashSet<>();
    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir") + File.separator + "myeverything";
    // 可配置参数


    private MyeverythingConfig () {
    }

    public static MyeverythingConfig  getInstance() {
        if (config == null) {
            synchronized(MyeverythingConfig .class) {
                if (config == null) {
                    config = new MyeverythingConfig ();
                    //1.获取文件系统
                    FileSystem fileSystem = FileSystems.getDefault();
                    //遍历的目录
                    Iterable<Path> iterable = fileSystem.getRootDirectories();
                    iterable.forEach(path -> config.includePath.add(path.toString()));
                    //排除的目录
                    //windows ： C:\Windows C:\Program Files (x86) C:\Program Files  C:\ProgramData
                    //linux : /tmp /etc
                    //unix
                    String osname = System.getProperty("os.name");
                    if (osname.startsWith("Windows")) {
                        config.getExcludePath().add("C:\\Windows");
                        config.getExcludePath().add("C:\\Program Files (x86)");
                        config.getExcludePath().add("C:\\Program Files");
                        config.getExcludePath().add("C:\\ProgramData");

                    } else {
                        config.getExcludePath().add("/tmp");
                        config.getExcludePath().add("/etc");
                        config.getExcludePath().add("/root");
                    }

                }
            }
        }
        return config;
    }

    public static void main(String[] args) {
        FileSystem fileSystem = FileSystems.getDefault();
        //遍历的目录
        Iterable<Path> iterable = fileSystem.getRootDirectories();

        System.out.println(iterable);


    }
}

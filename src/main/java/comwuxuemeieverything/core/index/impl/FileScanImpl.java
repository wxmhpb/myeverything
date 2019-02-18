package comwuxuemeieverything.core.index.impl;
import comwuxuemeieverything.config.MyeverythingConfig;
import comwuxuemeieverything.core.index.FileScan;
import comwuxuemeieverything.core.interceptor.FileInterceptor;
import java.io.File;
import java.util.LinkedList;
public class FileScanImpl implements FileScan {
    private MyeverythingConfig config = MyeverythingConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path) {
        File file = new File(path);
        if (file.isFile()) {
            //D:\a\b\abc.pdf  ->  D:\a\b
            if (config.getExcludePath().contains(file.getParent())) {
                return;
            }
        } else {
            if (config.getExcludePath().contains(path)) {
                return;
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        index(f.getAbsolutePath());
                    }
                }
            }
        }

        //File Directory
        for (FileInterceptor interceptor : this.interceptors) {
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}

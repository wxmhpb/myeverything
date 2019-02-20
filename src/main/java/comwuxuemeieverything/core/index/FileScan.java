package comwuxuemeieverything.core.index;
import comwuxuemeieverything.core.dao.DataSourceFactory;
import comwuxuemeieverything.core.dao.impl.FileIndexDaoImpl;
import comwuxuemeieverything.core.index.impl.FileScanImpl;
import comwuxuemeieverything.core.interceptor.FileInterceptor;
import comwuxuemeieverything.core.interceptor.impl.FileIndexInterceptor;
import comwuxuemeieverything.core.interceptor.impl.FilePrintInterceptor;
public interface FileScan {
    //遍历path
    void index(String path);
    //遍历拦截器
    void interceptor(FileInterceptor interceptor);
    public static void main(String[] args) {
        DataSourceFactory.initDatabase();
        FileScan scan = new FileScanImpl();
      FileInterceptor printInterceptor = new FilePrintInterceptor();
        scan.interceptor(printInterceptor);

     FileInterceptor fileIndexInterceptor = new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
       scan.interceptor(fileIndexInterceptor);

    scan.index("D:\\test");

   }
}

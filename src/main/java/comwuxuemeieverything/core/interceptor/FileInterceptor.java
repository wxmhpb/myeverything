//拦截器
package comwuxuemeieverything.core.interceptor;
import java.io.File;
@FunctionalInterface  //函数式编程
public interface FileInterceptor {
    void apply(File file);
}

package comwuxuemeieverything.core;
import comwuxuemeieverything.config.MyeverythingConfig;
import comwuxuemeieverything.core.index.FileScan;
import comwuxuemeieverything.core.search.FileSearch;
import comwuxuemeieverything.core.dao.DataSourceFactory;
import comwuxuemeieverything.core.dao.FileIndexDao;
import comwuxuemeieverything.core.dao.impl.FileIndexDaoImpl;
import comwuxuemeieverything.core.index.FileScan;
import comwuxuemeieverything.core.index.impl.FileScanImpl;
import comwuxuemeieverything.core.interceptor.impl.FileIndexInterceptor;
import comwuxuemeieverything.core.interceptor.impl.ThingClearInterceptor;
import comwuxuemeieverything.core.model.Condition;
import comwuxuemeieverything.core.model.Thing;
import comwuxuemeieverything.core.search.impl.FileSearchImpl;
import javax.sql.DataSource;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutorService;

public class MyeverythingManager {
    private static volatile MyeverythingManager manager;

    private FileSearch fileSearch;

    private FileScan fileScan;

    private ExecutorService executorService;
    //清理删除文件
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;//清理现场
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);//原子性，初始值为false

    private MyeverythingManager() {
        this.initComponent();//初始化构件
    }

    private void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();

        //检查数据库
        //第一次使用数据库时初始化
        initOresetDatabase();
        //业务层对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);

        this.fileSearch = new FileSearchImpl(fileIndexDao);

        this.fileScan = new FileScanImpl();
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));

        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Thing-Clear");
        this.backgroundClearThread.setDaemon(true);
    }

    //存在bug
    private void initOresetDatabase() {
            DataSourceFactory.initDatabase();
        }

    //懒汉式单例模式，double check
    public static MyeverythingManager getInstance() {
        if (manager == null) {
            synchronized ((MyeverythingManager.class)) {
                if (manager == null) {
                    manager = new MyeverythingManager();
                }
            }
        }
        return manager;
    }
    //检索
    public List<Thing> search(Condition condition) {
        //Stream 流式处理 JDK8
        return this.fileSearch.search(condition)
                .stream()
                .filter(thing -> {
                    String path = thing.getPath();
                    File f = new File(path);
                    boolean flag = f.exists();
                    if (!flag) {
                        //做删除
                        thingClearInterceptor.apply(thing);
                    }
                    return flag;

                }).collect(Collectors.toList());
    }

    //索引
    public void buildIndex() {
        initOresetDatabase();//重置时初始化
        Set<String> directories = MyeverythingConfig.getInstance().getIncludePath();
        if (this.executorService == null) {
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                    return thread;
                }
            });
        }
        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());
        System.out.println("Build index start ....");
        for (String path : directories) {
            this.executorService.submit(() -> {
                MyeverythingManager.this.fileScan.index(path);
                //当前任务完成，值-1
                countDownLatch.countDown();
            });
        }
        //阻塞，直到任务完成，值0
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build index complete ...");
    }


    /**
     * 启动清理线程
     */
    public void startBackgroundClearThread() {
        if (this.backgroundClearThreadStatus.compareAndSet(false, true)) {
            this.backgroundClearThread.start();
        } else {
            System.out.println("Cant repeat start BackgroundClearThread");
        }
    }
}


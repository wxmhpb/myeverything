package comwuxuemeieverything.core.monitor.impl;

import comwuxuemeieverything.core.dao.FileIndexDao;
import comwuxuemeieverything.core.monitor.FileWatch;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import comwuxuemeieverything.core.common.FileConvertThing;
import comwuxuemeieverything.core.common.HandlePath;
import org.apache.commons.io.monitor.FileAlterationObserver;
import java.io.File;
public class FileWatchImpl implements FileWatch,FileAlterationListener {
    private FileIndexDao fileIndexDao;
    private FileAlterationMonitor monitor;
    public FileWatchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.monitor = new FileAlterationMonitor(10); //监听：遍历间隔时间
    }

    @Override //可忽略
    public void onStart(FileAlterationObserver observer) {
//        observer.addListener(this);
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("onDirectoryCreate " + directory);
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("onDirectoryChange " + directory);
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("onDirectoryDelete " + directory);
    }

    @Override
    public void onFileCreate(File file) {
        //文件创建
        System.out.println("onFileCreate " + file);
        this.fileIndexDao.insert(FileConvertThing.convert(file)); //事件通知文件创建，就插入
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("onFileChange " + file);  //验证监听是否成功
    }

    @Override
    public void onFileDelete(File file) {
        //文件删除
        System.out.println("onFileDelete " + file);
        this.fileIndexDao.delete(FileConvertThing.convert(file));
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
//        observer.removeListener(this);
    }

    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(HandlePath handlePath) {
        //监控的是includePath集合
        for (String path : handlePath.getIncludePath()) {
            FileAlterationObserver observer = new FileAlterationObserver(  //两个参数，目录 :将excludepath目录排除掉
                    path, pathname -> {
                String currentPath = pathname.getAbsolutePath();
                for (String excludePath : handlePath.getExcludePath()) {
                    if (excludePath.startsWith(currentPath)) {
                        return false;
                    }
                }
                return true;
            });
            observer.addListener(this); //listenor接到文件系统的通知
            this.monitor.addObserver(observer);  //调度监控，启动监听器
        }
    }

    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


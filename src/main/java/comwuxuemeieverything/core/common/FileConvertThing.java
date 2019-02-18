package comwuxuemeieverything.core.common;
import comwuxuemeieverything.core.model.FileType;
import comwuxuemeieverything.core.model.Thing;

import java.io.File;
public final class FileConvertThing { //文件转换为Thing对象
    private FileConvertThing() {
    }
    public static Thing convert(File file) {
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computeFileDepth(file));
        thing.setFileType(computeFileType(file));
        return thing;
    }

    private static int computeFileDepth(File file) { //计算深度
        int dept = 0;

        String[] segments = file.getAbsolutePath().split("\\\\"); //以\\作为分隔符，  用\\进行转义
        dept = segments.length;
        return dept;
    }

    private static FileType computeFileType(File file) { //文件类型
        if (file.isDirectory()) { //判断如果是一个目录，返回OTHER
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");//从尾部开始以  .  后面的扩展名判断文件类型
        if (index != -1 && index < fileName.length() - 1) {
            //abc.
            String extend = fileName.substring(index + 1);  //扩展名从index+1以后开始截取
            return FileType.lookup(extend);
        } else {
            return FileType.OTHER;
        }
    }
}

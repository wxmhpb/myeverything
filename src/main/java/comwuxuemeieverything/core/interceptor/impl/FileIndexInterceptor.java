package comwuxuemeieverything.core.interceptor.impl;
import comwuxuemeieverything.core.common.FileConvertThing;
import comwuxuemeieverything.core.dao.FileIndexDao;
import comwuxuemeieverything.core.interceptor.FileInterceptor;
import comwuxuemeieverything.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements  FileInterceptor {
      private final FileIndexDao fileIndexDao;
      public FileIndexInterceptor(FileIndexDao fileIndexDao){
          this.fileIndexDao=fileIndexDao;
      }
      public void apply(File file){
          Thing thing=FileConvertThing.convert(file);
          fileIndexDao.insert(thing);
      }
}

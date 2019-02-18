package comwuxuemeieverything.core.search.impl;
import comwuxuemeieverything.core.dao.FileIndexDao;
import comwuxuemeieverything.core.model.Condition;
import comwuxuemeieverything.core.model.Thing;
import comwuxuemeieverything.core.search.FileSearch;
import java.util.List;
//查询  业务层
public class FileSearchImpl implements FileSearch {
   private final FileIndexDao fileIndexDao;
   //采用构造方法初始化赋值，使用起来更加灵活多变，什么时候改变值，对后续结果不影响
   public FileSearchImpl(FileIndexDao fileIndexDao){
       this.fileIndexDao=fileIndexDao;
   }
   public List<Thing>search(Condition condition){
       return this.fileIndexDao.search(condition);
   }
}

package comwuxuemeieverything.core.dao;

import comwuxuemeieverything.core.model.Condition;
import comwuxuemeieverything.core.model.Thing;
import java.util.List;
//业务层访问数据库的CRUD
public interface FileIndexDao {
    //插入数据Thing
    void insert(Thing thing);
    //删除数据
    void delete(Thing thing);
   // 根据condition条件进行数据库的检索
    List<Thing> search(Condition condition);
}

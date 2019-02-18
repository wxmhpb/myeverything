package comwuxuemeieverything.core.search;
import comwuxuemeieverything.core.model.Condition;
import comwuxuemeieverything.core.model.Thing;
import java.util.List;
public interface FileSearch {
    //根据Condition条件进行检索
    default List<Thing> search(Condition condition) {
        return null;
    }
}

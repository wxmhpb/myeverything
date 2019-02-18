package comwuxuemeieverything.core.model;
import lombok.Data;
@Data   //getter setter toString 生成完成
public class Condition {
  private String name;
  private String fileType;
  private Integer limit;
  //检索结果的depth信息排序规则
  //Asc->true   升序
  //Asc->flase   降序
  private Boolean orderByAsc;
}

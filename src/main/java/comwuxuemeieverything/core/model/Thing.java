package comwuxuemeieverything.core.model;
import lombok.Data;

/**
 * 文件属性信息索引之后的记录 Thing表示
 */
@Data  //getter setter toString生成完成
public class Thing {
    private String name; //文件名称

    private String path; //文件路径

    private Integer depth; //文件路径深度

    private FileType fileType; //文件类型
}

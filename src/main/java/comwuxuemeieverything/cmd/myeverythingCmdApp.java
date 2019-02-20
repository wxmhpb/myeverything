package comwuxuemeieverything.cmd;
import comwuxuemeieverything.config.MyeverythingConfig;
import comwuxuemeieverything.core.MyeverythingManager;
import comwuxuemeieverything.core.model.Thing;
import comwuxuemeieverything.core.model.Condition;
import java.util.*;
/**
 * Author: secondriver
 * Created: 2019/2/14
 * Description: 比特科技，只为更好的你；你只管学习，其它交给我。
 */
//客户端
//命令行代码
public class myeverythingCmdApp {
    private static Scanner sc=new Scanner(System.in);
    public static void main(String[] args) {
        //解析参数
        parseParams(args);
       //欢迎
        welcome();
        //统一调度器
        MyeverythingManager manager=MyeverythingManager.getInstance();
        //启动后台清理线程
        manager.startBackgroundClearThread();
        //启动监控
        manager.startFileSystemMonitor();
        //交互式
        interactive(manager);
    }
    private static void parseParams(String[]args) {
        MyeverythingConfig config = MyeverythingConfig.getInstance();
        //处理参数，如果用户指定的参数格式不对，使用默认值即可
        for (String param : args) {
            String maxReturnParam = "--maxReturn=";  //最大数量
            if (param.startsWith(maxReturnParam)) {
                int index = param.indexOf("=");//以等号建立索引
                String maxReturnStr = param.substring(index + 1);//等号后一个开始截取
             //参数格式不对，抛异常
              try {
                    int maxReturn = Integer.parseInt(maxReturnStr); //字符串包装为整形，最大数量
                    config.setMaxReturn(maxReturn);//配置最大数量
              } catch (NumberFormatException e) {
                    //如果用户指定的参数格式不对，使用默认值即可
                }
            }
            //排序策略
            String deptOrderByAscParam = "--deptOrderByAsc=";
            if (param.startsWith(deptOrderByAscParam)) {
                //--maxReturn=value
                int index = param.indexOf("=");
                String deptOrderByAscStr = param.substring(index + 1);
                boolean deptOrderByAsc = Boolean.parseBoolean(deptOrderByAscStr);
                config.setDeptOrderAsc(deptOrderByAsc);
            }
            //索引文件目录
            String includePathParam="--includePath=";
            if(param.startsWith(includePathParam)){
                int index=param.indexOf("=");
                String includePathStr=param.substring(index+1);
                String[]includePaths=includePathStr.split(";");
                if(includePaths.length>0){
                    config.getIncludePath().clear(); //如果路径长度大于0，就有文件目录存在，则将其清理掉
                }
                for(String p:includePaths){
                    config.getIncludePath().add(p);  //添加目录
                }
            }
            //排除文件目录，不需要清理
            String excludePathParam = "--excludePath=";
            if (param.startsWith(includePathParam)) {
                //--excludePath=values (;)
                int index = param.indexOf("=");
                String excludePathStr = param.substring(index + 1);
                String[] excludePaths = excludePathStr.split(";");
                config.getExcludePath().clear();
                for (String p : excludePaths) {
                    config.getExcludePath().add(p);
                }
            }
        }
    }
    private static void welcome()
    {
        System.out.println("欢迎使用,myeverything");
    }
    private static void interactive(MyeverythingManager manager){
            while(true){ //无限循环输入
                System.out.println("myeverything>>");
                String input=sc.nextLine();
                //search 后面有参数，优先处理search
                if(input.startsWith("search")){
                    // search name [file_type]
                    String[] values=input.split(" ");//输入的查询内容以空格作为分隔符
                    if(values.length>=2){
                        if(!values[0].equals("search")){
                            help();
                            continue;
                        }
                        Condition condition = new Condition();
                        String name = values[1]; //第一个参数为查询的文件名字
                        condition.setName(name);
                        if (values.length >= 3) {
                            String fileType = values[2];//第二个参数为文件类型
                            condition.setFileType(fileType.toUpperCase());//文件类型都为大写字母
                        }
                        search(manager,condition);
                        continue;
                    }else{
                        help();
                        continue;
                    }
                }
                switch(input){
                    case "help":
                        help();
                        break;
                    case "quit":
                        quit();
                        break;
                    case "index":
                        index(manager);
                        break;
                    default:
                        help();
                }
        }
    }
    private static void search(MyeverythingManager manager,Condition condition){
        //name fileType limit orderByAsc
        condition.setLimit(MyeverythingConfig.getInstance().getMaxReturn());
        condition.setOrderByAsc(MyeverythingConfig.getInstance().getDeptOrderAsc());
        System.out.println(condition.toString());
        List<Thing>thingList=manager.search(condition);
        for(Thing thing:thingList){
            System.out.println(thing.getPath());
        }
    }
    private static void index(MyeverythingManager manager){
        //统一调度器中的index
        new Thread(manager::buildIndex).start();//启动一个建立索引的线程
    }
    private static void quit(){
        System.out.println("再见");
        System.exit(0);
    }
    private static void help(){
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<file-Type> img | doc | bin | archive | other]");
    }
}

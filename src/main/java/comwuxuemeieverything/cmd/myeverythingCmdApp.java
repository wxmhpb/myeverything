package comwuxuemeieverything.cmd;
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
       //欢迎
        welcome();
        //统一调度器
        MyeverythingManager manager=MyeverythingManager.getInstance();
        //启动后台清理线程
        manager.startBackgroundClearThread();
        //交互式
        interactive(manager);
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

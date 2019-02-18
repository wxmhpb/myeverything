package comwuxuemeieverything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class DataSourceFactory {

    /**
     * 数据源（单例）
     */
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory() {

    }

    public static DataSource dataSource() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC  driver class
                    dataSource.setDriverClassName("org.h2.Driver");//通过反射
                    //h2有两种形式数据库：嵌入式和服务器式   服务器式数据库不在本地，java源码和数据库分离，所以采用嵌入式数据库
                    //url, username, password
                    //采用的是H2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口

                    //JDBC规范中关于MySQL  jdbc:mysql://ip:port/databaseName
                    //获取当前工程路径
                    String workDir = System.getProperty("user.dir");
                    //JDBC规范中关于H2 jdbc:h2:filepath ->存储到本地文件
                    //JDBC规范中关于H2 jdbc:h2:~/filepath ->存储到当前用户的home目录
                    //JDBC规范中关于H2 jdbc:h2://ip:port/databaseName ->存储到服务器
                    dataSource.setUrl("jdbc:h2:" + workDir + File.separator + "myeverything");//workDir获取当前工作目录
                    //Druid的可配置参数
                    //第一种方式
                    dataSource.setValidationQuery("select now()"); // 验证查询
                    //第二种方式
                    // dataSource.setTestWhileIdle(false);
                }
            }
        }
        return dataSource;
    }

    public static void initDatabase() { //初始化数据库
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        //2.获取SQL语句
        //不采取读取绝对路径文件
        //采取读取classpath路径下的文件
        try (InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("myeverything.sql");) {
            if (in == null) { //判断sql语句是否为空，如果为空，抛出异常
                throw new RuntimeException("Not read init database script please check it");
            }
            StringBuilder sqlBuilder = new StringBuilder();//此处不考虑线程安全问题，各自的方法是私有的，所以用StringBuilder
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("--")) {  //判断sql语句所在行是否为注释，如果不是注释则拼接
                        sqlBuilder.append(line);
                    }
                }
            }
            //3.获取数据库连接和名称执行SQL
            String sql = sqlBuilder.toString();//StringBuilder转换为String类
            //JDBC
            //3.1获取数据库的连接
            Connection connection = dataSource.getConnection();
            //3.2创建命令
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.execute(); //3.3执行SQL语句
            connection.close();//关闭连接
            statement.close(); //关闭命令
        } catch (IOException | SQLException e) {
            e.printStackTrace();
    }
    }
}
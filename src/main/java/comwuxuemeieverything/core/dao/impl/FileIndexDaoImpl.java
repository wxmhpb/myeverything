package comwuxuemeieverything.core.dao.impl;
import comwuxuemeieverything.core.dao.FileIndexDao;
import comwuxuemeieverything.core.model.Condition;
import comwuxuemeieverything.core.model.FileType;
import comwuxuemeieverything.core.model.Thing;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileIndexDaoImpl implements  FileIndexDao {
    private final DataSource dataSource;//以后不能改变

    //获取数据源
    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //插入数据
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //获取数据库连接
            connection = dataSource.getConnection();
            //准备SQL语句
            String sql = "insert into file_index(name, path, depth, file_type) values (?,?,?,?)";
            //准备命令
            statement = connection.prepareStatement(sql);
            //设置参数  name path  depth FileType
            statement.setString(1, thing.getName());
            statement.setString(2, thing.getPath());
            statement.setInt(3, thing.getDepth());
//            FileType.DOC  ->  DOC
            statement.setString(4, thing.getFileType().name());
            //执行命令
            statement.executeUpdate();//更新数据
        } catch (SQLException e) {

        } finally {
            releaseResource(null, statement, connection);
        }
    }

    @Override
    public void delete(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            String sql = "delete from file_index where path like '" + thing.getPath() + "%'";
            //3.准备命令
            statement = connection.prepareStatement(sql);
            //4.设置参数 1 2 3 4
            //5.执行命令
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(null, statement, connection);
        }
    }

    public List<Thing> search(Condition condition) {
        List<Thing> things = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            //name     ：  like
            //fileType : =
            //limit    : limit offset
            //orderbyAsc : order by    按照深度升序排序
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" select name, path, depth, file_type from file_index ");
            //name匹配：前模糊，后模糊，前后模糊
            sqlBuilder.append(" where ")
                    .append(" name like '%")
                    .append(condition.getName())
                    .append("%' ");
            if (condition.getFileType() != null) {
                sqlBuilder.append(" and file_type = '")
                        .append(condition.getFileType().toUpperCase()) //文件类型返回值设置全为大写字母
                        .append("' ");
            }
            //limit, order必选的
            sqlBuilder.append(" order by depth ")
                    .append(condition.getOrderByAsc() ? "asc" : "desc")
                    .append(" limit ")
                    .append(condition.getLimit())
                    .append(" offset 0 ");//偏移量从0开始

//            System.out.println(sqlBuilder.toString());
            //3.准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            //4.设置参数 1 2 3 4
            //5.执行命令
            resultSet = statement.executeQuery();
            //6.处理结果
            while (resultSet.next()) {
                //数据库的中行记录 -->  Java中的对象Thing
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType = resultSet.getString("file_type");
                thing.setFileType(FileType.lookupByName(fileType));
                things.add(thing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(resultSet, statement, connection);
        }
        return things;
    }


    //解决内部代码大量重复问题： 重构
    private void releaseResource(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
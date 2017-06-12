
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleDataSourceSimpleApplication.class)
public class MultiDataSourceTest {

  @Qualifier("DataSource-ds")
  @Autowired
  private DataSource druidDataSource;

  @Test
  public void select() throws SQLException {
    String orderSql = "select i.* from  t_user i limit 1, 10";
    Connection connection = druidDataSource.getConnection();
    Statement statement = connection.createStatement();
    for (int i = 0; i < 10; i++) {

      ResultSet resultSet = statement.executeQuery(orderSql);
      while (resultSet.next()) {
        System.out.println(resultSet.getInt(1));
        System.out.println(resultSet.getString(2));
        System.out.println(resultSet.getString(3));
      }
    }
    statement.close();

    connection.close();
  }

  @Test
  public void selectMaster() throws SQLException {
    String orderSql = "select i.* from  t_user i limit 0, 10";
//    HintManager.getInstance().setMasterRouteOnly();
    Connection connection = druidDataSource.getConnection();
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(orderSql);
    while (resultSet.next()) {
      System.out.println(resultSet.getInt(1));
      System.out.println(resultSet.getString(2));
      System.out.println(resultSet.getString(3));
    }
    statement.close();
    connection.close();
  }

  @Test
  public void insert() throws SQLException {
    String sql = "INSERT INTO t_user"
        + "(username,"
        + "password)"
        + "VALUES"
        + "(?,"
        + "?)";
    Connection connection = druidDataSource.getConnection();
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, "boddi"+ UUID.randomUUID().toString().substring(0,10));
    statement.setString(2, "boddi");
    statement.execute();
    statement.close();
    connection.close();
  }



  @Test
  public void update() throws SQLException {
    Connection connection = druidDataSource.getConnection();

    final String sql = "update t_user set username=? where id=?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, "boddi_new");
    statement.setInt(2, 3);
    statement.execute();
    statement.close();
    connection.close();

  }

  @Test
  public void delete() throws SQLException {
    Connection connection = druidDataSource.getConnection();
    final String sql = "delete from t_user where id =1";
    Statement statement = connection.createStatement();
    statement.execute(sql);
    statement.close();
    connection.close();
  }


}
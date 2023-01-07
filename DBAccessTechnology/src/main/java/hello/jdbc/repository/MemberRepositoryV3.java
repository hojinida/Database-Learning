package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql="insert into member(member_id, money) values (?, ?)";

        Connection connection=null;
        PreparedStatement preparedStatement=null;

        try{
            connection= getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2,member.getMoney());
            preparedStatement.executeUpdate();
            return member;
        }catch (SQLException e) {
            log.info("DB ERROR",e);
            throw e;
        }finally {
            close(connection,preparedStatement,null );
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql="select * from member where member_id = ?";
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet;

        try{
            connection= getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Member member=new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId="+memberId);
            }
        }catch (SQLException e) {
            log.info("DB ERROR",e);
            throw e;
        }finally {
            close(connection,preparedStatement,null );
        }
    }


    public Member findById(Connection connection,String memberId) throws SQLException {
        String sql="select * from member where member_id = ?";
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        try{
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Member member=new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId="+memberId);
            }
        }catch (SQLException e) {
            log.info("DB ERROR",e);
            throw e;
        }finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
            //JdbcUtils.closeConnection(connection);
        }
    }
    public void update(String memberId, int money) throws SQLException {
        String sql="update member set money=? where member_id=?";

        Connection connection=null;
        PreparedStatement preparedStatement=null;

        try{
            connection= getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,money);
            preparedStatement.setString(2, memberId);
            int resultSize=preparedStatement.executeUpdate();
            log.info("resultSize={}",resultSize);
        }catch (SQLException e) {
            log.info("DB ERROR",e);
            throw e;
        }finally {
            close(connection,preparedStatement,null );
        }
    }

    public void update(Connection connection,String memberId, int money) throws SQLException {
        String sql="update member set money=? where member_id=?";

        PreparedStatement preparedStatement=null;

        try{
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,money);
            preparedStatement.setString(2, memberId);
            int resultSize=preparedStatement.executeUpdate();
            log.info("resultSize={}",resultSize);
        }catch (SQLException e) {
            log.info("DB ERROR",e);
            throw e;
        }finally {
            JdbcUtils.closeStatement(preparedStatement);
            //JdbcUtils.closeConnection(connection);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql="delete from member where member_id=?";

        Connection connection=null;
        PreparedStatement preparedStatement=null;

        try{
            connection= getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            log.info("DB ERROR",e);
            throw e;
        }finally {
            close(connection,preparedStatement,null );
        }
    }

    private void close(Connection connection, PreparedStatement statement, ResultSet resultSet){
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        DataSourceUtils.releaseConnection(connection,dataSource);
        //JdbcUtils.closeConnection(connection);
    }

    private Connection getConnection() throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}",connection);
        return connection;
    }
}
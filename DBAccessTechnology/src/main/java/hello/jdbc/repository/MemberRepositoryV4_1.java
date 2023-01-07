package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
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
public class MemberRepositoryV4_1 implements MemberRepository {

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member){
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
            throw new MyDbException(e);
        }finally {
            close(connection,preparedStatement,null );
        }
    }

    @Override
    public Member findById(String memberId){
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
            throw new MyDbException(e);
        }finally {
            close(connection,preparedStatement,null );
        }
    }

    @Override
    public void update(String memberId, int money){
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
            throw new MyDbException(e);
        }finally {
            close(connection,preparedStatement,null );
        }
    }

    @Override
    public void delete(String memberId){
        String sql="delete from member where member_id=?";

        Connection connection=null;
        PreparedStatement preparedStatement=null;

        try{
            connection= getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            throw new MyDbException(e);
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
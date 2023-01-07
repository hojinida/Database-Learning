package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 memberRepositoryV1;

    @BeforeEach
    void beforeEach(){
        //DriverManagerDataSource dataSource=new DriverManagerDataSource(URL,USERNAME,PASSWORD);
        HikariDataSource dataSource=new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");
        memberRepositoryV1=new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV3", 20000);
        memberRepositoryV1.save(member);

        Member findMember =memberRepositoryV1.findById(member.getMemberId());
        log.info("findMember={}",findMember);
        assertThat(findMember).isEqualTo(member);

        memberRepositoryV1.update(member.getMemberId(), 100000);
        Member updateMember=memberRepositoryV1.findById(member.getMemberId());
        log.info("findMember={}",updateMember);
        assertThat(updateMember.getMoney()).isEqualTo(100000);

        memberRepositoryV1.delete(member.getMemberId());
        assertThatThrownBy(()->memberRepositoryV1.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

    }
}
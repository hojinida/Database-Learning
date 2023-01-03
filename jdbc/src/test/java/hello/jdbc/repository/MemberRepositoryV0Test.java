package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 memberRepositoryV0=new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV3", 20000);
        memberRepositoryV0.save(member);

        Member findMember =memberRepositoryV0.findById(member.getMemberId());
        log.info("findMember={}",findMember);
        assertThat(findMember).isEqualTo(member);

        memberRepositoryV0.update(member.getMemberId(), 100000);
        Member updateMember=memberRepositoryV0.findById(member.getMemberId());
        log.info("findMember={}",updateMember);
        assertThat(updateMember.getMoney()).isEqualTo(100000);
        
        memberRepositoryV0.delete(member.getMemberId());
        assertThatThrownBy(()->memberRepositoryV0.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

    }
}
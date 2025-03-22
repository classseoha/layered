package com.example.layered.repository;

import com.example.layered.dto.MemoResponseDto;
import com.example.layered.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository //@Repository 꼭 넣어줘야 함
public class JdbcTemplateMemoRepository implements MemoRepository {

    private  final JdbcTemplate jdbcTemplate;

    public JdbcTemplateMemoRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public MemoResponseDto saveMemo(Memo memo) {
        //jdbcInsert를 사용하면 INSERT Query를 문자열로 직접 작성하지 않아도 됨
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("memo").usingGeneratedKeyColumns("id"); //메모 테이블에 인서트 하겠다. 라는 뜻, 이 테이블의 키값은 id로 설정되어 있다.

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", memo.getTitle()); //파라미터를 통해서 타이틀 이라는 컬럼에는 메모의 타이틀을 넣고 컨텐츠 컬럼에는 메모의 컨텐츠를 넣겠다라는 의미
        parameters.put("contents", memo.getContents());

        //저장 후 생선된 key값(id값)을 number 타입으로 반환하는 메서드
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return new MemoResponseDto(key.longValue(), memo.getTitle(), memo.getContents()); //메모리스폰스디티오 형태로 반환
    }

    @Override
    public List<MemoResponseDto> findAllMemos() {

        return jdbcTemplate.query("select * from memo", memoRowMapper()); //해당 메서드의 리턴타입이 리스트 형태의 MemoResponseDto인데 memoRowMapper 메서드가 그형태로 바로 조회되게 해줌
    }

    @Override
    public Optional<Memo> findMemoById(Long id) { //Optional은 null값을 안전하게 다루기 위해 사용하는 랩퍼클래스
        List<Memo> result = jdbcTemplate.query("select * from memo where id = ?", memoRowMapperV2(), id);//마지막에 들어가는 id값이 ?와 치환되면서 값이 들어갈 예정
        return result.stream().findAny();//findAny를 사용하면 옵셔널 형태의 메모가 반환됨 >> 리스트형태의 메모는 비어있으면 null이 뜰 수 있기 때문에 findAny로 옵셔널 형태로 만듬
    }

    @Override
    public Memo findMemoByIdOrElseThrow(Long id) {
        List<Memo> result = jdbcTemplate.query("select * from memo where id = ?", memoRowMapperV2(), id);
        return result.stream().findAny().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exists id = " + id));//findAny 뒤에 orElseThrow가 붙으면 조회된 메모가 없는 경우 익셉션을 발생시키겠다라는 뜻
    }

    @Override
    public int updateMemo(Long id, String title, String contents) {

        return jdbcTemplate.update("update memo set title = ?, contents = ? where id = ?", title, contents, id); //쿼리에 반영된 ROW의 수가 int로 반환됨
    }

    @Override
    public int updateTitle(Long id, String title) {

        return jdbcTemplate.update("update memo set title = ? where id = ?", title, id);
    }

    @Override
    public int deleteMemo(Long id) {

        return jdbcTemplate.update("delete from memo where id = ?", id); //수정된 Row 수만큼 int타입이 반환됨
    }

    private RowMapper<MemoResponseDto> memoRowMapper() {

        return new RowMapper<MemoResponseDto>() {
            @Override
            public MemoResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new MemoResponseDto(rs.getLong("id"), rs.getString("title"), rs.getString("contents"));
            }
        };
    }

    private RowMapper<Memo> memoRowMapperV2() {
        return new RowMapper<Memo>() {
            @Override
            public Memo mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Memo(rs.getLong("id"), rs.getString("title"), rs.getString("contents"));
            }
        };
    }
}

package com.varchar.biz.hashtag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("ReviewHashtagDAO")
public class ReviewHashtagDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	static final private String SQL_SELECTALL = "SELECT rh.REVIEW_HASHTAG_NUM, rh.REVIEW_HASHTAG_CONTENT "
												+ "FROM REVIEW_HASHTAG rh "
												+ "JOIN HASHTAG_DETAIL hd ON hd.HASHTAG_NUM = rh.REVIEW_HASHTAG_NUM "
												+ "WHERE hd.ITEM_NUM = ?";

	static final private String SQL_SELECTALL_SEARCH = "SELECT r.REVIEW_NUM, r.BUY_SERIAL, m.MEMBER_NAME, r.REVIEW_CONTENT, r.REVIEW_INSERT_TIME, rh.REVIEW_HASHTAG_CONTENT, i.IMAGE_URL "
														+ "FROM REVEIW_HASHTAG rh "
														+ "JOIN HASHTAG_DETAIL hd ON hd.HASHTAG_NUM = rh.REVIEW_HASHTAG_NUM "
														+ "JOIN REVIEW r ON r.REVIEW_NUM = hd.ITEM_NUM "
														+ "JOIN MEMBER m ON m.MEMBER_ID = r.MEMBER_ID "
														+ "JOIN IMAGE i ON i.TEA_REVIEW_NUM = r.REVIEW_NUM "
														+ "WHERE rh.REVIEW_HASHTAG_CONTENT = ? "
														+ "AND i.IMAGE_DIVISION = 1";
	
	static final private String SQL_SELECTONE = "SELECT REVIEW_HASHTAG_NUM, REVIEW_HASHTAG_CONTENT FROM REVIEW_HASHTAG WHERE REVIEW_HASHTAG_CONTENT = ? ";
	
	
	static final private String SQL_INSERT = "INSERT INTO REVIEW_HASHTAG(REVIEW_HASHTAG_NUM, REVIEW_HASHTAG_CONTENT) "
											+ "VALUES ((SELECT NVL(MAX(REVIEW_HASHTAG_NUM), 2000) + 1 FROM REVIEW_HASHTAG), ?)";

	public List<ReviewHashtagVO> selectAll(ReviewHashtagVO reviewHashtagVO) {
		
		if (reviewHashtagVO.getHashTagSearchCondition().equals("후기번호검색")) {
			Object[] args = { reviewHashtagVO.getItemNum() };
			return jdbcTemplate.query(SQL_SELECTALL, args, new ReviewHashtagSelect());
		} 
		else {
			Object[] args = { reviewHashtagVO.getReviewHashtagContent() };
			return jdbcTemplate.query(SQL_SELECTALL_SEARCH, args, new ReviewHashtagSearch());
		}
	}

	public ReviewHashtagVO selectOne(ReviewHashtagVO reviewHashtagVO) {
		try {		
			Object[] args = {reviewHashtagVO.getReviewHashtagContent()};
			return jdbcTemplate.queryForObject(SQL_SELECTONE, args, new ReviewHashtagSelect());	
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public boolean insert(ReviewHashtagVO reviewHashtagVO) {
		int result = jdbcTemplate.update(SQL_INSERT, reviewHashtagVO.getReviewHashtagContent());

		if(result <= 0) {
			return false;
		}
		return true;
	}

	public boolean update(ReviewHashtagVO reviewHashtagVO) {
		return false;
	}

	public boolean delete(ReviewHashtagVO reviewHashtagVO) {
		return false;
	}


}

//----------------------------------------------------------------------------------------------------

// selectAll
class ReviewHashtagSelect implements RowMapper<ReviewHashtagVO> {

	@Override
	public ReviewHashtagVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ReviewHashtagVO data = new ReviewHashtagVO();
		data.setReviewHashtagNum(rs.getInt("REVIEW_HASHTAG_NUM"));
		data.setReviewHashtagContent(rs.getString("REVIEW_HASHTAG_CONTENT"));
		return data;
		
	}
	
}


// Search
class ReviewHashtagSearch implements RowMapper<ReviewHashtagVO> {

	@Override
	public ReviewHashtagVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ReviewHashtagVO data = new ReviewHashtagVO();
		data.setReviewNum(rs.getInt("REVIEW_NUM"));
		data.setBuySerial(rs.getInt("BUY_SERIAL"));
		data.setMemberName(rs.getString("MEMBER_NAME"));
		data.setReviewContent(rs.getString("REVEIW_CONTENT"));
		data.setReviewInsertTime(rs.getDate("REVIEW_INSERT_TIME"));
		data.setReviewHashtagContent(rs.getString("REVIEW_HASHTAG_CONTENT"));
		data.setImageUrl(rs.getString("IMAGE_URL"));
		return data;
	}
	
}






package com.example.prj1be20231109.mapper;

import com.example.prj1be20231109.domain.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper {


    @Delete("""
     DELETE FROM boardLike
     WHERE boardId= #{boardId}       
     AND memberId=#{memberId}
            """)
     int delete(Like like);


    @Insert("""
            INSERT INTO boardLike ( boardId,memberId)
            VALUES (#{boardId}, #{memberId})
            """)
    public void insert(Like like);
}

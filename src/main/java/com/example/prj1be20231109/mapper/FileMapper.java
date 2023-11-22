package com.example.prj1be20231109.mapper;

import com.example.prj1be20231109.domain.BoardFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper {

    @Insert("""
            INSERT INTO boardFile(boardId,name)
            VALUES(#{boardId},#{name})
            """)
    int insert(Integer boardId, String name);


    @Select("""

            SELECT name,id
            FROm boardfile
            WHERE boardId=#{boardId}
            """)
    List<BoardFile> selectNamesByBoardId(Integer boardId);


    @Delete("""
            DELETE FROM boardfile
            WHERE boardId=#{boardId}
            """)
    int deleteByBoardId(Integer boardId);


    @Select("""
            SELECT * FROM boardfile
            WHERE id = #{id}
            """)
    BoardFile selectById(Integer id);

    @Delete("""
DELETE FROM boardfile
WHERE id = #{id}
""")
    int deleteById(Integer id);
}

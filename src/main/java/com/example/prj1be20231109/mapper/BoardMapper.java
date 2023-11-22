package com.example.prj1be20231109.mapper;

import com.example.prj1be20231109.domain.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("""
        INSERT INTO board (title, content, writer)
        VALUES (#{title}, #{content}, #{writer})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Board board);

    @Select("""
        <script>
        SELECT b.id,
               b.title,
               b.writer,
               m.nickName,
               b.inserted,
               COUNT(DISTINCT c.id) countComment,
               COUNT(DISTINCT l.id) countLike,
               COUNT(DISTINCT f.id) countFile
        FROM board b JOIN member m ON b.writer = m.id
                     LEFT JOIN comment c ON b.id = c.boardId
                     LEFT JOIN boardLike l ON b.id = l.boardId
                     LEFT JOIN boardFile f ON b.id = f.boardId
        WHERE 
            <trim prefixOverrides="OR">
                <if test="category == 'all' or category == 'title'">
                    OR title LIKE #{keyword}
                </if>
                <if test="category == 'all' or category == 'content'">
                    OR content LIKE #{keyword}
                </if>
            </trim>
        GROUP BY b.id
        ORDER BY b.id DESC
        LIMIT #{from}, 10
        </script>
        """)
    List<Board> selectAll(Integer from, String keyword, String category);

    @Select("""
        SELECT b.id,
               b.title, 
               b.content, 
               b.writer, 
               m.nickName,
               b.inserted
        FROM board b JOIN member m ON b.writer = m.id
        WHERE b.id = #{id}
        """)
    Board selectById(Integer id);

    @Delete("""
        DELETE FROM board
        WHERE id = #{id}
        """)
    int deleteById(Integer id);

    @Update("""
        UPDATE board
        SET title = #{title},
            content = #{content}
        WHERE id = #{id}
        """)
    int update(Board board);


    @Delete("""
        DELETE FROM board
        WHERE writer = #{writer}
        """)

    int deleteByWriter(String writer);

    @Select("""
        SELECT id
        FROM board
        WHERE writer = #{id}
        """)
    List<Integer> selectIdListByMemberId(String writer);

    @Select("""
        <script>
        SELECT COUNT(*) FROM board
        WHERE 
            <trim prefixOverrides="OR">
                <if test="category == 'all' or category == 'title'">
                    OR title LIKE #{keyword}
                </if>
                <if test="category == 'all' or category == 'content'">
                    OR content LIKE #{keyword}
                </if>
            </trim>
        </script>
        """)
    int countAll(String keyword, String category);
}

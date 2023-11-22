package com.example.prj1be20231109.service;

import com.example.prj1be20231109.domain.Board;
import com.example.prj1be20231109.domain.BoardFile;
import com.example.prj1be20231109.domain.Member;
import com.example.prj1be20231109.mapper.BoardMapper;
import com.example.prj1be20231109.mapper.CommentMapper;
import com.example.prj1be20231109.mapper.FileMapper;
import com.example.prj1be20231109.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class BoardService {

    private final BoardMapper mapper;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;
    private final FileMapper fileMapper;

    private final S3Client s3;

    @Value("${image.file.prefix}")
    private String urlPrefix;
    @Value("${aws.s3.bucket.name}")
    private String bucket;

    public boolean save(Board board, MultipartFile[] files, Member login) throws IOException {
        //
        board.setWriter(login.getId());

        int cnt = mapper.insert(board);

        // boardFile 테이블에 files 정보 저장
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                // boardId, name
                fileMapper.insert(board.getId(), files[i].getOriginalFilename());

                // 실제 파일을 S3 bucket에 upload
                // 일단 local에 저장
                upload(board.getId(), files[i]);
            }
        }


        return cnt == 1;
    }

    private void upload(Integer boardId, MultipartFile file) throws IOException {

        String key = "prj1/" + boardId + "/" + file.getOriginalFilename();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));


    }

    public boolean validate(Board board) {
        if (board == null) {
            return false;
        }

        if (board.getContent() == null || board.getContent().isBlank()) {
            return false;
        }

        if (board.getTitle() == null || board.getTitle().isBlank()) {
            return false;
        }

        return true;
    }

    public Map<String, Object> list(Integer page, String keyword, String category) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> pageInfo = new HashMap<>();

        int countAll = mapper.countAll("%" + keyword + "%", category);
        int lastPageNumber = (countAll - 1) / 10 + 1;
        int startPageNumber = (page - 1) / 10 * 10 + 1;
        int endPageNumber = startPageNumber + 9;
        endPageNumber = Math.min(endPageNumber, lastPageNumber);
        int prevPageNumber = startPageNumber - 10;
        int nextPageNumber = endPageNumber + 1;

        pageInfo.put("currentPageNumber", page);
        pageInfo.put("startPageNumber", startPageNumber);
        pageInfo.put("endPageNumber", endPageNumber);
        if (prevPageNumber > 0) {
            pageInfo.put("prevPageNumber", prevPageNumber);
        }
        if (nextPageNumber <= lastPageNumber) {
            pageInfo.put("nextPageNumber", nextPageNumber);
        }

        int from = (page - 1) * 10;
        map.put("boardList", mapper.selectAll(from, "%" + keyword + "%", category));
        map.put("pageInfo", pageInfo);
        return map;
    }

    public Board get(Integer id) {
        Board board = mapper.selectById(id);

        List<BoardFile> boardFiles = fileMapper.selectNamesByBoardId(id);

        for (BoardFile boardFile : boardFiles) {
            String url = urlPrefix + "prj1/" + id + "/" + boardFile.getName();
            boardFile.setUrl(url);
        }

        board.setFiles(boardFiles);

        return board;
    }

    public boolean remove(Integer id) {
        // 게시물에 달린 댓글들 지우기
        commentMapper.deleteByBoardId(id);

        // 좋아요 레코드 지우기
        likeMapper.deleteByBoardId(id);

        deleteFile(id);


        return mapper.deleteById(id) == 1;
    }

    private void deleteFile(Integer id) {
        // 파일명 조회
        List<BoardFile> boardFiles = fileMapper.selectNamesByBoardId(id);

        // s3 bucket objects 지우기
        for (BoardFile file : boardFiles) {
            String key = "prj1/" + id + "/" + file.getName();

            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3.deleteObject(objectRequest);
        }

        // 첨부파일 레코드 지우기
        fileMapper.deleteByBoardId(id);
    }

    public boolean update(Board board, List<Integer> removeFileIds, MultipartFile[] uploadFiles) throws IOException {

        // 파일 지우기
        if (removeFileIds != null) {
            for (Integer id : removeFileIds) {
                // s3에서 지우기
                BoardFile file = fileMapper.selectById(id);
                String key = "prj1/" + board.getId() + "/" + file.getName();
                DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
                s3.deleteObject(objectRequest);

                // db에서 지우기
                fileMapper.deleteById(id);
            }
        }

        // 파일 추가하기
        if (uploadFiles != null) {
            for (MultipartFile file : uploadFiles) {
                // s3에 올리기
                upload(board.getId(), file);
                // db에 추가하기
                fileMapper.insert(board.getId(), file.getOriginalFilename());
            }
        }

        return mapper.update(board) == 1;
    }

    public boolean hasAccess(Integer id, Member login) {
        if (login == null) {
            return false;
        }

        if (login.isAdmin()) {
            return true;
        }

        Board board = mapper.selectById(id);

        return board.getWriter().equals(login.getId());
    }


}

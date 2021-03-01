package com.lin.mapper;

import com.lin.pojo.Comments;
import com.lin.pojo.VO.CommentVO;
import com.lin.utils.MyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsMapperCustom extends MyMapper<Comments> {

    public List<CommentVO> queryComments(String videoId);
}

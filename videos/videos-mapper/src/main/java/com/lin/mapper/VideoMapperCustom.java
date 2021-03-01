package com.lin.mapper;

import com.lin.pojo.VO.VideoVO;
import com.lin.pojo.Videos;
import com.lin.utils.PagedResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoMapperCustom {

    public List<VideoVO> queryAllVideos(@Param("videoDesc")String videoDesc,
                                        @Param("userId")String userId);

    /**
     * 对视频喜欢的数量进行累加
     */
    public void addVideoLikeCount(String videoId);

    /**
     * 对视频喜欢的数量进行累减
     */
    public void reduceVideoLikeCount(String videoId);


    /**
     * 查询点赞视频
     * @param userId
     * @return
     */
    public List<VideoVO> queryMyLikeVideos(@Param("userId")String userId);

    /**
     * 查询关注的视频
     * @param userId
     * @return
     */
    public List<VideoVO> queryMyFollowVideos(@Param("userId")String userId);
}

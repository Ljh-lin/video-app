package com.lin.service;

import com.lin.pojo.Comments;
import com.lin.pojo.Videos;
import com.lin.utils.PagedResult;

import java.util.List;

public interface VideoService {

    /**
     * 保存视频
     * @param videos
     */
    public String saveVideo(Videos videos);

    /**
     * 保存视频封面
     * @param videoId
     * @param coverPath
     */
    public void updateVideo(String videoId,String coverPath);

    /**
     * 分页获取视频及用户信息
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult getAllVideos(Videos video,Integer isSaveRecord,Integer page,Integer pageSize);

    /**
     * 获取点赞视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyLikeVideos(String userId,Integer page,Integer pageSize);

    /**
     * 查询关注的人的视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyFollowVideos(String userId,Integer page,Integer pageSize);

    /**
     * 获取热搜词列表
     * @return
     */
    public List<String> getHotwords();

    /**
     * 用户喜欢的视频/点赞视频
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    public void userLikeVideo(String userId,String videoId,String videoCreaterId);

    /**
     * 用户不喜欢的视频/取消点赞视频
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    public void userUnLikeVideo(String userId,String videoId,String videoCreaterId);

    /**
     * 保存留言信息
     * @param comments
     */
    public void saveComment(Comments comments);

    public PagedResult getAllComments(String videoId,Integer page,Integer pageSize);
}

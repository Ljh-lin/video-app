package com.lin.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface UsersMapperCustom {

    /**
     * 用户受喜欢数量累加
     */
    public void addReceiveLikeCount(String userId);

    /**
     * 用户受喜欢数量累减
     */
    public void reduceReceiveLikeCount(String userId);

    /**
     * 增加粉丝数
     * @param userId
     */
    public void adFansCount(String userId);

    /**
     * 增加关注数
     * @param userId
     */
    public void addFollersCount(String userId);

    /**
     * 减少粉丝数
     * @param userId
     */
    public void reduceFansCount(String userId);

    /**
     * 减少关注数
     * @param userId
     */
    public void reduceFollersCount(String userId);
}

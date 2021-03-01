package com.lin.service.Impl;

import com.lin.mapper.*;
import com.lin.pojo.Users;
import com.lin.pojo.UsersFans;
import com.lin.pojo.UsersLikeVideos;
import com.lin.pojo.UsersReport;
import com.lin.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Autowired
    private UsersReportMapper usersReportMapper;

    //全局自增主键
    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user=new Users();
        user.setUsername(username);

        Users result=usersMapper.selectOne(user);

        return result==null ? false:true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {

        String userId=sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {

        Example userExample=new Example(Users.class);
        Criteria criteria=userExample.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result=usersMapper.selectOneByExample(userExample);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInfo(Users user) {
        Example userExample=new Example(Users.class);
        Criteria criteria=userExample.createCriteria();
        criteria.andEqualTo("id",user.getId());
        usersMapper.updateByExampleSelective(user,userExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Example userExample=new Example(Users.class);
        Criteria criteria=userExample.createCriteria();
        criteria.andEqualTo("id",userId);
        Users user=usersMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {

        if (StringUtils.isBlank(userId)||StringUtils.isBlank(videoId)){
            return false;
        }
        Example example=new Example(UsersLikeVideos.class);
        Criteria criteria=example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);

        List<UsersLikeVideos> list=usersLikeVideosMapper.selectByExample(example);
        if (list!=null&&list.size()>0){
            return true;
        }
        return false;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        String relId=sid.nextShort();
        UsersFans usersFans=new UsersFans();
        usersFans.setId(relId);
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.insert(usersFans);

        usersMapperCustom.adFansCount(userId);
        usersMapperCustom.addFollersCount(fanId);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {
        Example example=new Example(UsersFans.class);
        Criteria criteria=example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);

        usersFansMapper.deleteByExample(example);

        usersMapperCustom.reduceFansCount(userId);
        usersMapperCustom.reduceFollersCount(fanId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryIfFollow(String userId, String fanId) {

        Example example=new Example(UsersFans.class);
        Criteria criteria=example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        List<UsersFans> list = usersFansMapper.selectByExample(example);
        if (list!=null&&!list.isEmpty()&&list.size()>0){
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport usersReport) {
        String urId=sid.nextShort();
        usersReport.setId(urId);
        usersReport.setCreateDate(new Date());

        usersReportMapper.insert(usersReport);
    }
}

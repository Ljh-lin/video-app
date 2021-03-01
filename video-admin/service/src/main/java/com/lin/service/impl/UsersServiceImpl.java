package com.lin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lin.mapper.UsersMapper;
import com.lin.pojo.Users;
import com.lin.service.UsersService;
import com.lin.utils.PagedResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersMapper;


    public PagedResult queryUsers(Users users, Integer page, Integer pageSize) {
        String username="";
        String nickname="";
        if (users!=null){
            username=users.getUsername();
            nickname=users.getNickname();
        }

        PageHelper.startPage(page,pageSize);
        Example usersexample=new Example(Users.class);
        Criteria criteria=usersexample.createCriteria();
        if (StringUtils.isNotBlank(username)){
            criteria.andLike("username","%"+username+"%");
        }
        if (StringUtils.isNotBlank(nickname)){
            criteria.andLike("nickname","%"+nickname+"%");
        }

        List<Users> usersList=usersMapper.selectByExample(usersexample);
        PageInfo<Users> pageList=new PageInfo<Users>(usersList);

        PagedResult grid=new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(usersList);
        grid.setRecords(pageList.getTotal());
        grid.setPage(page);
        return grid;
    }
}

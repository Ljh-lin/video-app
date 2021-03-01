package com.lin.service;

import com.lin.pojo.Users;
import com.lin.utils.PagedResult;

public interface UsersService {
    public PagedResult queryUsers(Users users,Integer page,Integer pageSize);
}

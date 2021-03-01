package com.lin.service;

import com.lin.pojo.Bgm;

import java.util.List;

public interface BgmService {

    /**
     * 查询背景音乐列表
     * @return
     */
    public List<Bgm> queryBgmList();

    public Bgm queryBgmById(String bgmId);
}

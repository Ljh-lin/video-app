package com.lin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lin.enums.BGMOperatorTypeEnum;
import com.lin.mapper.BgmMapper;
import com.lin.mapper.UsersReportMapper;
import com.lin.mapper.UsersReportMapperCustom;
import com.lin.mapper.VideosMapper;
import com.lin.pojo.Bgm;
import com.lin.pojo.UsersReport;
import com.lin.pojo.Videos;
import com.lin.pojo.vo.Reports;
import com.lin.service.VideoService;
import com.lin.util.ZKCurator;
import com.lin.utils.JsonUtils;
import com.lin.utils.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ZKCurator zkCurator;

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private UsersReportMapperCustom usersReportMapperCustom;

    @Transactional(propagation = Propagation.REQUIRED)
    public void addBgm(Bgm bgm) {
        String bgmId=sid.nextShort();
        bgm.setId(bgmId);
        bgmMapper.insert(bgm);

        Map<String,String> map=new HashMap<String, String>();
        map.put("operType",BGMOperatorTypeEnum.ADD.type);
        map.put("path",bgm.getPath());
        zkCurator.sendBgmOperator(bgmId, JsonUtils.objectToJson(map));

    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryBgmList(Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<Bgm> list = bgmMapper.selectAll();

        PageInfo<Bgm> pageList=new PageInfo<Bgm>(list);
        PagedResult result=new PagedResult();
        result.setTotal(pageList.getPages());
        result.setRows(list);
        result.setPage(page);
        result.setRecords(pageList.getTotal());
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBgm(String id) {
        Bgm bgm=bgmMapper.selectByPrimaryKey(id);
        bgmMapper.deleteByPrimaryKey(id);
        Map<String,String>map=new HashMap<String, String>();
        map.put("operType",BGMOperatorTypeEnum.DELETE.type);
        map.put("path",bgm.getPath());
        zkCurator.sendBgmOperator(id,JsonUtils.objectToJson(map));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryReportList(Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<Reports> reportList=usersReportMapperCustom.selectAllVideoRepotr();
        PageInfo<Reports> pageList=new PageInfo<Reports>(reportList);

        PagedResult grid=new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(reportList);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());

        return grid;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVideoStatus(String videoId, Integer status) {

        Videos video=new Videos();
        video.setId(videoId);
        video.setStatus(status);
        videosMapper.updateByPrimaryKeySelective(video);


    }
}

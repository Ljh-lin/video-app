package com.lin.mapper;

import com.lin.pojo.UsersReport;
import com.lin.pojo.vo.Reports;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersReportMapperCustom {
    List<Reports> selectAllVideoRepotr();
}

package com.lin.mapper;


import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRecordsMapperCustom {

    public List<String> getHotwords();
}

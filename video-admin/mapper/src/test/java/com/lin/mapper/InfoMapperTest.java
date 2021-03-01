package com.lin.mapper;

import com.lin.pojo.Info;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.swing.*;
import java.util.List;


/**
 * Create by linjh on
 */

@SpringBootTest
public class InfoMapperTest {

    @Resource
    private InfoMapper mapper;
    @Test
    public void test1(){
        List<Info> infos = mapper.searchByNo();
        System.out.println(infos);
    }

}
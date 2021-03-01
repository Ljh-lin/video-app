package com.lin;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    /**
     * swagger2的配置文件，这里配置swagger2的一些基本信息，比如扫描的包
     * @return
     */
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.lin.controller"))
                .paths(PathSelectors.any()).build();
    }

    /**
     * 构建api文档信息
     * @return
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                //设置页面标题
                .title("使用swagger2构建短视频后端api接口文档")
                //设置联系人
                .contact(new Contact("小罗","http:xiaoluo.com","girlfirend@163.com"))
                //描述
                .description("欢迎访问短视频接口文档")
                //定义版本号
                .version("1.0").build();


    }
}

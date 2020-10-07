package com.codebroker.web.api.config;

import com.google.common.base.Predicates;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket swaggerSpringMvcPlugin(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //选择那些路径和api会生成document
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                //对所有api进行监控
                .paths(PathSelectors.any())
                //错误路径不监控
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("机器人接口列表")
                .description("基于swagger生成接口文档,可在线对接口进行调试！")
//                .contact(new Contact("这是作者", "这是网址地址", "这是邮件地址"))//作者
                .version("1.0")
                .build();

    }

}
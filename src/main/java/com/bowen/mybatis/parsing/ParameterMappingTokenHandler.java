package com.bowen.mybatis.parsing;

import com.bowen.mybatis.entity.MappedStatement;

import java.util.List;

//参数映射记号处理器，静态内部类
public  class ParameterMappingTokenHandler  implements TokenHandler {


  private MappedStatement mappedStatement;

  public ParameterMappingTokenHandler(MappedStatement mappedStatement) {
    this.mappedStatement = mappedStatement;
  }

  @Override
  public String handleToken(String content) {
    List<String> parameters = mappedStatement.getParameters();
    parameters.add(content);
    //如何替换很简单，永远是一个问号，但是参数的信息要记录在parameterMappings里面供后续使用
    return "?";
  }
}
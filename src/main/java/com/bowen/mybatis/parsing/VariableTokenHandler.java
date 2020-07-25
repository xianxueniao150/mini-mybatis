package com.bowen.mybatis.parsing;

import java.util.Map;

public  class VariableTokenHandler implements TokenHandler {
    private Map<Object, Object> variables ;

    public VariableTokenHandler(Map<Object, Object> variables) {
      this.variables = variables;
    }


    @Override
    public String handleToken(String content) {
      return String.valueOf(variables.get(content));
    }
  }
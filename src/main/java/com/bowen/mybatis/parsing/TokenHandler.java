package com.bowen.mybatis.parsing;
/**
 * 记号处理器
 * 
 */
public interface TokenHandler {
	//处理记号
  String handleToken(String content);
}
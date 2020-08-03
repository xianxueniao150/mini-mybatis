/*
 *    Copyright 2009-2014 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.bowen.mybatis.executor;

/**
 * @author Clinton Begin
 */

import com.bowen.mybatis.entity.MappedStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * 执行器
 * 
 */
public interface Executor {
  public  <E> List<E> selectList(MappedStatement mappedStatement, Object[] params) throws SQLException, ClassNotFoundException,
          IllegalAccessException, InstantiationException ;

  public <T> T  selectOne(MappedStatement ms, Object[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException ;

  public Object update(MappedStatement mappedStatement, Object[] params) throws SQLException;
}

/**
 * 
 */
package test.dao;

import test.bean.User;

import java.util.List;
import java.util.Map;

public interface UserMapper{

    /**
     * 获取单个user
     * 
     * @param id
     * @return 
     * @see 
     */
    User getUser(Integer id);

    User getUserByMap(Map<String , Object> map);
    
    /**
     * 获取所有用户
     * 
     * @return 
     * @see 
     */
    List<User> getAll();
    
    /**
     * 更新用户（功能未完成）
     * 
     * @param id 
     */
    Integer updateUser(String id);
}

package test;

import com.bowen.mybatis.ConfigFilesLoad;
import com.bowen.mybatis.MapperProxy;
import test.bean.User;
import test.dao.UserMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */


public class TestMain{

    /**
     * mainTEST
     *
     * @param args
     */
    public static void main(String[] args){
        ConfigFilesLoad.loadFile("conf.properties");

        UserMapper userMapper = new MapperProxy<>(UserMapper.class).getProxy();
//        List<User> all = userMapper.getAll();
//        System.out.println(all);
//        User user = userMapper.getUser(1);
//        System.out.println(user);
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三2");
        map.put("id",1);
        map.put("condition","1=1");
        User userByMap = userMapper.getUserByMap(map);
        System.out.println(userByMap);


        /*SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("conf.properties");
        SqlSession session = factory.openSession();

        UserMapper userMapper = session.getMapper(UserMapper.class);
        User user = userMapper.getUser("1");
        System.out.println("******* " + user);
        System.out.println("*******all " + userMapper.getAll());
        
        userMapper.updateUser("1");
        System.out.println("*******all " + userMapper.getAll());*/
    }

}

package test;

import com.alibaba.fastjson.JSONObject;
import com.bowen.mybatis.session.DefaultSqlSession;
import com.bowen.mybatis.session.DefaultSqlSessionFactory;
import test.bean.User;
import test.dao.UserMapper;

import java.util.List;

/**
 * 
 */


public class TestMain{

    /**
     * mainTEST
     *
     *
     * @param args
     */
    public static void main(String[] args){

        DefaultSqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory();
        sqlSessionFactory.loadConfig("MybatisConfig.xml");
        DefaultSqlSession session = sqlSessionFactory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("favor",5);
        User user = new User(17, jsonObject);
//        Integer integer = userMapper.addUser(user);
//        System.out.println(integer);

        List<User> all = userMapper.getAll(user);
        System.out.println(all);
//        User user = userMapper.getUser(1);
//        System.out.println(user);
//        Map<String,Object> map = new HashMap<>();
//        map.put("name","张三2");
//        map.put("id",1);
//        map.put("condition","1=1");
//        User userByMap = userMapper.getUserByMap(map);
//        System.out.println(userByMap);
//        Integer integer = userMapper.updateUser("1");
//        System.out.println(integer);


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

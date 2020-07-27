package test;

import com.bowen.mybatis.MapperProxy;
import com.bowen.mybatis.xml.XMLConfigBuilder;
import test.dao.UserMapper;

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
        XMLConfigBuilder.loadConfig("MybatisConfig.xml");

        UserMapper userMapper = new MapperProxy<>(UserMapper.class).getProxy();
//        List<User> all = userMapper.getAll();
//        System.out.println(all);
//        User user = userMapper.getUser(1);
//        System.out.println(user);
//        Map<String,Object> map = new HashMap<>();
//        map.put("name","张三2");
//        map.put("id",1);
//        map.put("condition","1=1");
//        User userByMap = userMapper.getUserByMap(map);
//        System.out.println(userByMap);
        Integer integer = userMapper.updateUser("1");
        System.out.println(integer);


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

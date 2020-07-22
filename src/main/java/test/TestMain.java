package test;

import com.bowen.mybatis.ConfigFilesLoad;
import com.bowen.mybatis.MapperProxy;
import test.bean.User;
import test.dao.UserMapper;

import java.util.List;

/**
 * 
 */


public class TestMain{

    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args){
        ConfigFilesLoad.loadFile("conf.properties");
        UserMapper userMapper = new MapperProxy(new UserMapper() {
            @Override
            public User getUser(String id) {
                return null;
            }

            @Override
            public List<User> getAll() {
                return null;
            }

            @Override
            public void updateUser(String id) {

            }
        }).getProxy();
//        List<User> all = userMapper.getAll();
//        System.out.println(all);
        User user = userMapper.getUser("1");
        System.out.println(user);


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

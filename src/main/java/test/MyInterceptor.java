package test;

import com.alibaba.fastjson.JSONObject;
import com.bowen.mybatis.entity.MappedStatement;
import com.bowen.mybatis.executor.Executor;
import com.bowen.mybatis.plugin.*;
import lombok.extern.slf4j.Slf4j;
import test.bean.User;

/**
 * @author: zhaobowen
 * @create: 2020-07-30 18:25
 * @description:
 **/
@Intercepts(
        @Signature(method = "selectList",
                type = Executor.class,
                args = {MappedStatement.class, Object[].class}
        )
)
@Slf4j
public class MyInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 根据签名指定的args顺序获取具体的实现类
        // 1. 获取MappedStatement实例, 并获取当前SQL命令类型
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        log.debug("ms: {}",ms);

        // 2. 获取当前正在被操作的类, 有可能是Java Bean, 也可能是普通的操作对象, 比如普通的参数传递
        // 普通参数, 即是 @Param 包装或者原始 Map 对象, 普通参数会被 Mybatis 包装成 Map 对象
        // 即是 org.apache.ibatis.binding.MapperMethod$ParamMap
        Object[] parameter = (Object[])invocation.getArgs()[1];
        if(parameter[0] instanceof User){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("favor",5);
            parameter[0] = new User(16, jsonObject);
            log.debug("parameter:{}",parameter);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


}

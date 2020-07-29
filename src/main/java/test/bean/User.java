package test.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User{
    /** the id */
    private Integer id;

    /** the name */
    private JSONObject name;

}

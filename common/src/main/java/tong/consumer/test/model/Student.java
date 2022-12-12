package tong.consumer.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author tong-exists
 * @Create 2022/12/12 10:33
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {

    private String name;
    private Integer age;
    private Double score;
    private String[] likes;
    private List<Student> friends;
    private Map<String, String> scoreMap;

}

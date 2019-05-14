package cn.fzz.elasticsearch.service;

import cn.fzz.elasticsearch.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    Long count();
    List<User> listByUser(User user);
}

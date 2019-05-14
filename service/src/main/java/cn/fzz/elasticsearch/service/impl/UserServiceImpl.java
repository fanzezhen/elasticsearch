package cn.fzz.elasticsearch.service.impl;

import cn.fzz.common.utils.JestUtils;
import cn.fzz.elasticsearch.model.User;
import cn.fzz.elasticsearch.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
//    @Autowired
//    private UserRepository userRepository;
    private static JestUtils jestUtils = new JestUtils("db", "user");

    @Override
    public Long count() {
        return null;
    }

    @Override
    public List<User> listByUser(User user) {
        return null;
    }
}

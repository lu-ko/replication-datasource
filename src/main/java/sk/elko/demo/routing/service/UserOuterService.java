package sk.elko.demo.routing.service;

import sk.elko.demo.routing.entity.User;
import sk.elko.demo.routing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserOuterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInnerService userInnerService;

    @Transactional(readOnly = true)
    public User findByIdRead(Integer id) {
        return userRepository.findOne(id);
    }

    @Transactional(readOnly = false)
    public User findByIdWrite(Integer id) {
        return userRepository.findOne(id);
    }

    @Transactional(readOnly = false)
    public void save(User user) throws Exception {
        userRepository.save(user);
    }

    @Transactional(readOnly = false)
    public Map<String, User> findByIdWriteAndInnerReadWithPropagationRequired(Integer outerFirstId, Integer innerId, Integer outerSecondId) {
        Map<String, User> users = new HashMap<String, User>();
        users.put("outerFirstUser", userRepository.findOne(outerFirstId));
        users.put("innerUser", userInnerService.findByUserIdWithPropagationRequired(innerId));
        users.put("outerSecondUser", userRepository.findOne(outerSecondId));
        return users;
    }

    @Transactional(readOnly = false)
    public Map<String, User> findByIdWriteAndInnerReadWithPropagationRequiresNew(Integer outerFirstId, Integer innerId, Integer outerSecondId) {
        Map<String, User> users = new HashMap<String, User>();
        users.put("outerFirstUser", userRepository.findOne(outerFirstId));
        users.put("innerUser", userInnerService.findByUserIdWithPropagationRequiresNew(innerId));
        users.put("outerSecondUser", userRepository.findOne(outerSecondId));
        return users;
    }

    @Transactional(readOnly = false)
    public Map<String, User> findByIdWriteAndInnerReadWithPoropagationMandatory(Integer outerFirstId, Integer innerId, Integer outerSecondId) {
        Map<String, User> users = new HashMap<String, User>();
        users.put("outerFirstUser", userRepository.findOne(outerFirstId));
        users.put("innerUser", userInnerService.findByUserIdWithPropagationMandatory(innerId));
        users.put("outerSecondUser", userRepository.findOne(outerSecondId));
        return users;
    }

}

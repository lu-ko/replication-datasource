package sk.elko.demo.routing.service;

import sk.elko.demo.routing.entity.User;
import sk.elko.demo.routing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInnerService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public User findByUserIdWithPropagationRequired(Integer id) {
        return userRepository.findOne(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public User findByUserIdWithPropagationRequiresNew(Integer id) {
        return userRepository.findOne(id);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public User findByUserIdWithPropagationMandatory(Integer id) {
        return userRepository.findOne(id);
    }

}

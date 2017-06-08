package sk.elko.demo.routing.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import sk.elko.demo.routing.configuration.DatabaseConfiguration;
import sk.elko.demo.routing.configuration.SpringReplicationRoutingDataSourceConfiguration;
import sk.elko.demo.routing.entity.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DatabaseConfiguration.class, SpringReplicationRoutingDataSourceConfiguration.class})
@DirtiesContext
public class UserRepositoryTest {

    private Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional(readOnly = true)
    public void findByName() throws Exception {
        Optional<User> result1 = userRepository.findByName("read_5");
        log.info("findByName 1: {}", result1);
        assertThat(result1.isPresent()).as("user not found").isFalse();

        Optional<User> result2 = userRepository.findByName("read_1");
        log.info("findByName 2: {}", result2);
        assertThat(result2.isPresent()).as("user found").isTrue();
        assertThat(result2.get().getName()).as("user found and name equals").isEqualTo("read_1");
    }

    @Test
    @Transactional(readOnly = true)
    public void findAllByOrderByNameAsc() throws Exception {
        List<User> result = userRepository.findAllByOrderByNameAsc();
        log.info("findAllByOrderByNameAsc : {}", result);
        assertThat(result.isEmpty()).as("users found").isFalse();
        assertThat(result.size()).as("users count").isEqualTo(4);
    }

    @Test
    @Transactional(readOnly = true)
    public void findByNameContainingIgnoreCaseOrderByNameDesc() throws Exception {
        List<User> result1 = userRepository.findByNameContainingIgnoreCaseOrderByNameDesc("REad_5");
        log.info("findByNameContainingIgnoreCaseOrderByNameDesc 1: {}", result1);
        assertThat(result1.isEmpty()).as("users not found").isTrue();

        List<User> result2 = userRepository.findByNameContainingIgnoreCaseOrderByNameDesc("rEAd");
        log.info("findByNameContainingIgnoreCaseOrderByNameDesc 2: {}", result2);
        assertThat(result2.isEmpty()).as("users found").isFalse();
        assertThat(result2.size()).as("users count").isEqualTo(4);
        assertThat(result2.get(0).getName()).as("users are in desc order 0").isEqualTo("read_4");
        assertThat(result2.get(1).getName()).as("users are in desc order 1").isEqualTo("read_3");
        assertThat(result2.get(2).getName()).as("users are in desc order 2").isEqualTo("read_2");
        assertThat(result2.get(3).getName()).as("users are in desc order 3").isEqualTo("read_1");
    }

    @Test
    @Transactional(readOnly = true)
    public void queryByNameContainingIgnoreCaseOrderByNameDesc() throws Exception {
        List<User> result1 = userRepository.queryByNameContainingIgnoreCaseOrderByNameDesc("REad_5");
        log.info("queryByNameContainingIgnoreCaseOrderByNameDesc 1: {}", result1);
        assertThat(result1.isEmpty()).as("users not found").isTrue();

        List<User> result2 = userRepository.queryByNameContainingIgnoreCaseOrderByNameDesc("rEAd");
        log.info("queryByNameContainingIgnoreCaseOrderByNameDesc 2: {}", result2);
        assertThat(result2.isEmpty()).as("users found").isFalse();
        assertThat(result2.size()).as("users count").isEqualTo(4);
        assertThat(result2.get(0).getName()).as("users are in desc order 0").isEqualTo("read_4");
        assertThat(result2.get(1).getName()).as("users are in desc order 1").isEqualTo("read_3");
        assertThat(result2.get(2).getName()).as("users are in desc order 2").isEqualTo("read_2");
        assertThat(result2.get(3).getName()).as("users are in desc order 3").isEqualTo("read_1");
    }

    @Test
    @Transactional(readOnly = true)
    public void findByNameLike() throws Exception {
        Page<User> result1 = userRepository.findByNameLike("read", new PageRequest(0, 2));
        log.info("findByNameLike 1: {}", result1);
        assertThat(result1.getTotalPages()).as("users not found").isEqualTo(0);
        assertThat(result1.getTotalElements()).as("users not found").isEqualTo(0);

        Page<User> result2 = userRepository.findByNameLike("read%", new PageRequest(0, 2, new Sort(new Order(Direction.DESC, "name"))));
        log.info("findByNameLike 2: {}", result2);
        assertThat(result2.getTotalPages()).as("users found - all pages").isEqualTo(2);
        assertThat(result2.getTotalElements()).as("users found - all elements").isEqualTo(4);
        assertThat(result2.getContent().size()).as("users count on selected page").isEqualTo(2);
        assertThat(result2.getContent().get(0).getName()).as("users are in desc order 0").isEqualTo("read_4");
        assertThat(result2.getContent().get(1).getName()).as("users are in desc order 1").isEqualTo("read_3");
    }

    @Test
    @Transactional(readOnly = true)
    public void queryByNameLike() throws Exception {
        Page<User> result1 = userRepository.queryByNameLike("read", new PageRequest(0, 2));
        log.info("queryByNameLike 1: {}", result1);
        assertThat(result1.getTotalPages()).as("users not found").isEqualTo(0);
        assertThat(result1.getTotalElements()).as("users not found").isEqualTo(0);

        Page<User> result2 = userRepository.queryByNameLike("read%", new PageRequest(0, 2, new Sort(new Order(Direction.DESC, "name"))));
        log.info("queryByNameLike 2: {}", result2);
        assertThat(result2.getTotalPages()).as("users found - all pages").isEqualTo(2);
        assertThat(result2.getTotalElements()).as("users found - all elements").isEqualTo(4);
        assertThat(result2.getContent().size()).as("users count on selected page").isEqualTo(2);
        assertThat(result2.getContent().get(0).getName()).as("users are in desc order 0").isEqualTo("read_4");
        assertThat(result2.getContent().get(1).getName()).as("users are in desc order 1").isEqualTo("read_3");
    }

}

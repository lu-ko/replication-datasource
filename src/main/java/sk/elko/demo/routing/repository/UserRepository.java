package sk.elko.demo.routing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.elko.demo.routing.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Example: Simple select query by where clause with optional result
     */
    Optional<User> findByName(String name);

    /**
     * Example: Multiple results and ordering
     */
    List<User> findAllByOrderByNameAsc();

    /**
     * Example: Multiple results and where clause
     */
    List<User> findByNameContainingIgnoreCaseOrderByNameDesc(String name);

    /**
     * Example: Previous example as query
     */
    @Query("SELECT u FROM User u WHERE lower(u.name) like lower(concat('%', :name, '%')) ORDER BY u.name DESC")
    List<User> queryByNameContainingIgnoreCaseOrderByNameDesc(@Param("name") String name);

    /**
     * Example: Pagination with/without ordering
     */
    Page<User> findByNameLike(String name, Pageable pageable);

    /**
     * Example: Previous example as query
     */
    @Query(value = "SELECT u.* FROM users u WHERE u.name like :name \n-- #pageable\n",
            countQuery = "SELECT count(1) FROM users WHERE name like :name",
            nativeQuery = true)
    Page<User> queryByNameLike(@Param("name") String name, Pageable pageable);

}

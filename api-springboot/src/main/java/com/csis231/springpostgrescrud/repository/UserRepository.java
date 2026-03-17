package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
            select u
            from User u
            where lower(u.username) like lower(concat('%', :q, '%'))
               or lower(u.email) like lower(concat('%', :q, '%'))
            """)
    Page<User> dynamicSearch(@Param("q") String q, Pageable pageable);
}

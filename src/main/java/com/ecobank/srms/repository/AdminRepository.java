package com.ecobank.srms.repository;

import com.ecobank.srms.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional
public interface AdminRepository extends JpaRepository <Admin,Long>{
    @Override
    Optional<Admin> findById(Long aLong);

    Optional<Admin> findByUsername(String username);

}

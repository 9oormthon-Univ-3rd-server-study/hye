package com.example.goorm.repository;


import com.example.goorm.domain.Coupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon,Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT c FROM Coupon c WHERE c.id =:id")
    Optional<Coupon> findByIdWithLock(Long id);
}

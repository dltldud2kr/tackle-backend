package com.example.tackle.payment.repository;

import com.example.tackle.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByIdx(String idx);

    Payment findByImpUid(String impUid);
}

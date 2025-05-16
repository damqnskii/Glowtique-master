package com.glowtique.glowtique.voucher.repository;

import com.glowtique.glowtique.voucher.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.glowtique.glowtique.user.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, UUID> {

    Optional<Voucher> getVoucherByName(String name);
    Optional<Voucher> getVoucherByNameAndUserId(String voucherName, UUID userId);
    @Query("SELECT v FROM Voucher v WHERE v.user = :user ORDER BY v.usedAt DESC LIMIT 1")
    Optional<Voucher> getVoucherByUserAndUsedAfterAt(User user);
}

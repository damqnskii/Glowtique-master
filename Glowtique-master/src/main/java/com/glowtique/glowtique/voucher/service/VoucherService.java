package com.glowtique.glowtique.voucher.service;

import com.glowtique.glowtique.exception.VoucherNotExistingException;
import com.glowtique.glowtique.voucher.model.Voucher;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.voucher.model.VoucherType;
import com.glowtique.glowtique.voucher.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class VoucherService {
    private VoucherRepository voucherRepository;

    @Autowired
    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public Voucher getVoucher(String name) {
        return voucherRepository.getVoucherByName(name).orElseThrow(() -> new VoucherNotExistingException("Не съществува такъв код за отсъпка !"));
    }

    public Voucher getVoucherByNameAndUserId(String voucherName, UUID userId) {
        return voucherRepository.getVoucherByNameAndUserId(voucherName, userId).orElseThrow(() -> new VoucherNotExistingException("Не съществува такъв код за отсъпка !"));
    }

    public Voucher createPercentageVoucher(User user, String name, BigDecimal percent) {
        Voucher voucher = Voucher.builder()
                .user(user)
                .name(name)
                .priceDiscount(null)
                .percentageDiscount(percent)
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .usedAt(LocalDateTime.now())
                .build();

        return voucherRepository.save(voucher);
    }

    public Voucher createPriceVoucher(User user, String name, BigDecimal price) {
        Voucher voucher = Voucher.builder()
                .user(user)
                .name(name)
                .priceDiscount(price)
                .percentageDiscount(null)
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .usedAt(LocalDateTime.now())
                .build();

        return voucherRepository.save(voucher);
    }

    public Voucher getLastUsedVoucher(User user) {
        return voucherRepository.getVoucherByUserAndUsedAfterAt(user).orElseThrow(() -> new VoucherNotExistingException("Такъв ваучер не е намерен !"));
    }


}

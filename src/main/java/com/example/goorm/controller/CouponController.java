package com.example.goorm.controller;

import com.example.goorm.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class CouponController {

    private final CouponService couponService;


    @GetMapping("")
    public String getCoupon() throws Exception {
        return couponService.getCoupon();
    }

}

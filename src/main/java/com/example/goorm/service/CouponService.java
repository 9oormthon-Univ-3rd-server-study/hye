package com.example.goorm.service;

import com.example.goorm.domain.Coupon;
import com.example.goorm.repository.CouponRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReentrantLock;

/*
주어진 함수에서 의도하는 변수가 명확하지 않아 내 생각대로 가설을 세웠다.
칭찬스티커지급 메소드를 호출하는 메소드가 잘 갖추어져 있다면 문제에서 주어진 메소드에는 문제가 없다고 생각한다.
주어진 메소드는 사용자의 아이디와 쿠폰의 아이디를 묶어 스티커로 등록하는 메소드이다.
가정은 이렇다 쿠폰 테이블에는 쿠폰이 100개가 등록되어 있고 각 호출별로 1번 쿠폰부터 100번쿠폰까지 지급이 된다.
그래서 service내에 내부 sequence를 정해주고 요청이 올 때 마다 sequence를 하나씩 올려가며 그 sequence 값에 맞는 id를 가진 쿠폰을 가져온다.
처음 가설은 트랜잭션을 범위를 정해놓고 중간에 쿠폰 번호를 읽어오는 것에 비관적 락을 걸면 트랜잭션 범위내에서 락이 작동해 자동으로 모든 요청이 순서대로 이루어질것이라 생각했지만 락이 걸리기전에 동시에 들어와버린 요청들에 대해서는 동시성 제어가 불가능했다.
여기서 문제는 동시에 들어오는 순서를 어떻게 제어를 할 것인가인데 synchronized를 사용했다. 메소드에 synchronized를 걸면 쿠폰아이디를 1번부터 100번까지 순서대로 지급이 가능하다. 하지만 이 방식을 사용하면 JVM의 스케쥴링으로 들어오는 요청들이 스케쥴링이 되어 선착순으로 지급한다는 조건을 만족하지못한다.
그래서 사용한 것이 ReetrantLock으로 이것을 사용하면 메소드 내에서 이 객체의 메소드를 사용하여 원하는 지점을 락을 걸고 그 부분에 대해 들어오는 요청들이 들어오는 순서들을 보장 받으면서 요청이 수행된다.
하지만 가장 오래 기다린 요청이 선점을 가져가기 위해 계산하는 과정이 있기 때문에 성능 저하는 어쩔 수 없는 이슈이다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {
    private final CouponRepository couponRepository;

    ReentrantLock lock = new ReentrantLock(true);
    private Long seq = 1L;

    @PostConstruct
    @Transactional
    public void init(){
        for(int i = 1 ; i < 102 ; i++){
            couponRepository.save(Coupon.builder().number(i+"").build());
            log.info("쿠폰 등록 번호 : {}",i);
        }
    }

    @Transactional
    public String getCoupon() throws Exception {
        lock.lock();
        try {
            if (seq > 101) {
                throw new Exception("쿠폰 선착순 마감 현재 번호 : " + seq);
            }
            Coupon coupon = couponRepository.findById(seq).get();
            칭찬스티커지급(coupon.getId());
            seq++;
            return coupon.getNumber();
        }finally {
            lock.unlock();
        }
    }


    public void 칭찬스티커지급(Long couponId){
        log.info("쿠폰아이디 : {}",couponId);
    }
}

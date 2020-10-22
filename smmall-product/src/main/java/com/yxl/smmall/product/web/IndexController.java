package com.yxl.smmall.product.web;

import com.yxl.smmall.product.entity.PmsCategoryEntity;
import com.yxl.smmall.product.service.PmsCategoryService;
import com.yxl.smmall.product.vo.Catelog2VO;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** 商品搜索页面
 * @author SADSADSD
 */
@Controller
public class IndexController {

    @Autowired
    PmsCategoryService pmsCategoryService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/", "/index.html"})
    public String IndexPage(Model model) {
        List<PmsCategoryEntity> list = pmsCategoryService.getLavelCategory();
        model.addAttribute("category", list);
        return "index";
    }

    //  index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2VO>> getCatalog() {
        Map<String, List<Catelog2VO>> categoryJson = pmsCategoryService.getCategoryJson();
        return categoryJson;
    }

    @GetMapping("/hello")
    public String hello() {
        //获取一把锁，只要锁的名字一样，就是痛一把锁
        RLock lock = redissonClient.getLock("my-lock");
        /** RLock Java对象实现了java.util.concurrent.locks.Lock接口
         * public interface RLock extends Lock, RLockAsync
         */
        //2，加锁
        lock.lock();
        try {
            System.out.println("加锁成功，执行业务。。。。。" + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            lock.unlock();
            System.out.println("释放锁" + Thread.currentThread().getId());
        }

        return "hello";
    }


    /**
     * 保证一定能读到最新的数据，修改期间，写锁是一个排他锁（互斥锁），读锁是一个共享锁
     * 写锁没释放读就必须等待
     * 写+读 等待写锁释放
     * 写+写 阻塞
     * 读+写 等待写锁释放
     * 读+读 相当于无锁模式
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/writer")
    public String writeValue() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = readWriteLock.writeLock();
        try {
            rLock.lock();
            System.out.println("写锁加锁成功" + Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writerValue", s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("写锁释放" + Thread.currentThread().getId());
        }

        return s;
    }

    /**
     * 读锁
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/read")
    public String readValue() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        String writerValue = "";
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        try {
            System.out.println("读锁加锁成功" + Thread.currentThread().getId());
            writerValue = redisTemplate.opsForValue().get("writerValue");
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("读锁释放成功" + Thread.currentThread().getId());
        }

        return writerValue;
    }

    /**
     * semaphore 信号量 采用Java对象RSemaphore采用了与java.util.concurrent.Semaphore相似的接口和用法。
     * semaphroe也是一个阻塞式锁
     * 实例：雷士车库问题：
     * 5个车位
     * @return
     * @throws InterruptedException
     */
    @ResponseBody
    @GetMapping("/park")
     public String park() throws InterruptedException {
         RSemaphore semaphore = redissonClient.getSemaphore("park");
         semaphore.acquire();

         return "ok";
     }
    @ResponseBody
    @GetMapping("/go")
     public String go(){
        RSemaphore semaphore = redissonClient.getSemaphore("park");
        semaphore.release();
        return "go";
    }

    /**
     * 闭锁，
     * 模拟放假关门
     * @return
     * @throws InterruptedException
     */
    @ResponseBody
@GetMapping("/lockDoor")
public String lockDoor() throws InterruptedException {
    RCountDownLatch door = redissonClient.getCountDownLatch("door");
    door.trySetCount(5);
    door.await();
    return "放假了";
}
@ResponseBody
@GetMapping("/Out/{id}")
public String outDoors(@PathVariable int id){
    RCountDownLatch door = redissonClient.getCountDownLatch("door");
    door.countDown();

    return id+"班走了";

}

}

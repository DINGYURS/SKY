package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Resource
    private DishService dishService;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    @Cacheable(cacheNames = "dish", key = "#categoryId")
    public Result<List<DishVO>> list(Long categoryId) {

//        // 构造redis中的key
//        String key = "dish_" + categoryId;
//
//        // 查询redis中是否存在菜品数据
//        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
//        // 如果存在就直接返回而无需查询mysql数据库
//        if(list!=null && list.size()>0) {
//            return Result.success(list);
//        }
//        // 如果不存在就查询数据库然后将其存入redis

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<DishVO> list = dishService.listWithFlavor(dish);
//        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }

}

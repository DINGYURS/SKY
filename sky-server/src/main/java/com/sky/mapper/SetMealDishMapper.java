package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品id查询对应的套餐id
     * @param ids
     * @return
     */
    List<Long> getSetMealDishIds(List<Long> ids);

    /**
     * 保存套餐与菜品的对应关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 批量删除套餐与菜品的对应关系
     * @param setmealIds
     */
    void deleteBySetMealIds(List<Long> setmealIds);

    /**
     * 根据id查询套餐与菜品的对应关系
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);
}

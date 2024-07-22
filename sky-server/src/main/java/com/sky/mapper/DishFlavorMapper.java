package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据dish_id删除菜品信息
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long id);

    /**
     * 根据dish_id集合删除菜品信息
     * @param dishIds
     */
    void deleteByDishIds(List<Long> dishIds);

    /**
     * 根据dishId查询菜品对应的口味数据
     * @param dishId
     * @return
     */
    @Select("select id, dish_id, name, value from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getById(Long dishId);
}

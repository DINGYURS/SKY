package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Resource
    private SetMealMapper setMealMapper;

    @Resource
    private SetMealDishMapper setMealDishMapper;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 向套餐表添加数据
        setMealMapper.insert(setmeal);

        // 获取生成的id
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //保存套餐与菜品的对应关系
        setMealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> setmealPage = setMealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(setmealPage.getTotal(), setmealPage.getResult());
    }

    /**
     * 删除套餐
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断当前菜品能否被删除
        ids.forEach(id -> {
            Setmeal setmeal = setMealMapper.getById(id);
            if (setmeal.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        // 删除套餐表中的数据
        setMealMapper.deleteByIds(ids);
        // 删除套餐与菜品的对应关系
        setMealDishMapper.deleteBySetMealIds(ids);
    }

    /**
     * 根据id查询套餐数据
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        // 查询套餐表中的数据
        Setmeal setmeal = setMealMapper.getById(id);

        // 查询套餐与菜品的对应关系
        List<SetmealDish> setmealDishes = setMealDishMapper.getBySetmealId(id);

        //封装SetmealVO对象
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //根据id更新数据
        setMealMapper.update(setmeal);

        // 获取生成的id
        Long setmealId = setmeal.getId();

        /*
            可以选择在Mapper层中再写一个deleteBySetMealId方法用于根据id单次删除，
            但是这里为了适应deleteBySetMealIds，使得批量删除统一化，选择将id封装成List集合
         */
        List<Long> list = new ArrayList<>();
        list.add(setmealId);

        // 删除原有的套餐与菜品的对应关系
        setMealDishMapper.deleteBySetMealIds(list);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //插入新的套餐与菜品对应关系
        setMealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 起售或者禁售套餐
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setMealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setMealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setMealMapper.getDishItemBySetmealId(id);
    }
}

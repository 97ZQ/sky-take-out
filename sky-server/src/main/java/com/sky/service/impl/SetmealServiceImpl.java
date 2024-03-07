package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //向套餐里插入数据
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();

        //向套餐菜品关系表中插入数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setMealDishMapper.insertBatch(setmealDishes);


    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //先判断套餐的状态，如果套餐是起售，则抛出异常
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (StatusConstant.ENABLE == setmeal.getStatus() ){
                throw new SetmealEnableFailedException("商品在起售钟，无法售卖");
            }
        }
        //删除套餐表的数据
        setmealMapper.deleteBatch(ids);
        //删除套餐菜品表的关联
        setMealDishMapper.deleteBatch(ids);
    }

    /**
     * 根据id查询套餐和套餐菜品关系
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        //根据id查询套餐数据·
        Setmeal setmeal = setmealMapper.getById(id);
        //根据id查询套餐菜品关联数据
        List<SetmealDish> setmealDishes = setMealDishMapper.getBySetmealId(id);
        //将两种数据封装到DishVO钟
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
         Setmeal setmeal = new Setmeal();
         BeanUtils.copyProperties(setmealDTO,setmeal);
        //修改套餐数据
        setmealMapper.update(setmeal);
        //删除套餐菜品关联数据

        setMealDishMapper.deleteBySetmealId(setmealDTO.getId());
        //插入新的套餐菜品关联数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });

            setMealDishMapper.insertBatch(setmealDishes);
        }

    }
}

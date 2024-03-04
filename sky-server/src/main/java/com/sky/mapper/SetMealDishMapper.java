package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品ID参训对应套餐id
     * @param dishIDs
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in ()
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);
}

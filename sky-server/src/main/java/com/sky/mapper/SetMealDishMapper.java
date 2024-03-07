package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品ID参训对应套餐id
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in ()
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);

    /**
     * 分批保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);



    /**
     *  根据套餐id集合删除联系
     * @param setmealIds
     */
    void deleteBatch(List<Long> setmealIds);

    /**
     * 根据套餐id查询套餐菜品关联信息
     * @param selmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long selmealId);

    /**
     * 根据套餐id删除菜品套餐关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}

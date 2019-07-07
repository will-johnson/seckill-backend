package com.seen.seckillbackend.service;

import com.seen.seckillbackend.dao.GoodsDao;
import com.seen.seckillbackend.domain.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public boolean reduceStockById(long id, int buy) {
        int red = goodsDao.reduceStockById(id, buy );
        return red>0;
    }

    public List<Goods> getGoodsList() {
        return goodsDao.getGoodsList();
    }

    public Goods getGoodById(long id) {
        return goodsDao.getGoodById(id);
    }


    public Long getGoodsStockById(long id) {
        return goodsDao.getGoodsStockById(id);
    }

    public void reset() {
        goodsDao.reset();
    }
}

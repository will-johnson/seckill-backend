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

    public List<Goods> getGoodsList() {
        return goodsDao.getGoodsList();
    }

    public Goods getGoodById(long id) {
        return goodsDao.getGoodById(id);
    }

    public boolean reduceStockById(long id) {
        boolean b = goodsDao.reduceStockById(id);
        return b;
    }

    public Long getGoodsStockById(long id) {
        return goodsDao.getGoodsStockById(id);
    }

    public void reset() {
        goodsDao.reset();
    }
}

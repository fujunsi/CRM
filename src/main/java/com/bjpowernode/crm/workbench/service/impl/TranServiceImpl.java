package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);


    public boolean save(Tran t, String customerName) {

        /*
            执行交易添加业务
                在做添加之前，参数t里面少了一项信息，就是客户的主键，customerId
                先处理客户的相关的需求
                    1.判断customerName，根据客户名称在客户表进行精确查询
                      如果有这个客户，则取出这个客户的id，封装到t对象中
                      如果没有这个客户，则在建一张客户表，然后将新建的客户的id取出，封装到t对象中

                     2.经过以上操作后t对象中的信息就全了，需要执行添加交易的操作

                     3.添加交易完成，需要创建一条交易历史
         */

        boolean flag = true;

        Customer customer = customerDao.getCustomerByName(customerName);

        if (customer==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(t.getCreateBy());
            customer.setCreateTime(t.getCreateTime());
            customer.setContactSummary(t.getContactSummary());
            customer.setNextContactTime(t.getNextContactTime());

            //添加客户
            int count1 = customerDao.save(customer);
            if (count1!=1){
                flag = false;
            }
        }

        //通过以上对于客户的处理，不论是查询出来已有的客户，还是以前没有我们新增的客户，总之客户已经有了，客户id就有了

        //将客户的id封装到t对象中
        t.setCustomerId(customer.getId());

        //添加交易
        int count2 = tranDao.save(t);
        if (count2!=1){
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateTime(t.getCreateTime());
        th.setCreateBy(t.getCreateBy());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setStage(t.getStage());
        th.setTranId(t.getId());
        int count3 = tranHistoryDao.save(th);
        if (count3!=1){
            flag = false;
        }

        
        return flag;
    }

    public Tran detail(String id) {

        Tran t = tranDao.detail(id);

        return t;
    }

    public List<TranHistory> getHistoryListByTranId(String tranId) {

        List<TranHistory> tList = tranHistoryDao.getHistoryListByTranId(tranId);

        return tList;
    }

    public boolean changeStage(Tran t) {

        boolean flag = true;

        //改变交易阶段
        int count1 = tranDao.changeStage(t);
        if (count1!=1){
            flag = false;
        }

        //交易阶段改变后，生成交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        //添加交易历史
        int count2 = tranHistoryDao.save(th);
        if (count1!=1){
            flag = false;
        }

        return flag;
    }

    public Map<String, Object> getCharts() {

        //取得total
        int total = tranDao.getTotal();

        //取得dataList

        List<Map<String,Object>> dataList = tranDao.getCharts();

        //将total和dataList保存到map
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("total",total);
        map.put("dataList",dataList);


        return map;
    }
}

package cn.solarcat.service.Impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.solarcat.aop.Log;
import cn.solarcat.common.configuration.ActiveMQConfiguration;
import cn.solarcat.common.configuration.ItemConfiguration;
import cn.solarcat.common.pojo.ACTION;
import cn.solarcat.common.pojo.EasyUIDataGridResult;
import cn.solarcat.common.util.IDUtils;
import cn.solarcat.common.util.SolarCatResult;
import cn.solarcat.mapper.TbItemDescMapper;
import cn.solarcat.mapper.TbItemMapper;
import cn.solarcat.pojo.TbItem;
import cn.solarcat.pojo.TbItemDesc;
import cn.solarcat.pojo.TbItemExample;
import cn.solarcat.pojo.TbItemExample.Criteria;
import cn.solarcat.service.ItemService;

@Service
@Component
public class ItemServiceImpl implements ItemService {
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public SolarCatResult addItem(TbItem tbItem, String desc) {
		final long Id = IDUtils.genItemId();
		tbItem.setId(Id);
		tbItem.setStatus((byte) 1);
		tbItem.setCreated(new Date());
		tbItem.setUpdated(new Date());
		itemMapper.insert(tbItem);
		TbItemDesc tbItemDesc = new TbItemDesc();
		tbItemDesc.setItemId(Id);
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setCreated(new Date());
		tbItemDesc.setUpdated(new Date());
		itemDescMapper.insert(tbItemDesc);
		// 发送一个商品添加消息
//		jmsTemplate.convertAndSend(itemTopic, new MessageCreator() {
//			@Override
//			public Message createMessage(Session session) throws JMSException {
//				TextMessage textMessage = session.createTextMessage();
//				return textMessage;
//			}
//		});
		jmsTemplate.send(ActiveMQConfiguration.ITEM_ADD, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(Id + "");
				return textMessage;
			}
		});
		// jmsTemplate.convertAndSend(itemTopic, jsonObject);
		return SolarCatResult.ok();
	}

	@Override
	public SolarCatResult deleteItem(Long itemId) {
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		tbItem.setStatus((byte) 3);
		itemMapper.updateByPrimaryKey(tbItem);
		return SolarCatResult.ok();
	}

	@Override
	public TbItem getItem(Long itemId) {
		TbItem tbItem = new TbItem();
		tbItem = itemMapper.selectByPrimaryKey(itemId);
		return tbItem;
	}

	@Override
	@Log(action = ACTION.SELECT)
	public EasyUIDataGridResult getItemList(int page, int rows) {
		// 设置分页信息
		PageHelper.startPage(page, rows);
		// 执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		// 创建一个返回值对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		// 取分页结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		// 取总记录数
		long total = pageInfo.getTotal();
		result.setTotal((int) total);
		return result;
	}

	@Override
	public TbItem getTbItemById(long itemId) {
		try {
			String json = redisTemplate.opsForValue().get(ItemConfiguration.REDIS_ITEM_PRE + ":" + itemId + ":BASE");
			if (StringUtils.isNotBlank(json)) {
				// TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				TbItem tbItem = JSONObject.parseObject(json, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(itemId);
		List<TbItem> list = itemMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			try {
				redisTemplate.opsForValue().set(ItemConfiguration.REDIS_ITEM_PRE + ":" + itemId + ":BASE",
						/* JsonUtils.objectToJson(list.get(0)) */JSONObject.toJSONString(list.get(0)));
				redisTemplate.expire(ItemConfiguration.REDIS_ITEM_PRE + ":" + itemId + ":BASE",
						ItemConfiguration.ITEM_CACHE_EXPIRE, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list.get(0);
		}
		return null;
	}

	@Override
	public TbItemDesc getTbItemDescById(long itemId) {
		try {
			String json = redisTemplate.opsForValue().get(ItemConfiguration.REDIS_ITEM_PRE + ":" + itemId + ":DESC");
			if (StringUtils.isNotBlank(json)) {
				// TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				TbItemDesc tbItemDesc = JSONObject.parseObject(json, TbItemDesc.class);
				return tbItemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		try {
			redisTemplate.opsForValue().set(ItemConfiguration.REDIS_ITEM_PRE + ":" + itemId + ":DESC",
					/* JsonUtils.objectToJson(tbItemDesc) */JSONObject.toJSONString(tbItemDesc));
			redisTemplate.expire(ItemConfiguration.REDIS_ITEM_PRE + ":" + itemId + ":DESC",
					ItemConfiguration.ITEM_CACHE_EXPIRE, TimeUnit.MICROSECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItemDesc;
	}

	@Override
	public SolarCatResult updateInstock(Long itemId) {
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		tbItem.setStatus((byte) 2);
		itemMapper.updateByPrimaryKey(tbItem);
		return SolarCatResult.ok();
	}

	@Override
	public SolarCatResult updateItemAndDesc(TbItem item, TbItemDesc itemDesc) {
		item.setUpdated(new Date());
		itemMapper.updateByPrimaryKeySelective(item);
		itemDesc.setUpdated(new Date());
		itemDescMapper.updateByPrimaryKeySelective(itemDesc);
		return SolarCatResult.ok();
	}

	@Override
	public SolarCatResult updateReshelf(Long itemId) {
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		tbItem.setStatus((byte) 1);
		itemMapper.updateByPrimaryKey(tbItem);
		return SolarCatResult.ok();
	}

}

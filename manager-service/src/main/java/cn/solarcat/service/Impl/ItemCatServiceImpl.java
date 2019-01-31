package cn.solarcat.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;

import cn.solarcat.common.pojo.EasyUITreeNode;
import cn.solarcat.mapper.TbItemCatMapper;
import cn.solarcat.pojo.TbItemCat;
import cn.solarcat.pojo.TbItemCatExample;
import cn.solarcat.pojo.TbItemCatExample.Criteria;
import cn.solarcat.service.ItemCatService;

@Service
@Component
public class ItemCatServiceImpl implements ItemCatService {
	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Override
	public List<EasyUITreeNode> getItemCatList(long parentId) {
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		List<EasyUITreeNode> resultList = new ArrayList<>();
		for (TbItemCat tbItemCat : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbItemCat.getId());
			node.setState(tbItemCat.getIsParent() ? "closed" : "open");
			node.setText(tbItemCat.getName());
			resultList.add(node);
		}
		return resultList;
	}

}

package cn.signit.service.db.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.signit.dao.mysql.UserMapper;
import cn.signit.domain.mysql.User;
import cn.signit.service.NormalService;
import cn.signit.service.db.UserService;

/**
*用户账户相关基本服务
* @ClassName UserServiceImpl
* @author Liwen
* @date 2016年11月13日-上午10:34:36
* @version (版本号)
* @see (参阅)
*/
@Service("userService")
public class UserServiceImpl extends NormalService implements UserService{
	@Resource
	private UserMapper userDao;

	/**
	*@param account
	*@return
	*@see (参阅)
	*/
	@Override
	public User getUser(String account) {
		return userDao.selectByAvailableName(account);
	}

	/**
	*@param userid
	*@return
	*@see (参阅)
	*/
	@Override
	public User getUser(Long userid) {
		return userDao.selectByPrimaryKey(userid);
	}

	/**
	*@param user
	*@return
	*@see (参阅)
	*/
	@Override
	public User getUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	*@param user
	*@return
	*@see (参阅)
	*/
	@Override
	public Long addUserAndGetId(User user) {
		return userDao.insertAndGetId(user);
	}

	/**
	*@param user
	*@return
	*@see (参阅)
	*/
	@Override
	public boolean updateUser(User user) {
		return toBoolean(userDao.updateByPrimaryKeySelective(user));
	}
}

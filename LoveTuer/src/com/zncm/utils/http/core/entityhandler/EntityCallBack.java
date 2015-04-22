package com.zncm.utils.http.core.entityhandler;

public interface EntityCallBack {
	
	/**
	 * 
	 * @Description
	 * @param count
	 * @param current
	 * @param mustNoticeUI
	 * 
	 */
	public void callBack(long count, long current, boolean mustNoticeUI);
}
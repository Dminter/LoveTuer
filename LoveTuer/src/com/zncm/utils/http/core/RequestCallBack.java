package com.zncm.utils.http.core;


public abstract class RequestCallBack<T> {

	private boolean progress = true;
	private int rate = 1000 * 1;// 每秒
	private String id;

	public boolean isProgress() {
		return progress;
	}

	public int getRate() {
		return rate;
	}

	public void setId(String id) {
        this.id = id;
    }
	
	public String getId() {
        return id;
    }
	/**
	 * 设置进度,而且只有设置了这个了以后，onLoading才能有效。
	 * 
	 * @param progress
	 *            是否启用进度显示
	 * @param rate
	 *            进度更新频率
	 */
	public RequestCallBack<T> progress(boolean progress, int rate) {
		this.progress = progress;
		this.rate = rate;
		return this;
	}

	public void onStart() {
	};

	/**
	 * onLoading方法有效progress
	 * 
	 * @param count
	 * @param current
	 */
	public void onLoading(long count, long current) {
	};

	public abstract void onSuccess(T t);

	public abstract void onFailure(Throwable t, String strMsg);
}

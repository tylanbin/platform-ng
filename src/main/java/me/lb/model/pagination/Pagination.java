package me.lb.model.pagination;

import java.util.List;

public class Pagination<T> {

	private int total;
	private List<T> datas;

	public Pagination() {
	}

	public Pagination(int total, List<T> datas) {
		this.total = total;
		this.datas = datas;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

}
package com.yyx.aio.common.pageInfo;

import java.io.Serializable;
import java.util.List;

/*
 * 
 * @author: yaofeng
 * @version:2012-11-27  下午4:03:50
 * 类说明  分页类
 */
@SuppressWarnings("serial")
public class PageInfo<E> implements Serializable{

	private List<E> rows;

	private long total;
	
	private Integer pageSize=10;
	
	private Integer pageNumber=1;
	
	//排序：字段（自己根据前台定义，后台对应数据库字段）
	private String sortColum;
	
	//排序：顺序（1升序，2降序）
	private Integer sortMethod = 1;
	
	public String getSortColum() {
		return sortColum;
	}

	public void setSortColum(String sortColum) {
		this.sortColum = sortColum;
	}

	public Integer getSortMethod() {
		return sortMethod;
	}

	public void setSortMethod(Integer sortMethod) {
		this.sortMethod = sortMethod;
	}

	/*
	 * 获取
	 * 
	 * @return the rows
	 */
	public List<E> getRows() {
		return rows;
	}

	/*
	 * 设置
	 * 
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(List<E> rows) {
		this.rows = rows;
	}

	

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	/*
	 *获取
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/*
	 *设置
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/*
	 *获取
	 * @return the pageNumber
	 */
	public Integer getPageNumber() {
		return pageNumber;
	}

	/*
	 *设置
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	

	public int getStart() {
		return (this.pageNumber - 1) * this.pageSize;
	}
}

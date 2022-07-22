package com.sdzx.tools;

import android.graphics.Bitmap;
import com.avos.avoscloud.*;

/**
 * 用户中心List列表信息
 * 
 * @author Administrator
 * 
 */
public class NewsItemDataClass {
	private String fenleiCid;//在精选页面的ID
	/** 编号 **/
	private String cid;
	/** 名字 **/
	private String title;
	/** 摘要 **/
	private String abstra;
	/**图片**/
	private Bitmap bmp;
	/**URL**/
	private String url;
	
	private String className;
	private AVObject avo;
	private String summary;
	
	AVObject user;
	
	public AVObject getUser(){
		return user;
	}

	public void setUser(AVObject newUsr){
		user=newUsr;
	}
	public String getFenleiCid(){
		return fenleiCid;
	}

	public void setFenleiCid(String newFCid){
		fenleiCid=newFCid;
	}
	
	public String getCid() {
		return cid;
	}

	public String getSummary(){
		return summary;
	}

	public AVObject getObj() {
		return avo;
	}
	public void setObj(AVObject newAvo) {
		this.avo=newAvo;
	}
	
	public void setSummary(String sm){
		this.summary=sm;
	}
	
	public void setCid(String cidm) {
		this.cid = cidm;
	}

	public String getCatname() {
		return title;
	}

	public void setCatname(String catname) {
		this.title = catname;
	}

	public String getClassname() {
		return className;
	}

	public void setClassname(String ss) {
		this.className = ss;
	}
	
	public String getabstra() {
		return abstra;
	}
	
	public void setUrl(String urlname) {
		this.url = urlname;
	}

	public String getUrl() {
		return url;
	}
	
	public void setBmp(Bitmap bmpname) {
		this.bmp= bmpname;
	}

	public Bitmap getBmp() {
		return bmp;
	}

	public void setabstra(String absname) {
		this.abstra = absname;
	}
	
	public NewsItemDataClass() {
		// TODO Auto-generated constructor stub
	}

	public NewsItemDataClass(String classNamestr,String cidm, String catname,String absname,String mSummary,AVObject newA) {
		// TODO Auto-generated constructor stub
		this.cid = cidm;
		this.title = catname;
		this.abstra = absname;
		this.summary=mSummary;
		this.className=classNamestr;
		this.avo=newA;
		//this.user=newAvo;
	}
}

package com.sdzx.news.views;

import android.content.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.graphics.drawable.*;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
	private List<List<String>> datas;
	private List<Integer> ids;
	private int layout;
	private List<String> items;
	private Context context;
	private OnRecyclerViewClickListener onRecyclerViewClickListener=null;
	private OnRecyclerViewLongClickListener onRecyclerViewLongClickListener=null;
	
	public RecyclerViewAdapter(Context context,int LayoutId,List<Integer> ids,List<List<String>> datas){
		this.context=context;
		this.layout=LayoutId;
		this.ids=ids;
		this.datas=datas;
	}

	public static interface OnRecyclerViewClickListener
	{
		public void onItemClick(View v, int position);
	}

	public static interface OnRecyclerViewLongClickListener{
		public void onLongClick(View v,int position);
	}
	
	@Override
	public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// TODO: Implement this method
		View v=LayoutInflater.from(p1.getContext()).inflate(layout, p1, false);
		return new ViewHolder(v,this.ids ,this.onRecyclerViewClickListener,this.onRecyclerViewLongClickListener);
	}

	@Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
		// TODO: Implement this method
		for(int p=0;p<viewHolder.views.size();p++){
			viewHolder.views.get(i).setText(datas.get(i).get(p));
		}
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return items == null ?0: items.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
	{

		public List<TextView> views=new ArrayList<TextView>();
		private OnRecyclerViewClickListener onRecyclerViewClickListener=null;
		private OnRecyclerViewLongClickListener onRecyclerViewLongClickListener=null;

		@Override
		public void onClick(View p1)
		{
			// TODO: Implement this method
			if (this.onRecyclerViewClickListener != null)
			{
				this.onRecyclerViewClickListener.onItemClick(p1, getPosition());
			}
		}

		@Override
		public boolean onLongClick(View p1)
		{
			// TODO: Implement this method
			if(this.onRecyclerViewLongClickListener !=null){
				this.onRecyclerViewLongClickListener.onLongClick(p1,getPosition());
				return true;
			}
			else return false;
		}
		
		public ViewHolder(View v,List<Integer> ids,OnRecyclerViewClickListener onRecyclerViewClickListener,OnRecyclerViewLongClickListener onRecyclerViewLongClickListener)
		{
			super(v);
			this.onRecyclerViewClickListener = onRecyclerViewClickListener;
			this.onRecyclerViewLongClickListener=onRecyclerViewLongClickListener;
			for(int i=0;i<ids.size();i++){
				TextView view=(TextView)v.findViewById(ids.get(i).intValue());
				view.setOnClickListener(this);
				view.setOnLongClickListener(this);
				views.add(view);
			}
		}
	}

	public void setOnItemClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener)
	{
		this.onRecyclerViewClickListener = onRecyclerViewClickListener;
	}
	
	public void setOnItemLongClickListener(OnRecyclerViewLongClickListener onRecyclerViewLongClickListener){
		this.onRecyclerViewLongClickListener=onRecyclerViewLongClickListener;
	}

}

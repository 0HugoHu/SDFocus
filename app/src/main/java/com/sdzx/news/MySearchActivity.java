package com.sdzx.news;
import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.search.AVSearchQuery;
import com.sdzx.tools.ApplicationHelper;
import com.sdzx.tools.NewsItemDataClass;

import android.os.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.content.*;
import android.graphics.*;
import android.text.*;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class MySearchActivity extends SwipeBackActivity
{
	private TextView resultTextView;
	private EditText searEditText;

	int cateId=1;
	private ListView listView;
	private int size=3;
	private ListViewAdapter adapter;
	private ArrayList<NewsItemDataClass> list;
	private int numberOfRefreshEachTime=1;//每次加载的条数
	private int numberOfItems=20;//item的数量，以后要随云端更新
	private ApplicationHelper appHelper;
	private boolean ifLoading=true;//是否在加载中
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);

		appHelper=new ApplicationHelper();

		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.searchToolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setTitle("搜索");

		resultTextView=(TextView)findViewById(R.id.resultSearchlayoutTextView);
		searEditText=(EditText)findViewById(R.id.SearchEditTextView1);
		
		searEditText.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				MySearchActivity.this.onSearchClick(null);
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});

			

		list = new ArrayList<NewsItemDataClass>();
		listView = (ListView) findViewById(R.id.searchListView1);
	    listView.setDivider(null);
	    listView.setFastScrollEnabled(false);
	    listView.setFastScrollAlwaysVisible(false);
		adapter = new ListViewAdapter(this, list);
		listView.setAdapter(adapter);
		AVOSCloud.initialize(this, "rTL0VObhRrmgAPdG7fWrd0lr", "xjLm6hYPJHxxtkdEfJkpPomD");


		ifLoading=true;

		
		list.add(new NewsItemDataClass("Test",""," ",null," ",null));
		
	}
	

	
	
	
	
	public void onSearchClick(View v){
		list = new ArrayList<NewsItemDataClass>();
		listView = (ListView) findViewById(R.id.searchListView1);
	    listView.setDivider(null);
	    listView.setFastScrollEnabled(false);
	    listView.setFastScrollAlwaysVisible(false);
		adapter = new ListViewAdapter(this, list);
		listView.setAdapter(adapter);
		list.add(new NewsItemDataClass("Test", "", " ", null, " ", null));

		
		String searchString;
		searchString=searEditText.getText().toString();

		AVSearchQuery search = new AVSearchQuery(searchString);
		search.setLimit(30);

		search.findInBackgroud(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> objects, AVException exception) {
				if (exception == null) {
					//你可以使用 objects来展现自己的UI
					if (objects.size()==30)
						resultTextView.setText( "查询到30+条符合条件的数据");
					else resultTextView.setText( "查询到"+objects.size()+"条符合条件的数据");
					for (int i=0;i<objects.size();i++) {
						list.add(new NewsItemDataClass(objects.get(i).getClassName(), objects.get(i).getObjectId(), objects.get(i).getString("Title").toString(), objects.get(i).getString("Message").toString(), objects.get(i).getString("Summary").toString(), objects.get(i)));
						adapter.notifyDataSetChanged();
					}
				} else {
					//Exception happened
					resultTextView.setText( "无数据");
				}

			}
		});




/*



		if (searchString.equals("")) return;
		
		List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
		queries.add(AVQuery.getQuery("Test").whereContains("Title", searchString));
		queries.add(AVQuery.getQuery("Test").whereContains("Message", searchString));
		AVQuery<AVObject> mainQuery = AVQuery.or(queries);
		mainQuery.findInBackground(new FindCallback<AVObject>() 
				{
					@Override
					public void done(List<AVObject> avObjects, AVException e) 
					{
						if (e==null)
						{
							resultTextView.setText( "查询到" + avObjects.size() + " 条符合条件的数据");
				            }
						else resultTextView.setText( "无数据");
			            
						
						for (int i=0;i<avObjects.size();i++){
							list.add(new NewsItemDataClass(avObjects.get(i).getClassName(),avObjects.get(i).getObjectId(), avObjects.get(i).getString("Title").toString(),avObjects.get(i).getString("Message").toString(),avObjects.get(i).getString("Summary").toString(),avObjects.get(i)));
							adapter.notifyDataSetChanged();
						}
					}
				});
				*/
	}
	
	
	
	
	public class ListViewAdapter extends BaseAdapter 
	{
		private Context context;
		private ArrayList<NewsItemDataClass> list;
	
		public ListViewAdapter(Context context, ArrayList<NewsItemDataClass> list) 
		{
			this.context = context;
			this.list = list;
		}
		public int getCount() 
		{
			return list.size();
		}
	
		public Object getItem(int arg0) 
		{
			return null;
		}
	
		public long getItemId(int arg0) 
		{
			return 0;
		}
		Type_1_Holder type_1_view = null;
		public View getView(final int position, View convertView, ViewGroup arg2) 
		{
			if (position<list.size()){
			type_1_view = new Type_1_Holder();
			
			convertView = LayoutInflater.from(context).inflate(R.layout.search_item, null);
			type_1_view.list_text = (TextView) convertView.findViewById(R.id.user_center_item_txt);
			type_1_view.sumText= (TextView) convertView.findViewById(R.id.itemMainSummary);
			type_1_view.sumText.setText(list.get(position).getSummary());
			type_1_view.list_text.setText(list.get(position).getCatname());
			listView.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
						
					Intent intent = new Intent();
					intent.putExtra("cid",(list.get(p3).getCid())+"");
					intent.putExtra("ClassName", list.get(p3).getClassname());
					intent.setClass(MySearchActivity.this, ReaderActivity.class);
					startActivity(intent);
					}
				});
			
		 
			if (position==0){
				convertView= LayoutInflater.from(context).inflate(R.layout.viewpager_item, null);
				
			}
				
			}
				
			return convertView;
		}
		
		class Type_1_Holder 
		{
			TextView list_text;
			TextView sumText;
		    Button onclick;
		}
		
	}
	
	
	
}

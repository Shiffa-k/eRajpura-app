package com.erajpura;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.erajpura.Model.TopCategoryAndShops;
import com.erajpura.adapter.CategoryListAdapter;
import com.erajpura.common.AppController;
import com.erajpura.common.Global;
import com.erajpura.common.GridSpacesItemDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryListActivity extends AppCompatActivity {

    TextView toolBarTitle;
    LinearLayout backButton;

    RecyclerView recyclerViewCategory;
    GridLayoutManager gridLayoutManager;
    CategoryListAdapter categoryListAdapter;
    SwipeRefreshLayout swipe;

    GsonBuilder gsonBuilder;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        inflateToolbar();
        inflateLayout();
        getCategoryList();;


    }

    public void inflateToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolBarTitle=(TextView) toolbar.findViewById(R.id.toolbar_title);
        backButton=(LinearLayout) toolbar.findViewById(R.id.imageView_back);


        setSupportActionBar(toolbar);
        toolBarTitle.setText("Category");
        backButton.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void inflateLayout()
    {
        recyclerViewCategory=(RecyclerView)findViewById(R.id.recyclerViewCategoryList);
        gridLayoutManager= new GridLayoutManager(CategoryListActivity.this,2);
        swipe=(SwipeRefreshLayout)findViewById(R.id.swipe);
        swipe.setEnabled(false);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin5);
        recyclerViewCategory.addItemDecoration(new GridSpacesItemDecoration(spacingInPixels,true));
        recyclerViewCategory.setLayoutManager(gridLayoutManager);

        gsonBuilder = new GsonBuilder();

        gson = gsonBuilder.create();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchViewItem.setVisible(false);
        //searchViewItem.setIntent(new Intent(CategoryListActivity.this,SearchActivity.class));

        return true;
    }

    public void getCategoryList() {

        try {
            swipe.setRefreshing(true);

            String LOGIN_URL= Global.BASE_URL + Global.API_ALL_CATEGORIES;
            StringRequest sr = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    swipe.setRefreshing(false);

                    try{
                        JSONObject obj=new JSONObject(response);
                        if(obj.getInt("code")==1){

                            List<TopCategoryAndShops> shopsList = Arrays.asList(gson.fromJson(obj.getString("data"), TopCategoryAndShops[].class));
                            categoryListAdapter =new CategoryListAdapter(CategoryListActivity.this,shopsList,null);
                            recyclerViewCategory.setAdapter(categoryListAdapter);


                        }
                    }catch (Exception e){e.printStackTrace();}
                }
            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipe.setRefreshing(false);
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(CategoryListActivity.this, "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                    } else  {
                        VolleyLog.d("", "" + error.getMessage() + "," + error.toString());
                    }
                }
            }
            )

            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", "aa");
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.frodo.app.android.simple;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.android.app.simple.R;
import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.android.core.toolbox.ScreenUtils;
import com.frodo.app.android.simple.entity.Movie;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.framework.log.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 2015/9/14.
 */
public class MovieView extends UIView {
	private BaseAdapter movieAdapter;
	private GridView gridView;
	private List<Movie> movies = new ArrayList<>();

	public MovieView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
		super(presenter, inflater, container, R.layout.layout_movie_list);
	}

	@Override
	public void initView() {
		int[] imageSize = calcPosterSize();
		gridView = (GridView) getRootView().findViewById(R.id.gridview);

		movieAdapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return movies.size();
			}

			@Override
			public Movie getItem(int position) {
				return movies.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder;
				if (convertView == null) {
					convertView = LayoutInflater.from(getPresenter().getAndroidContext()).inflate(R.layout.layout_movie_item, null);
					holder = new ViewHolder();
					holder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.image);
					holder.imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
					holder.textView = (TextView) convertView.findViewById(R.id.text);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				Movie movie = getItem(position);
				Logger.fLog().tag("MovieView").i("Loading image : " + movie.posterPath);
				holder.imageView.setImageURI( Uri.parse(movie.posterPath));

				holder.textView.setText(movie.title);

				return convertView;
			}

			class ViewHolder {
				SimpleDraweeView imageView;
				TextView textView;
			}
		};

		gridView.setAdapter(movieAdapter);
	}

	@Override
	public void registerListener() {
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("movie", movies.get(position));
//				FragmentScheduler.nextFragment(getPresenter().getAndroidContext(), MovieDetailFragment.class, bundle);
                FragmentScheduler.doNext(getPresenter().getAndroidContext(), FragmentScheduler.schema() + "/redirect", bundle);
			}
		});
	}

	private int[] calcPosterSize() {
		int itemWidth = (ScreenUtils.getScreenWidth(getPresenter().getAndroidContext()) - 4 * 20) / 3;
		int itemHeight = itemWidth * 278 / 135;
		return new int[]{itemWidth, itemHeight};
	}

	public void showMovieList(List<Movie> movies) {
		if (movies==null || movies.isEmpty()) return;
		this.movies.clear();
		this.movies.addAll(movies);
		movieAdapter.notifyDataSetChanged();
	}

	public void showError(String errorMsg) {
		if (isOnShown()) {
			Toast.makeText(getPresenter().getAndroidContext(), errorMsg, Toast.LENGTH_SHORT).show();
		}
	}
}

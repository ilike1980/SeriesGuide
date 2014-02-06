/*
 * Copyright 2014 Uwe Trottmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.battlelancer.seriesguide.adapters;

import com.battlelancer.seriesguide.settings.DisplaySettings;
import com.battlelancer.seriesguide.settings.TmdbSettings;
import com.battlelancer.seriesguide.ui.SeriesGuidePreferences;
import com.battlelancer.seriesguide.util.ImageDownloader;
import com.uwetrottmann.seriesguide.R;
import com.uwetrottmann.tmdb.entities.Movie;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

/**
 * Displays movie titles of the given {@link Movie} array.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    private static int LAYOUT = R.layout.movie_item;

    private LayoutInflater mInflater;

    private ImageDownloader mImageDownloader;

    private OnClickListener mOnClickListener;

    private String mImageBaseUrl;

    private DateFormat dateFormatMovieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public MoviesAdapter(Context context, OnClickListener listener) {
        super(context, LAYOUT);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageDownloader = ImageDownloader.getInstance(context);
        mOnClickListener = listener;

        // figure out which size of posters to load based on screen density
        if (DisplaySettings.isVeryHighDensityScreen(context)) {
            mImageBaseUrl = TmdbSettings.getImageBaseUrl(context)
                    + TmdbSettings.POSTER_SIZE_SPEC_W342;
        } else {
            mImageBaseUrl = TmdbSettings.getImageBaseUrl(context)
                    + TmdbSettings.POSTER_SIZE_SPEC_W154;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid
        // unnecessary calls to findViewById() on each row.
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(LAYOUT, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.textViewMovieTitle);
            holder.date = (TextView) convertView.findViewById(R.id.textViewMovieDate);
            holder.poster = (ImageView) convertView.findViewById(R.id.imageViewMoviePoster);
            holder.contextMenu = (ImageView) convertView
                    .findViewById(R.id.imageViewMovieItemContextMenu);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        Movie movie = getItem(position);

        holder.title.setText(movie.title);
        if (movie.release_date != null) {
            holder.date.setText(dateFormatMovieReleaseDate.format(movie.release_date));
        } else {
            holder.date.setText("");
        }
        if (!TextUtils.isEmpty(movie.poster_path)) {
            mImageDownloader.download(mImageBaseUrl + movie.poster_path, holder.poster, false);
        } else {
            // clear image
            holder.poster.setImageDrawable(null);
        }

        // context menu
        holder.contextMenu.setOnClickListener(mOnClickListener);

        return convertView;
    }

    public void setData(List<Movie> data) {
        clear();
        if (data != null) {
            for (Movie item : data) {
                if (item != null) {
                    add(item);
                }
            }
        }
    }

    static class ViewHolder {

        TextView title;

        TextView date;

        ImageView poster;

        ImageView contextMenu;
    }

}
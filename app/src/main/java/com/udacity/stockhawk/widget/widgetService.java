package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.udacity.stockhawk.data.Contract.Quote;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.StockMoreInfo;

import static com.udacity.stockhawk.R.id.symbol;


public class widgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockRemoteViewsFactory(getApplicationContext(), intent);
    }

    class StockRemoteViewsFactory implements RemoteViewsFactory {
        private Context context;
        private Cursor cursor;
        private Intent intent;


        public StockRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;

        }


        // Init Cursor Function
        private void initCursor() {
            if (cursor != null) {
                cursor.close();
            }
            final long identityToken = Binder.clearCallingIdentity();

            cursor = context.getContentResolver().query(
                    Quote.URI,
                    new String[]{
                            Contract.Quote.COLUMN_SYMBOL,
                            Contract.Quote.COLUMN_PRICE,
                            Contract.Quote.COLUMN_ABSOLUTE_CHANGE
                    },
                    null,
                    null,
                    null);
            Binder.restoreCallingIdentity(identityToken);
        }


        @Override
        public void onCreate() {
            initCursor();
            if (cursor != null) {
                cursor.moveToFirst();
            }
        }

        @Override
        public void onDataSetChanged() {
            initCursor();
        }

        @Override
        public void onDestroy() {
            cursor.close();
        }

        @Override
        public int getCount() {
            int CountInt = cursor.getCount();
            String count = String.valueOf(CountInt);
            Log.v("Count", count);
            return cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_list_item);

            if (cursor.moveToPosition(i)) {

                remoteViews.setTextViewText(R.id.widget_symbol, cursor.getString(0));
                remoteViews.setTextViewText(R.id.widget_price, cursor.getString(1));

                // Color red/green
                if (Float.parseFloat(cursor.getString(2)) > 0) {
                    remoteViews.setTextViewText(R.id.widget_change, cursor.getString(2));
                    remoteViews.setTextColor(R.id.widget_change, ResourcesCompat.getColor(getResources(), R.color.material_green_700, null));

                } else {
                    remoteViews.setTextViewText(R.id.widget_change, cursor.getString(2));
                    remoteViews.setTextColor(R.id.widget_change, ResourcesCompat.getColor(getResources(), R.color.material_red_700, null));
                }

                final Intent fillInIntent = new Intent();
                context.getContentResolver().query(
                        Quote.URI,
                        new String[]{
                                Contract.Quote.COLUMN_SYMBOL,
                                Contract.Quote.COLUMN_PRICE,
                                Contract.Quote.COLUMN_ABSOLUTE_CHANGE
                        },
                        null,
                        null,
                        null);
                fillInIntent.putExtra(widgetProvider.EXTRA_SYMBOL, cursor.getString(0));
                remoteViews.setOnClickFillInIntent(R.id.wid_list_item,fillInIntent);

            }

            return remoteViews;
        }


        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}


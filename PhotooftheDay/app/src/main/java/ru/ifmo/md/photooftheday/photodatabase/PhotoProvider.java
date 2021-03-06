package ru.ifmo.md.photooftheday.photodatabase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public class PhotoProvider extends ContentProvider {
    public static final String TAG = PhotoProvider.class.getSimpleName();

    public static final String AUTHORITY = PhotoProvider.class.getCanonicalName();
    public static final String PHOTO_TABLE = PhotoDatabaseHelper.Tables.PHOTO;

    public static final Uri PHOTO_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PHOTO_TABLE);
    public static final int URI_PHOTO_ID = 1;

    static final String PHOTO_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PHOTO_TABLE;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(AUTHORITY, PHOTO_TABLE, URI_PHOTO_ID);
    }

    private PhotoDatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new PhotoDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = URI_MATCHER.match(uri);
        Cursor cursor;
        switch (match) {
            case URI_PHOTO_ID:
                cursor = dbHelper.getReadableDatabase().query(PHOTO_TABLE,
                        projection, selection, selectionArgs, null, null, sortOrder);
                final Context context = getContext();
                cursor.setNotificationUri((context == null ? null : context.getContentResolver()), PHOTO_CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("No such URI found: " + match);
        }
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_PHOTO_ID:
                return PHOTO_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("No such URI found: " + match);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri resultURI;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_PHOTO_ID:
                long rowID = dbHelper.getWritableDatabase().insert(PHOTO_TABLE, null, values);
                resultURI = ContentUris.withAppendedId(PHOTO_CONTENT_URI, rowID);
                final Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(resultURI, null);
                }
                break;
            default:
                throw new IllegalArgumentException("No such URI found: " + match);
        }
        return resultURI;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int result;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_PHOTO_ID:
                result = dbHelper.getWritableDatabase().delete(PHOTO_TABLE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("No such URI found: " + match);
        }
        final Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_PHOTO_ID:
                result = dbHelper.getWritableDatabase().update(PHOTO_TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("No such URI found: " + match);
        }
        final Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return result;
    }
}

package vn.ngh.epubreader.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class StringUtil {

    public static String getRealPathFromURI(Context context, Uri contentUri, String fileExtension) throws Exception {
        String path = null;
        Log.d("get real path", "path : " + contentUri.getPath());
        if (contentUri.getPath().lastIndexOf(fileExtension) > 0) {
            path = contentUri.getPath();
        } else {
            String[] proj = {MediaStore.MediaColumns.DATA};
            Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                path = cursor.getString(column_index);
            }
            cursor.close();
        }
        return path;
    }

}

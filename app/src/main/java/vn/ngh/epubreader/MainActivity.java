package vn.ngh.epubreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooser;
import vn.ngh.epubreader.core.EpubBook;
import vn.ngh.epubreader.core.EpubCommon;
import vn.ngh.epubreader.core.TableOfContent;

public class MainActivity extends AppCompatActivity {

    private static final String ROOT_FILE = "file://";

    private EpubCommon mEpubCommon;
    private EpubBook mBook = null;
    private TableOfContent mToc = null;
    private PrepareBookAsyncTask prepareBookTask;
    private WebView mWebView;
    private View mFrameLayout;
    private RecyclerView mRecyclerViewChapter;
    private ChapterAdapter mChapterAdapter;
    private DrawerLayout mDrawerLayout;

    private final static int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 13;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDefaultTextEncodingName("utf-8");
        webSetting.setBuiltInZoomControls(true);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setUseWideViewPort(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAllowFileAccessFromFileURLs(true);
        mWebView.clearCache(true);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setTag(null);
        mFrameLayout = (View) findViewById(R.id.frameLayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mRecyclerViewChapter = (RecyclerView) findViewById(R.id.recyclerview_chapter);
        mChapterAdapter = new ChapterAdapter(recyclerViewClickListener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewChapter.setLayoutManager(layoutManager);
        mRecyclerViewChapter.setAdapter(mChapterAdapter);


        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            addFileChooserFragment();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addFileChooserFragment();
            }
        }
    }

    private void addFileChooserFragment() {
        FileChooser.Builder builder = new FileChooser.Builder(FileChooser.ChooserType.FILE_CHOOSER,
                new FileChooser.ChooserListener() {
                    @Override
                    public void onSelect(String path) {
                        Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction()
                                .remove(getSupportFragmentManager().findFragmentById(R.id.frameLayout)).commit();
                        mBook = new EpubBook();
                        mBook.path = path;
                        if (mBook.path != null) {
                            prepareBookForReader();
                        }
                    }
                });
        try {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout, builder.build())
                    .commit();
        } catch (ExternalStorageNotAvailableException e) {
            Toast.makeText(this, "There is no external storage available on this device.",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadEpub2Webview() {
        if (mWebView != null) {
            mWebView.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.GONE);
            mWebView.loadUrl(ROOT_FILE + mEpubCommon.getBaseUrl());//loadDataWithBaseURL(ROOT_FILE + mEpubCommon.getBaseUrl(), "", "text/html", "utf-8", null);
            mChapterAdapter.addListTitle(mToc.getChapterList());
            mChapterAdapter.notifyDataSetChanged();
        }
    }

    private void loadEpubByUrl(String url) {
        Log.i(this.toString(), "load webview url: " + url);
        mWebView.loadUrl(ROOT_FILE + url);//.loadDataWithBaseURL(ROOT_FILE + url, "", "text/html", "utf-8", null);
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void doBack() {
        finish();
    }

    protected int getTotalBookPage() {
        if (mToc != null && mToc.getChapter(mToc.getTotalSize() - 1) != null) {
            return mToc.getChapter(mToc.getTotalSize() - 1).getEndPage();
        }
        return 0;
    }


    @SuppressLint("NewApi")
    protected void prepareBookForReader() {
        if (prepareBookTask != null
                && prepareBookTask.getStatus() == AsyncTask.Status.RUNNING) {
            prepareBookTask.cancel(true);
        }
        prepareBookTask = new PrepareBookAsyncTask(this);
        prepareBookTask.execute();//executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private RecyclerViewClickListener recyclerViewClickListener = new RecyclerViewClickListener() {
        @Override
        public void onClick(View view, int position) {
            TableOfContent.Chapter chapter = mToc.getChapterList().get(position);
            if (chapter != null) {
                String url = mEpubCommon.getBasePath() + "/" + chapter.getUrl();
                loadEpubByUrl(url);
                if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        }
    };


    private final class PrepareBookAsyncTask extends AsyncTask<Void, Void, Void> {
        protected WeakReference<MainActivity> readRfc;

        protected boolean isCancel = false;

        public PrepareBookAsyncTask(MainActivity mainActivity) {
            readRfc = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        protected Void doInBackground(Void... params) {
            MainActivity mainActivity = readRfc.get();

            // Prepare environment
            try {
                Log.d(this.toString(), "path: " + mainActivity.mBook.path);
                mainActivity.mEpubCommon = new EpubCommon(
                        mainActivity.mBook.path, null, false);


                // Get table of content
                // Parse table of content from file
                mBook = mainActivity.mEpubCommon.parseBookInfo();
                mainActivity.mToc = mainActivity.mEpubCommon.parseTableOfContent();
                if (mainActivity.mToc == null || mainActivity.mToc.getTotalSize() == 0) {
                    mainActivity.doBack();
                }

                // Check baseUrl
                String firstPath = mainActivity.mToc.getChapter(0).getUrl();
                Log.e(this.toString(), "firstPath in toc  " + firstPath);
                if (firstPath.indexOf("/") > 0) {
                    String baseUrl = mainActivity.mEpubCommon.getBaseUrl();
                    baseUrl = baseUrl.substring(baseUrl.length() - 1).equalsIgnoreCase("/") ? baseUrl : baseUrl + "/";
                    String pathOfChapter = firstPath.substring(0, firstPath.lastIndexOf("/"));
                    pathOfChapter = pathOfChapter.substring(0, 1)
                            .equalsIgnoreCase("/") ? pathOfChapter.substring(1)
                            : pathOfChapter;
                    mainActivity.mEpubCommon.setBaseUrl(baseUrl + pathOfChapter);
                    Log.d(this.toString(), "baseurl: " + mEpubCommon.getBaseUrl());
                } else {
                    mainActivity.mEpubCommon.setBaseUrl(mEpubCommon.getBaseUrl() + "/" + firstPath);
                    Log.d(this.toString(), "baseurl: " + mEpubCommon.getBaseUrl());
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainActivity.doBack();
            }


            // Update view count & visited time
            mainActivity.mBook.totalPage = mainActivity.getTotalBookPage();
            if (isCancelled()) {
                isCancel = true;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i(this.toString(), "PrepareBookAsyncTask onPreExecute");
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(this.toString(), "PrepareBookAsyncTask onPostExecute");
            MainActivity read = readRfc.get();
            if (read != null) {
                read.loadEpub2Webview();
            }
        }
    }
}

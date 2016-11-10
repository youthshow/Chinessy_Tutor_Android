package com.chinessy.tutor.android;

import android.app.ProgressDialog;
import android.net.http.SslError;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FAQActivity extends AppCompatActivity {
    final String tag = "FAQActivity";

    WebView mWvFaq;
    String mFAQUrl = "http://www.chinessy.com/faq";

    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.Waiting));
        mProgressDialog.show();

        mWvFaq = (WebView)findViewById(R.id.faq_wv_faq);
        mWvFaq.getSettings().setJavaScriptEnabled(true);
        mWvFaq.getSettings().setDomStorageEnabled(true);
        mWvFaq.loadUrl(mFAQUrl);
        mWvFaq.setWebViewClient(new FAQWebViewClient());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_faq, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            FAQActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private class FAQWebViewClient extends WebViewClient {
        @Override
        // 在WebView中而不是默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressDialog.dismiss();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            super.onReceivedError(view, errorCode, description, failingUrl);
//            Log.e(tag, errorCode + "");
//            Log.e(tag, description);
//        }
    }
}

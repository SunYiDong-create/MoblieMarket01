package MoblieMarket.com.MoblieMarket;

import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mob.MobSDK;

import java.io.Serializable;


import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cniao5.com.cniao5shop.R;
import MoblieMarket.com.MoblieMarket.bean.ShoppingCart;
import MoblieMarket.com.MoblieMarket.bean.Wares;
import MoblieMarket.com.MoblieMarket.utils.CartProvider;
import MoblieMarket.com.MoblieMarket.utils.ToastUtils;
import MoblieMarket.com.MoblieMarket.widget.CNiaoToolBar;
import dmax.dialog.SpotsDialog;

public class WareDetailActivity extends AppCompatActivity implements View.OnClickListener {


    @ViewInject(R.id.webView)
    private WebView mWebView;

    @ViewInject(R.id.toolbar)
    private CNiaoToolBar mToolBar;

    private Wares mWare;

    private WebAppInterface mAppInterfce;

    private CartProvider cartProvider;

    private SpotsDialog mDialog;

    public boolean granted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ware_detail);
        ViewUtils.inject(this);


        Serializable serializable = getIntent().getSerializableExtra(Contants.WARE);
        if (serializable == null)
            this.finish();


        mDialog = new SpotsDialog(this, "loading....");
        mDialog.show();


        mWare = (Wares) serializable;
        cartProvider = new CartProvider(this);

        initToolBar();
        initWebView();

    }


    private void initWebView() {

        WebSettings settings = mWebView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        settings.setAppCacheEnabled(true);


        mWebView.loadUrl(Contants.API.WARES_DETAIL);

        mAppInterfce = new WebAppInterface(this);
        mWebView.addJavascriptInterface(mAppInterfce, "appInterface");
        mWebView.setWebViewClient(new WC());


    }


    private void initToolBar() {


        mToolBar.setNavigationOnClickListener(this);
        mToolBar.setRightButtonText("分享");

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {

            private PlatformActionListener mPlatformActionListener;

            @Override
            public void onClick(View v) {
                MobSDK.submitPolicyGrantResult(granted, null);
                OnekeyShare oks = new OnekeyShare();
// title标题，微信、QQ和QQ空间等平台使用
                oks.setTitle(getString(R.string.share));
// titleUrl QQ和QQ空间跳转链接
                oks.setTitleUrl("http://www.cniao5.com");
// text是分享文本，所有平台都需要这个字段
                oks.setText(mWare.getName());
// setImageUrl是网络图片的url
                oks.setImageUrl(mWare.getImgUrl());
// url在微信、Facebook等平台中使用
                oks.setUrl("http://www.cniao5.com");
// 启动分享GUI
                oks.show(MobSDK.getContext());

                Platform plat = ShareSDK.getPlatform(QQ.NAME);
//移除授权状态和本地缓存，下次授权会重新授权
                plat.removeAccount(true);
//SSO授权，传false默认是客户端授权
                plat.SSOSetting(false);
//授权回调监听，监听oncomplete，onerror，oncancel三种状态
                plat.setPlatformActionListener(mPlatformActionListener);
//抖音登录适配安卓9.0
//ShareSDK.setActivity(MainActivity.this);
//要数据不要功能，主要体现在不会重复出现授权界面
                plat.showUser(null);
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onClick(View v) {
        this.finish();
    }


    class WC extends WebViewClient {


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);


            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();

            mAppInterfce.showDetail();


        }
    }


    class WebAppInterface {


        private Context mContext;

        public WebAppInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void showDetail() {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mWebView.loadUrl("javascript:showDetail(" + mWare.getId() + ")");

                }
            });
        }


        @JavascriptInterface
        public void buy(long id) {

            cartProvider.put((ShoppingCart) mWare);
            ToastUtils.show(mContext, "已添加到购物车");

        }

        @JavascriptInterface
        public void addFavorites(long id) {


        }

    }

}

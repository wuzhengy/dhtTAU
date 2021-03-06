package io.taucoin.torrent.publishing.ui.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.king.zxing.util.CodeUtils;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.taucoin.torrent.publishing.MainApplication;
import io.taucoin.torrent.publishing.R;
import io.taucoin.torrent.publishing.core.storage.sqlite.entity.User;
import io.taucoin.torrent.publishing.core.utils.ActivityUtil;
import io.taucoin.torrent.publishing.core.utils.SpanUtils;
import io.taucoin.torrent.publishing.core.utils.StringUtil;
import io.taucoin.torrent.publishing.core.utils.UsersUtil;
import io.taucoin.torrent.publishing.core.utils.Utils;
import io.taucoin.torrent.publishing.databinding.ActivityQrCodeBinding;
import io.taucoin.torrent.publishing.ui.BaseActivity;
import io.taucoin.torrent.publishing.ui.constant.IntentExtra;
import io.taucoin.torrent.publishing.ui.user.UserViewModel;

/**
 * 用户QR Code页面
 */
public class UserQRCodeActivity extends BaseActivity implements View.OnClickListener {

    private CompositeDisposable disposables = new CompositeDisposable();
    private ActivityQrCodeBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_code);
        binding.setListener(this);
        ViewModelProvider provider = new ViewModelProvider(this);
        userViewModel = provider.get(UserViewModel.class);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        String publicKey = MainApplication.getInstance().getPublicKey();
        if(StringUtil.isEmpty(publicKey)){
            return;
        }
        binding.toolbarInclude.toolbar.setNavigationIcon(R.mipmap.icon_back);
        binding.toolbarInclude.toolbar.setTitle(R.string.qr_code_title);
        binding.toolbarInclude.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.tvPublicKey.setText(publicKey);
        binding.roundButton.setBgColor(Utils.getGroupColor(publicKey));

        Bitmap bitmap = CodeUtils.createQRCode(publicKey, 480);
        binding.ivQrCode.setImageBitmap(bitmap);

        SpannableStringBuilder scanQrCode = new SpanUtils()
                .append(getString(R.string.qr_code_scan_friend_qr))
                .setUnderline()
                .create();
        binding.tvScanQrCode.setText(scanQrCode);

        disposables.add(userViewModel.observeCurrentUser()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    String showName = UsersUtil.getShowName(user);
                    binding.roundButton.setText(StringUtil.getFirstLettersOfName(showName));
                }));
    }

    /**
     * import seed和generate seed点击事件
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_scan_qr_code) {
            onBackPressed();
            Intent intent = new Intent();
            ActivityUtil.startActivity(intent, this, ScanQRCodeActivity.class);
        }
    }
}
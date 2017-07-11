package com.as.chain.util;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

import com.as.chain.R;
import com.as.chain.activity.BaseActivity;
import com.as.chain.ui.IDialogClickListener;
import com.as.chain.ui.SimpleDialog;
import com.stone.app.App;
import com.stone.app.Setting;
import com.stone.app.file.FileUtil;
import com.stone.archive.IArchive;
import com.stone.archive.IArchiveListener;
import com.stone.archive.Zip;
import com.stone.log.Logger;
import com.stone.thread.ThreadParams;
import com.stone.thread.ThreadUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;

public class DataMgr {
	public static final String TAG = DataMgr.class.getSimpleName();
	
	public static File getSrcDir() {
		Context context = App.getInstance().getContext();
		return new File(context.getFilesDir(), "src");
	}
	
	public static void checkLocalData() {
		final Context context = BaseActivity.getInstance();
	
		final SimpleDialog sd = new SimpleDialog(context);
		sd.setMessage(R.string.msg_local_data_checking);
		sd.setButtons(R.string.wd_confirm);
		sd.setCanceledOnTouchOutside(false);
		sd.setClickListener(new IDialogClickListener() {
			@Override
			public void onClick(Dialog dialog, int index, ClickType type) {
			}
		});
		sd.show();
		
		ThreadUtil.run(new ThreadParams(false, new Runnable() {
			@Override
			public void run() {
				try {
					AssetManager am = context.getAssets();
					InputStream is = am.open("version");
					Scanner scanner = new Scanner(is);
					final int asv = scanner.nextInt();
					scanner.close();
					
					int cuv = Setting.getInstance().getInt(Const.KEY_DATA_VERSION, 0);
					if (cuv >= asv) {
						sd.dismiss();
						return;
					}
					
					is = am.open("src.zip");
					
					File dir = getSrcDir();
					if (dir.exists()) {
						if (dir.isDirectory()) {
							FileUtil.clearDir(dir);
						} else {
							FileUtil.delete(dir);
							dir.mkdirs();
						}
					} else {
						dir.mkdirs();
					}
					
					Zip zip = new Zip();
					zip.setListener(new IArchiveListener() {
						@Override
						public void onProgress(IArchive archive, String from, String to) {
							sd.setMessage(String.format("%s\nTo\n%s", from, to));
						}
						
						@Override
						public void onFinish(IArchive archive) {
							Setting.getInstance().setInt(Const.KEY_DATA_VERSION, asv);
							sd.dismiss();
						}
						
						@Override
						public void onException(IArchive archive, Exception ex) {
							sd.setMessage(R.string.err_data_init_failed);
						}
					});
					zip.decompressSync(is, dir);
				} catch (Exception e) {
					Logger.print(TAG, e);
					
					sd.setMessage(R.string.err_data_init_failed);
				}
			}
		}));
	}
}

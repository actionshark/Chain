package com.as.chain.util;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

import com.as.chain.R;
import com.as.chain.activity.BaseActivity;
import com.as.chain.ui.IDialogClickListener;
import com.as.chain.ui.SimpleDialog;
import com.as.app.App;
import com.as.app.Setting;
import com.as.app.file.FileUtil;
import com.as.archive.IArchive;
import com.as.archive.IArchiveListener;
import com.as.archive.Zip;
import com.as.log.Logger;
import com.as.thread.ThreadParams;
import com.as.thread.ThreadUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;

public class DataMgr {
	public static final String TAG = DataMgr.class.getSimpleName();
	
	public static final File SRC_DIR;
	public static final String SRC_PATH;
	
	static {
		Context context = App.getInstance().getContext();
		SRC_DIR = new File(context.getFilesDir(), "src");
		SRC_PATH = SRC_DIR.getPath() + "/";
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
					
					File dir = SRC_DIR;
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

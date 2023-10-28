package com.example.clipimagedemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.jy.jyhy.GameUtil;
import com.jy.jyhy.MainGameActivity;
import com.kola.GameGloableData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 裁剪图片的Activity
 * 
 * @ClassName: CropImageActivity
 * @Description:
 * @author xiechengfa2000@163.com
 * @date 2015-5-8 下午3:39:22
 */
public class ClipImageActivity extends Activity implements OnClickListener {
	public static final String RESULT_PATH = "crop_image";
	private static final String KEY = "path";
	private ClipImageLayout mClipImageLayout = null;

	public static void startActivity(Activity activity, String path, int code) {
//		Intent intent = new Intent(activity, ClipImageActivity.class);
//		intent.putExtra(KEY, path);
//		activity.startActivityForResult(intent, code);

		
		Intent intent = new Intent(activity, ClipImageActivity.class);
		intent.putExtra(KEY, path);
		activity.startActivity(intent);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crop_image_layout);

		mClipImageLayout = (ClipImageLayout) findViewById(R.id.clipImageLayout);
		String path = getIntent().getStringExtra(KEY);

		// 有的系统返回的图片是旋转了，有的没有旋转，所以处理
		int degreee = readBitmapDegree(path);
		Bitmap bitmap = createBitmap(path);
		if (bitmap != null) {
			if (degreee == 0) {
				mClipImageLayout.setImageBitmap(bitmap);
			} else {
				mClipImageLayout.setImageBitmap(rotateBitmap(degreee, bitmap));
			}
		} else {
			finish();
		}
		findViewById(R.id.okBtn).setOnClickListener(this);
		findViewById(R.id.cancleBtn).setOnClickListener(this);
		
		MenuMainty.getIntance().finish();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub	
		
		boolean result = v.getId() == R.id.okBtn;
		if (result) {
			Bitmap bitmap = mClipImageLayout.clip();

//			String path = Environment.getExternalStorageDirectory() + "/"
//					+ MainActivity.TMP_PATH;
			
			
			Log.i("wwl", " 2222 file path = "+this.getFilesDir());
//			String path = this.getFilesDir() + "/"
//					+ MainActivity.TMP_PATH;
			
			String path = GameGloableData.GetHeadImgPath();
			
			
			Bitmap b = CompressBitmap(bitmap,64);
			saveBitmap(b, path);

			Intent intent = new Intent();
			intent.putExtra(RESULT_PATH, path);
			setResult(RESULT_OK, intent);
			
		}
		
		Bundle b = new Bundle();
		b.putString("success", String.valueOf(result));
		GameUtil.sendMessageToUnity3D("HandleHeadSelectMenu", b);
		finish();
	}

	private Bitmap CompressBitmap(Bitmap bmp, int width) {
		int height = width * bmp.getHeight() / bmp.getWidth();
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, width, height, true);
		return thumbBmp;
	}
	
	private void saveBitmap(Bitmap bitmap, String path) {
		Log.i("wwl", "save bitmap,path="+path);
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}
		
		FileOutputStream fOut = null;
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
			fOut = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 10, fOut);
			fOut.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fOut != null)
					fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建图片
	 * 
	 * @param path
	 * @return
	 */
	private Bitmap createBitmap(String path) {
		if (path == null) {
			return null;
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inPurgeable = true;
		FileInputStream is = null;
		Bitmap bitmap = null;
		try {
			is = new FileInputStream(path);
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	// 读取图像的旋转度
	private int readBitmapDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	// 旋转图片
	private Bitmap rotateBitmap(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		return resizedBitmap;
	}
}

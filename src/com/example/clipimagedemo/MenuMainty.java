package com.example.clipimagedemo;

import java.io.File;
import java.io.FileNotFoundException;

import com.kola.GameGloableData;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * @ClassName: MainActivity
 * @Description:
 * @author xiechengfa2000@163.com
 * @date 2015-5-10 ����11:14:08
 */
public class MenuMainty extends Activity implements OnClickListener {
	private final int START_ALBUM_REQUESTCODE = 1;
	private final int CAMERA_WITH_DATA = 2;
	private final int CROP_RESULT_CODE = 3;
	public static final String TMP_PATH = "clip_temp.jpg";
	
	private static MenuMainty intance;
	
	
	public static MenuMainty getIntance() {
		return intance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_head_menu);

		intance = this;
		
		
		findViewById(R.id.albumBtn).setOnClickListener(this);
		findViewById(R.id.captureBtn).setOnClickListener(this);
		findViewById(R.id.cancleMenuBtn).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (v.getId() == R.id.albumBtn) {
			startAlbum();
		} else if (v.getId() == R.id.captureBtn) {
			startCapture();
		}
		else if (v.getId() == R.id.cancleMenuBtn) {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// String result = null;
		
		Log.i("wwl","resultCode="+resultCode);
		
		if (resultCode != RESULT_OK) {
			finish();
			return;
		}

		switch (requestCode) {
		case CROP_RESULT_CODE:
//			String path = data.getStringExtra(ClipImageActivity.RESULT_PATH);
//			Bitmap photo = BitmapFactory.decodeFile(path);
//			ImageView imageView = (ImageView) findViewById(R.id.imageView);
//			imageView.setImageBitmap(photo);
			break;
		case START_ALBUM_REQUESTCODE:
			//startCropImageActivity(getFilePath(data.getData())); //wwl
			startCropImageActivity(handleImageOnKitKat(data));  //wwl
			break;
		case CAMERA_WITH_DATA:
			// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
//			startCropImageActivity(Environment.getExternalStorageDirectory()
//					+ "/" + TMP_PATH);
			
//			Log.i("wwl", "files path = "+this.getFilesDir());
//			startCropImageActivity(this.getFilesDir()+"/"+TMP_PATH);
			
			startCropImageActivity(GameGloableData.GetHeadImgPath());
			
			break;
			
		}
	}


	 // 4.4及以上系统使用这个方法处理图片 相册图片返回的不再是真实的Uri,而是分装过的Uri
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        Log.d("TAG", "imagePath= " + imagePath);
        return imagePath;
    }
	
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

	
	// 裁剪图片的Activity
	private void startCropImageActivity(String path) {
		ClipImageActivity.startActivity(MenuMainty.this, path, CROP_RESULT_CODE);
		//ClipImageActivity.startActivity(GameIn.get_intance(), path, CROP_RESULT_CODE);
	}

	private void startAlbum() {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			startActivityForResult(intent, START_ALBUM_REQUESTCODE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			try {
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, START_ALBUM_REQUESTCODE);
			} catch (Exception e2) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	private void startCapture() {
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
//				Environment.getExternalStorageDirectory(), TMP_PATH)));
//		startActivityForResult(intent, CAMERA_WITH_DATA);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				GameGloableData.getHeadImagSaveFolderPath(), GameGloableData.getHeadImagSaveFileName())));
		startActivityForResult(intent, CAMERA_WITH_DATA);
	}

	/**
	 * 通过uri获取文件路径
	 * 
	 * @param mUri
	 * @return
	 */
	public String getFilePath(Uri mUri) {
		try {
			if (mUri.getScheme().equals("file")) {
				return mUri.getPath();
			} else {
				return getFilePathByUri(mUri);
			}
		} catch (FileNotFoundException ex) {
			return null;
		}
	}

	// 获取文件路径通过url
	private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
		Cursor cursor = getContentResolver()
				.query(mUri, null, null, null, null);
		cursor.moveToFirst();
		return cursor.getString(1);
	}

}

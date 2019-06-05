package com.example.expresseeliverycheck.until;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Image Utilities。<br>
 */
public class ImageUtil {
	
	
	/** Decode a bitmap */
	public static Bitmap decodeBitmap(String path, double needSize) {
		Bitmap bitmap = null;
		// Decode image size
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bfo);
		// Calculate scale
		int scale = (int) Math.round(Math.sqrt(bfo.outHeight * bfo.outWidth) / needSize);
		if (scale < 1) {
			scale = 1;
		}
		// Decode with inSampleSize
		BitmapFactory.Options resize = new BitmapFactory.Options();
		resize.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(path, resize);
		return bitmap;
	}

	/** Decode a bitmap */
	public static Bitmap decodeBitmap(String path, int dimension) {

		int degree = ImageUtil.readPictureDegree(path);
		Bitmap bitmap = null;
		// Decode image size
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bfo);
		// Calculate scale
		int scale = 0;
		if (degree % 180 != 0) {
			scale = Math.round(bfo.outHeight / dimension);
		} else {
			scale = Math.round(bfo.outWidth / dimension);
		}
		// Decode with inSampleSize
		BitmapFactory.Options resize = new BitmapFactory.Options();
		resize.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(path, resize);
		Bitmap bmp = ImageUtil.postRotate(bitmap, degree);
		return bmp;
	}
	
	/**
	 * 图片转字节数组
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	/**
	 * Binaryでイメージオブジェクトを取得する。<BR>
	 *
	 * @author zhaosx
	 * @param param
	 *            Binaryオブジェクト
	 * @return Bitmap メージオブジェクト
	 */
	public static Bitmap getBitmapFromByte(byte[] param) {
		if (param != null && param.length > 0) {
			return BitmapFactory.decodeByteArray(param, 0, param.length);
		}
		return null;
	}

	/**
	 * イメージオブジェクトでBinaryを取得する。<BR>
	 *
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		byte[] result = null;
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, os);
			result = os.toByteArray();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					Log.e("AgentUtils", "bitmapToBytes", e);
				}
			}
		}
		return result;
	}

	/**
	 * ストリングはビットマップに転換する.
	 *
	 * @return　ビットマップ
	 */
	public static Bitmap stringToBitmap(String string) {
		byte[] bytes = Base64.decode(string, Base64.DEFAULT);
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	public static Bitmap zoomImg(Bitmap bm, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		Bitmap result = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		return result;
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = null;
		ByteArrayInputStream isBm = null;
		Bitmap bitmap = null;
		try {
			baos = new ByteArrayOutputStream();
			image.compress(CompressFormat.JPEG, 100, baos);
			int options = 100;
			while (baos.toByteArray().length / 1024 > 100) {
				baos.reset();
				image.compress(CompressFormat.JPEG, options, baos);
				options -= 10;
			}
			isBm = new ByteArrayInputStream(baos.toByteArray());
			bitmap = BitmapFactory.decodeStream(isBm, null, null);
			baos.close();
			isBm.close();
		} catch (IOException e) {
			Log.e("AgentUtils", "compressImage", e);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Log.e("AgentUtils", "compressImage", e);
				}
			}
			if (isBm != null) {
				try {
					isBm.close();
				} catch (IOException e) {
					Log.e("AgentUtils", "compressImage", e);
				}
			}
		}
		return bitmap;
	}
	/**
	 * 保存压缩图片
	 *
	 * @param filePath
	 * @return
	 */
	public static Bitmap savePhoto(String filePath, double size) {
		// 图片压缩
		Bitmap resizedBmp = getResizedBitmap(filePath, (int) size);
		if (null == resizedBmp) {
			return null;
		}
		// 图片旋转
		Bitmap newBmp = ImageUtil.postRotate(resizedBmp, ImageUtil.readPictureDegree(filePath));
		resizedBmp = null;
		return newBmp;
	}

	/**
	 * 获取等比例压缩后的图片
	 *
	 * @param filePath
	 * @return
	 */
	public static Bitmap getResizedBitmap(String filePath, int maxLength) {
		// 获取原始图片的宽高
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 不读入内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		int height = options.outHeight;
		int width = options.outWidth;
		// 根据最长边的大小计算实际输出大小
		int reqWidth = 0;
		int reqHeight = 0;
		float scal = height / (float) width;
		float scal2 = width / (float) height;
		if (scal > scal2) {
			if (height > maxLength) {
				reqHeight = maxLength;
				reqWidth = (int) (reqHeight / scal);
			} else {
				reqHeight = height;
				reqWidth = width;
			}
		} else {
			if (width > maxLength) {
				reqWidth = maxLength;
				reqHeight = (int) (reqWidth / scal2);
			} else {
				reqWidth = width;
				reqHeight = height;
			}
		}
		// 压缩比例
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// 在内存中创建Bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 获取Bitmap实际大小
	 *
	 * @param originalBmp
	 */
	public static void getBitmapSize(Bitmap originalBmp, String path) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		originalBmp.compress(CompressFormat.JPEG, 100, os);
		byte[] bytes = os.toByteArray();
		File mPhotoFile = getFileFromBytes(bytes, path);
		FileInputStream fs;
		try {
			fs = new FileInputStream(mPhotoFile);
			int length;
			try {
				length = fs.available();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/** Get file from Bytes[] */
	public static File getFileFromBytes(byte[] btyes, String outputFile) {
		File file = null;
		BufferedOutputStream stream = null;
		FileOutputStream fstream = null;
		try {
			file = new File(outputFile);
			fstream = new FileOutputStream(file);

			stream = new BufferedOutputStream(fstream);
			stream.write(btyes);
			stream.close();
			fstream.close();
		} catch (FileNotFoundException e) {
			Log.e("AgentUtils", "getFileFromBytes", e);
		} catch (IOException e) {
			Log.e("AgentUtils", "getFileFromBytes", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					Log.e("AgentUtils", "getFileFromBytes", e);
				}
			}
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e) {
					Log.e("AgentUtils", "getFileFromBytes", e);
				}
			}
		}
		return file;
	}

	/**
	 * Get round corners without background.
	 *
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmapNoBackground(Bitmap bitmap,
                                                            float roundPx) {
		return getRoundedCornerBitmap(bitmap, roundPx);
	}

	/**
	 * Get round corners
	 *
	 * @param bitmap
	 *            Orgin image
	 * @param roundPx
	 * @return
	 */
	protected static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * Get round corners with background.
	 *
	 * @param width
	 * @param height
	 * @param roundPx
	 * @param color
	 * @return
	 */
	protected static Bitmap getRoundedCornerBitmapWithBackground(int width,
                                                                 int height, float roundPx, int color) {
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		return output;
	}

	/**
	 * Get round corners with background.
	 *
	 * @param bitmap
	 * @param color
	 * @param intervalPx
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmapWithBackground(Bitmap bitmap,
                                                              int color, int intervalPx, float roundPx) {
		Bitmap temp = getRoundedCornerBitmap(bitmap, roundPx);
		Bitmap output = Bitmap.createBitmap(temp.getWidth() + intervalPx * 2,
				temp.getHeight() + intervalPx * 2, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());
		final RectF rectF = new RectF(rect);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		canvas.drawBitmap(temp, intervalPx, intervalPx, null);
		return output;
	}

	public static Bitmap getRightTopRoundedCornerBitmap(Bitmap bitmap,
                                                        float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(-20, 0, bitmap.getWidth(),
				bitmap.getHeight() + 20);

		final Paint paint = new Paint();
		int color = 0xff424242;
		paint.setColor(color);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap getRightBottomRoundedCornerBitmap(Bitmap bitmap,
                                                           float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(-20, -20, bitmap.getWidth(),
				bitmap.getHeight());

		final Paint paint = new Paint();
		int color = 0xff424242;
		paint.setColor(color);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * Resize bitmap.
	 *
	 * @param bitmap
	 * @param newHeight
	 * @param newWidth
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, double newHeight,
                                      double newWidth) {
		Bitmap result = bitmap;
		if (newHeight > 0 && newWidth > 0) {
			int bmpHeight = bitmap.getHeight();
			int bmpWidth = bitmap.getWidth();
			if (bmpHeight > newWidth || bmpWidth > newHeight) {
				DecimalFormat df2 = new DecimalFormat("###.000");
				float wScale = Float.parseFloat(df2.format(newHeight
						/ bmpWidth));
				float hScale = Float.parseFloat(df2.format(newWidth
						/ bmpHeight));
				Matrix matrix = new Matrix();
				matrix.postScale(Math.min(wScale, hScale),
						Math.min(wScale, hScale));

				result = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight,
						matrix, true);
			}
		}
		return result;
	}

	/**
	 * Crop Bitmap.
	 *
	 * @param bmp
	 * @return
	 */
	public static Bitmap cropBitmap(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int sc = width > height ? height : width;
		int retX = width > height ? (width - height) / 2 : 0;
		int retY = width > height ? 0 : (height - width) / 2;
		return Bitmap.createBitmap(bmp, retX, retY, sc, sc, null, false);
	}

	/**
	 * Load Bitmap
	 *
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap loadBitmap(File file) throws FileNotFoundException {
		BitmapFactory.Options bitopt = new BitmapFactory.Options();
		bitopt.inSampleSize = 1;
		FileInputStream fis = new FileInputStream(file);
		Bitmap bm = BitmapFactory.decodeStream(fis);
		try {
			fis.close();
		} catch (IOException e) {
			Log.e("ImageUtil", "loadBitmap", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					Log.e("ImageUtil", "loadBitmap", e);
				}
			}
		}
		return bm;
	}

	/**
	 * Make canescent Bitmap.
	 *
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toGrayBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap grayImg = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(grayImg);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		float contrast = 0.7f;
		float brightness = -25;
		cm.set(new float[] { contrast, 0, 0, 0, brightness, 0, contrast, 0, 0,
				brightness, 0, 0, contrast, 0, brightness, 0, 0, 0, contrast, 0 });
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return grayImg;
	}

	/**
	 * Drawable に　bitmapを転換する
	 *
	 * @param resources
	 * @param drawable
	 * @return
	 */
	public static Bitmap toDrawable(Resources resources, int drawable) {
		InputStream is = null;
		Bitmap mBitmap = null;
		try {
			is = resources.openRawResource(drawable);
			mBitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			Log.e("ImageUtil", "toDrawable", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e("ImageUtil", "toDrawable", e);
				}
			}
		}
		return mBitmap;
	}

	public static Bitmap reSizeBitmap(String filePath) {
		Bitmap bm = getSmallBitmap(filePath);
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.JPEG, 100, baos);
			baos.close();
		} catch (IOException e) {
			Log.e("ImageUtil", "reSizeBitmap", e);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Log.e("ImageUtil", "reSizeBitmap", e);
				}
			}
		}
		return bm;
	}

	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		int width = options.outWidth;
		int height = options.outHeight;
		int reqWidth = 0;
		int reqHeight = 0;
		float scal = height / (float) width;
		float scal2 = width / (float) height;
		if (scal > scal2) {
			if (height > 720) {
				reqHeight = 720;
				reqWidth = (int) (reqHeight / scal);
			} else {
				reqHeight = height;
				reqWidth = width;
			}
		} else {
			if (width > 720) {
				reqWidth = 720;
				reqHeight = (int) (reqWidth / scal2);
			} else {
				reqWidth = width;
				reqHeight = height;
			}
		}
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	public static int readPictureDegree(String path) {
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
			default:
				degree = 0;
				break;
			}
		} catch (IOException e) {
			Log.e("ImageUtil", "readPictureDegree", e);
		}
		return degree;
	}

	public static Bitmap postRotate(Bitmap bmp, float degree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix,
				true);
		return bitmap;
    }

    /**
     * Free up the memory
     */
    public static void recycleBitmapDrawable(Drawable d) {
        if (d != null) {
            if (d instanceof BitmapDrawable) {
                BitmapDrawable bd = (BitmapDrawable)d;
                bd.getBitmap().recycle();
                bd = null;
            }
        }
    }

    /**
     * Sets a Bitmap as the content of this ImageView.
     * 
     * @param iv The ImageView to set
     * @param bm The bitmap to set
     */
    public static void setImageBitmap(ImageView iv, Bitmap bm) {
        if (iv != null && bm != null) {
            recycleBitmapDrawable(iv.getDrawable());
            iv.setImageBitmap(bm);
        }
    }

    /**
     * Set the background to a given Drawable, or remove the background.
     * 
     * @param iv The ImageView to set
     * @param bm The bitmap to set
     */
    public static void setBackgroundDrawable(ImageView iv, Bitmap bm) {
        if (iv != null && bm != null) {
            recycleBitmapDrawable(iv.getBackground());
            iv.setBackgroundDrawable(new BitmapDrawable(bm));
        }
    }

    /**
     * Set the background to a given resource. The resource should refer to a
     * Drawable object or 0 to remove the background.
     * 
     * @param iv The ImageView to set
     * @param resid The identifier of the resource.
     */
    public static void setBackgroundResource(ImageView iv, int resid) {
        if (iv != null && resid > 0) {
            recycleBitmapDrawable(iv.getBackground());
            iv.setBackgroundResource(resid);
        }
    }
    
    
    public static int dp2px(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
}

package jp.co.recruit.mtl.nfc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateProfileImageTask extends AsyncTask<Bitmap, Void, Boolean> {  
	private static final String consumerKey = "hoge";
	private static final String consumerSecret = "hogehoge";
	private static final String accessToken = "Foo";
	private static final String accessTokenSecret = "Bar";
	
	private static final String TAG = "UpdateProfileImageTask";
    // バックグラウンドで実行する処理  
    @Override  
    protected Boolean doInBackground(Bitmap... bmp) {  
    	Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		twitter.setOAuthAccessToken(new AccessToken(accessToken,
				accessTokenSecret));

		//リソースに入ってる画像をストリームに変換するごにょごにょ
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp[0].compress(Bitmap.CompressFormat.PNG, 50, bos);
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

		//4XXや5XXでても3回まではリトライする
		int continuousErrorCount = 0;
		while(true){
			try {
				Log.i(TAG, "START.");
				//やりたいのはこれだけ。アイコン画像のアップロード
				twitter.updateProfileImage(bis);
			} catch (TwitterException e) {
				Integer errorCode = e.getStatusCode();
				if(errorCode.toString().startsWith("5") || errorCode.toString().startsWith("4")){
					continuousErrorCount++;
					if(continuousErrorCount < 4){
						Log.e(TAG, "STATUS CODE is 4XX , 5XX. Try " + continuousErrorCount + "times.");
						continue;
					}else{
						//リトライ4回目で終了（もう無理あきらめる）
						Log.e(TAG, "STATUS CODE is 4XX , 5XX. Try " + continuousErrorCount + "times.");
						return false;
					}
				}else{
					//STATUS CODE = 3XX , 2XXのときはリトライなしで終了
					Log.e(TAG, "STATUS CODE is 3XX , 2XX. Try " + continuousErrorCount + "times.");
					return false;
				}
			}
			//成功したら終了
			Log.i(TAG, "SUCCESS. Try " + continuousErrorCount + "times.");
			break;
		}
		
		try {
			bis.close();
			bos.close();
		} catch (IOException e3) {
			e3.printStackTrace();
		}

    	return true;
    }
}  
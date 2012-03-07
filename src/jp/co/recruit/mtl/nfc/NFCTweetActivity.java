package jp.co.recruit.mtl.nfc;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

public class NFCTweetActivity extends Activity {
	private String TAG = "NFCTweet";
	
	//各自のNFCタグのIDを設定
	private static final String SUICA_ID = "11410f7ede17";
	private static final String EMP_ID = "11614ebe43a";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String action = getIntent().getAction();

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

			//NFCのIDを取得する
			String id = getNfcId();

			//リソースから画像を読み込む
			Resources r = getResources();
			int image = R.drawable.i2key;
			if (SUICA_ID.equals(id)) {
				// SUICA(IDべた打ち)
				image = R.drawable.i2key_train;
			} else if(EMP_ID.equals(id)) {
				// 社員証(IDべた打ち)
				image = R.drawable.i2key_work;
			}
			Bitmap bmp = BitmapFactory.decodeResource(r, image);
			
			//Twitter4Jを使うロジックはGUIスレッドではなく非同期で別スレッドで実行
			UpdateProfileImageTask task = new UpdateProfileImageTask();
			task.execute(bmp);
		}
	}
	
	/**
	 * NFCからIDを抽出する
	 * @return
	 */
	private String getNfcId(){
		byte[] rawIds = getIntent().getByteArrayExtra(NfcAdapter.EXTRA_ID);
		String id = "";
		StringBuffer idByte = new StringBuffer();
		if (rawIds != null) {
			for (int i = 0; i < rawIds.length; i++) {
				Log.i(TAG,
						"rawsIDs[" + i + "]="
								+ Integer.toHexString(rawIds[i] & 0xff));
				idByte.append(Integer.toHexString(rawIds[i] & 0xff));
			}
			id = idByte.toString();
			//Toast.makeText(this, "id:" + id, Toast.LENGTH_LONG).show();
		} else {
			// Unknown tag type
			Log.i(TAG, "unknown tag type#ID");
		}
		return id;
	}
}
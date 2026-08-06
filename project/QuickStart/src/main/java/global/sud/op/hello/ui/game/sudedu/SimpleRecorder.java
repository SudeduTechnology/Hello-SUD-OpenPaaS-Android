package global.sud.op.hello.ui.game.sudedu;

import android.content.Context;
import android.media.MediaRecorder;

import java.io.File;

public class SimpleRecorder {
    private static boolean _isRecording=false;
    private static MediaRecorder mediaRecorder;
    private static String storageFilePath = null;

    public static void startRecording(Context context) {
        stopRecording();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        mediaRecorder.setAudioChannels(1);
//        // 设置录音文件的清晰度
//        mediaRecorder.setAudioSamplingRate(44100);
//        mediaRecorder.setAudioEncodingBitRate(192000);

        // 设置为WAV格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 使用AMR_NB编码器，这是WAV常用的编码器
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioSamplingRate(8000);
        mediaRecorder.setAudioEncodingBitRate(12200);

        String path = context.getExternalFilesDir(null) + "/records/";
        // storage/emulated/0/Android/data/包名/files/records/temp.mp4
        long timestamp = System.currentTimeMillis();
        storageFilePath = path + timestamp + ".wav";
        File dir = new File(path);
        dir.mkdir();

        try {
            mediaRecorder.setOutputFile(storageFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            _isRecording = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String stopRecording() {
        try {
            boolean __isRecording=_isRecording;
            _isRecording=false;
            if (__isRecording && (mediaRecorder != null)) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                return storageFilePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isRecording(){
        return _isRecording;
    }

    public static String getOutputFilePath() {
        return storageFilePath;
    }


}

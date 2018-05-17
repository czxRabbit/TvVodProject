package com.ts.cyd.tsreplay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Handler;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.id.message;
import static android.R.id.progress;

/**
 * Created by Administrator on 2017/7/11.
 */

public class Update{
    private static final String TAG = "UpdateManager";
    private static final int DOWNLOAD = 1;

    private static final int TYPE_FALIED = 3;
    private static final int TYPE_SUCCESS=4;
    private String Updateurl="http://tv.com/app/iptv_replay.apk";
    private final String VersionUrl ="http://tv.com/app/version.txt";
    private Context mContext;
    private int  newVersion=0;
    private int nowversion=0;
    private int progress;
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private android.os.Handler mHandler = new android.os.Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD:
                    progress=msg.arg1;
                    int lastProgress=0;
                    if (lastProgress <progress) {
                        lastProgress =progress;
                        mProgress.setProgress(lastProgress);
                        if(lastProgress == 100 )
                        {
                         mDownloadDialog.dismiss();
                        }
                    }
                    break;

                case TYPE_FALIED:
                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                    mDownloadDialog.dismiss();
                    break;
                case TYPE_SUCCESS:
                    String filename = Updateurl.substring(Updateurl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    Log.d(TAG,"File Open"+directory+filename);
                    File file = new File(directory + filename);
                    openFile(file);


            }
        }


    };


    public Update(Context context)
    {
        this.mContext = context;

    }



    private void openFile(File file) {
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    private void openFile(String downloadUrl)
    {
        Log.d(TAG,"openFile");
        String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File(directory + filename);
        openFile(file);
    }

    public void checkUpdate()
    {


        nowversion=getPackageInfo(mContext).versionCode;
        newVersion=getversioncode(VersionUrl);
        if (newVersion > nowversion)

        {  // 显示提示对话框
            showNoticeDialog();
        }

    }

    private void download(String downloadUrl) {
        try {
            Log.e(TAG, "download");
            URL url = new URL(downloadUrl);
            //打开连接
            URLConnection conn = url.openConnection();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            int contentLength = conn.getContentLength();
            Log.e(TAG, "contentLength = " + contentLength);
            //创建文件夹 MyDownLoad，在存储卡下
            String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File file = new File(directory + filename);
            //不存在创建

            if (file.exists()) {
                file.delete();
            }
            //创建字节流
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(file);
            //写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            //完成后关闭流
            Log.e(TAG, "download-finish");
            os.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int  getversioncode(String versionUrl)
    {
        String fileName =versionUrl.substring(versionUrl.lastIndexOf("/"));
        String  directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File (directory + fileName);
        int newversion = 0;
        Log.d(TAG, "getversioncode: ");

        if(file.exists())
        {
            try{
//
                BufferedReader bre =new BufferedReader(new FileReader(file));
                String str;
                while ((str = bre.readLine())!= null) // 判断最后一行不存在，为空结束循环
                {
                    newversion=Integer.parseInt(str);  ;//原样输出读到的内容
                    Log.d(TAG, "得到新版本号:" + newversion);
                };

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return   newversion;
        }
        return newversion;


    }


    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return pi;
    }
    private void showNoticeDialog()
    {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("发现更新，是否下载更新?");
        // 更新
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                // 显示下载对话框
                showDownloadDialog();
            }
        });
        // 稍后更新
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }
    private void showDownloadDialog()
    {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请勿操作，耐心等待，更新中.... ");
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress,null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        // 取消更新
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        mDownloadDialog.setCancelable(false);
        // 现在文件
        downloadApk();
    }
    private void downloadApk()
    {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    private class downloadApkThread extends Thread
    {
        @Override
        public void run()
        {
            String downloadUrl = Updateurl;
            File file  =null;
            try{ URL url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                if(is == null)
                {
                    mHandler.sendEmptyMessage(TYPE_FALIED);
                }
                else {

                    String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    Log.d(TAG, "File" + directory + filename);
                    file = new File(directory + filename);
                    if (file.exists()) {
                        file.delete();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    int count = 0;
                    byte buf[] = new byte[1024];
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        Message msg = new Message();

                        progress = (int) (((float) count / length) * 100);
                        msg.what = DOWNLOAD;
                        msg.arg1 = progress;
                        // 更新进度
                        mHandler.sendMessage(msg);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(TYPE_SUCCESS);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (true);// 点击取消就停止下载.
                    fos.close();
                    is.close();

                }
            }
            catch (MalformedURLException e)
            {

                e.printStackTrace();
                mHandler.sendEmptyMessage(TYPE_FALIED);
            } catch (IOException e)
            {
                e.printStackTrace();
                mHandler.sendEmptyMessage(TYPE_FALIED);
            }
            mDownloadDialog.dismiss();

        }
    }
    private long getContentLength(String downloadurl) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request =new Request.Builder()
                .url(downloadurl)
                .build();
        Response response = client.newCall(request).execute();
        if(response != null && response.isSuccessful())
        {
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return  0 ;
    }



}

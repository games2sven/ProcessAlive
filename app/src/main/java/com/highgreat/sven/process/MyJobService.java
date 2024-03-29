package com.highgreat.sven.process;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import java.util.List;

@SuppressLint("NewApi")
public class MyJobService extends JobService{

    private static final String TAG = "MyJobService";

    public static void startJob(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(10,new ComponentName(context.getPackageName(),
                MyJobService.class.getName())).setPersisted(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            //7.0以上延迟1s执行
            builder.setMinimumLatency(1000);
        }else{
            //每隔1s执行一次job
            builder.setPeriodic(1000);
        }
        jobScheduler.schedule(builder.build());
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        //7.0以上轮询
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startJob(this);
        }
        //判断服务是否在运行
        boolean isLocalServiceRun = isServiceRunning(this, LocalService.class.getName());
        boolean isRemoteServiceRun = isServiceRunning(this, RemoteService.class.getName());
        if (!isLocalServiceRun || !isRemoteServiceRun) {
            startService(new Intent(this, LocalService.class));
            startService(new Intent(this, RemoteService.class));
        }

        return false;
    }

    private boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(10);
        for(ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfos){
            if(TextUtils.equals(runningServiceInfo.service.getClassName(),serviceName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}

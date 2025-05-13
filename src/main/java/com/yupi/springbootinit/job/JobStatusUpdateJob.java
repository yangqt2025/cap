package com.yupi.springbootinit.job;

import com.yupi.springbootinit.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.HashMap;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 岗位状态更新定时任务
 */
@Component
@Slf4j
public class JobStatusUpdateJob {

    @Resource
    private JobService jobService;

    /**
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateJobStatus() {
        log.info("开始更新岗位报名状态");
        try {
            jobService.updateRegistrationStatus();
            log.info("岗位报名状态更新完成");
        } catch (Exception e) {
            log.error("岗位报名状态更新失败", e);
        }
    }


    public static String subString(String s){
        if(s==null){
            return null;
        }
        int n = s.length();
        int ans = 0;
        Map<Character,Integer> map =new HashMap<>();
        int start=0;
        int maxStart=0;
        int maxLen=0;
        for(int end=0;end<n;end++){
            if(map.containsKey(s.charAt(end))){
                start = Math.max(map.get(s.charAt(end)),start);
            }
            int cur = end-start+1;
            if(cur>maxStart){
                maxLen = cur;
                maxStart =start ;
            }
            map.put(s.charAt(end),end+1);
        }
        return s.substring(maxStart,maxLen+maxStart);
    }

    public static void main(String[] args) {
        //无重复字符的最长子串
        String s = null;
        System.out.println(subString(s));







    }



}







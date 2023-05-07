package com.shier.feign.config;

import feign.Logger;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Shier
 * CreateTime 2023/4/17 17:14
 */
@FeignClient(value = "userservice")
public class FeignClientConfiguration {
    public Logger.Level logLevel(){
        return Logger.Level.BASIC;
    }
}

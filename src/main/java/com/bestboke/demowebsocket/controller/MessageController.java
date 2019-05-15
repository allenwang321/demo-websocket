package com.bestboke.demowebsocket.controller;

import com.alibaba.fastjson.JSONObject;
import com.bestboke.demowebsocket.config.SnowFlake;
import com.bestboke.demowebsocket.pojo.Message;
import com.bestboke.demowebsocket.result.Result;
import com.bestboke.demowebsocket.result.ResultUtil;
import com.bestboke.demowebsocket.utils.EmojiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("message")
public class MessageController {

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping("send")
    @ResponseBody
    public Result receiveMessage(@RequestParam("message") String message){
        Message messagePojo = new Message();
        messagePojo.setMessage(EmojiUtils.emojiConvert(message));
        messagePojo.setTime(new Date());
        messagePojo.setId(SnowFlake.instant().nextId());
        redisTemplate.convertAndSend("test", JSONObject.toJSONString(messagePojo));
        return ResultUtil.success();
    }

}

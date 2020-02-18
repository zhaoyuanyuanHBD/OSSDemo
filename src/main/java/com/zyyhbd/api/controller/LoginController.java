package com.zyyhbd.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zyyhbd.api.util.HttpRequestUtil;

import net.sf.json.JSONObject;


@Controller
public class LoginController {

	@RequestMapping(value = "/callback")
    @ResponseBody
    public String callback(HttpServletRequest request,
                           HttpServletResponse respons,
                           HttpSession session) throws  Exception{

        String code = request.getParameter("code");

        String tokenUrl = "http://47.96.187.200/profile/oauth2/accessToken?";

        String tokenParam = HttpRequestUtil.getAccessTokenParam("dEAGYWIJZd", "e58dbbd0-cb2a-49ee-97b6-45d5d6ac01fe",
                "http://127.0.0.1:8088/callback", code);

        String token = HttpRequestUtil.getResult(tokenUrl, tokenParam);

        JSONObject jsonObject = JSONObject.fromObject(token);

        String userUrl = "http://47.96.187.200/profile/oauth2/profile?";

        String userParam = HttpRequestUtil.getUserParam("dEAGYWIJZd",
                "e58dbbd0-cb2a-49ee-97b6-45d5d6ac01fe",
                jsonObject.getString("access_token"));


        String user = HttpRequestUtil.getResult(userUrl, userParam);

        jsonObject = JSONObject.fromObject(user);
        
        
        session.setAttribute("userId",jsonObject.getString("id"));

        return user;
    }
	
	@RequestMapping("/login")
	@ResponseBody
	public String login(@RequestParam("username")String username, @RequestParam("password")String password) {
		return username + " ------ " + password;
	}
	
}

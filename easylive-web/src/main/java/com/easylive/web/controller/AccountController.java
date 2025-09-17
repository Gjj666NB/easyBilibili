package com.easylive.web.controller;

import com.easylive.component.RedisComponent;
import com.easylive.constants.Constants;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.exception.BusinessException;
import com.easylive.redis.RedisConfig;
import com.easylive.redis.RedisUtils;
import com.easylive.service.UserInfoService;
import com.easylive.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashMap;

@RestController
@RequestMapping("/account")
public class AccountController  extends ABaseController{
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisComponent redisComponent;

    @RequestMapping("/checkCode")
    public ResponseVO getCheckCode(HttpSession session){
        // 生成图片验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 40);
        //获取结果
        String code = captcha.text();
        //把验证码保存到redis中
        String checkCodeKey = redisComponent.saveCheckCode(code);
        // 将图片验证码base64返回前端
        String Checkbase64 = captcha.toBase64();

        HashMap<String, String> map = new HashMap<>();
        map.put("checkCode", Checkbase64);
        map.put("checkCodeKey", checkCodeKey);
        return getSuccessResponseVO(map);
    }

    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty @Email @Size(max = 150) String email,
                               @NotEmpty @Size(max = 30) String nickName,
                               @NotEmpty @Pattern(regexp = Constants.PASSWORD_REGEX) String registerPassword,
                               @NotEmpty String checkCode,
                               @NotEmpty String checkCodeKey) {
        //判断验证码
        if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))){
            throw  new BusinessException("验证码错误");
        }
        userInfoService.register(email,nickName,registerPassword);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/login")
    public ResponseVO login( HttpServletRequest request,
                             HttpServletResponse response,
                             @NotEmpty @Email String email,
                             @NotEmpty String password,
                             @NotEmpty String checkCode,
                             @NotEmpty String checkCodeKey) {
        try {
            //判断验证码
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))){
                throw  new BusinessException("验证码错误");
            }
            String ip = getIpAddr();
            TokenUserInfoDto tokenUserInfoDto = userInfoService.login(email,password,ip);
            saveToken2Cookie(response, tokenUserInfoDto.getToken());
            return getSuccessResponseVO(tokenUserInfoDto);
        }finally {
            // 清除redis中的验证码
            redisComponent.cleanCheckCode(checkCodeKey);
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                String token = null;
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(Constants.WEB_TOKEN)) {
                        token = cookie.getValue();
                        break;
                    }
                }
                if (!StringTools.isEmpty(token)) {
                    redisComponent.cleanTokenInfo4Web(token);
                }
            }
        }
    }

    /**
     * 自动登录
     */
    @RequestMapping("/autoLogin")
    public ResponseVO autoLogin(HttpServletResponse response){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
            if (tokenUserInfoDto == null){
                return getSuccessResponseVO( null);
            }

            if (tokenUserInfoDto.getExpireTime()-System.currentTimeMillis() < Constants.TIME_SECONDS_DAY){
                redisComponent.saveTokenInfo4Web(tokenUserInfoDto);
                saveToken2Cookie(response, tokenUserInfoDto.getToken());
            }
        return getSuccessResponseVO(tokenUserInfoDto);
    }

    //http://localhost:7071/account/logout
    @RequestMapping("/loginOut")
    public ResponseVO logout(HttpServletResponse response){
        cleanCookie(response);
        return getSuccessResponseVO(null);
    }
}

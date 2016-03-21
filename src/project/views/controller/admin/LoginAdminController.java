package project.views.controller.admin;

import static drizzt.common.constants.CommonConstants.COOKIES_USER;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import project.common.tools.UserUtils;
import project.entities.log.LogLogin;
import project.entities.sys.SysUser;
import project.logic.component.CacheComponent;
import project.logic.service.log.LogLoginService;
import project.logic.service.sys.SysUserService;
import drizzt.common.base.BaseController;
import drizzt.common.tools.VerificationUtils;

@Controller
public class LoginAdminController extends BaseController {
    @Autowired
    private SysUserService service;
    @Autowired
    private CacheComponent cacheComponent;
    @Autowired
    private LogLoginService logLoginService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(String username, String password, String returnUrl, HttpServletRequest request, HttpSession session,
            HttpServletResponse response, ModelMap model) {
        if (virifyNotEmpty("username", username, model) || virifyNotEmpty("password", password, model)) {
            model.addAttribute("username", username);
            model.addAttribute("returnUrl", returnUrl);
            return "login";
        }
        SysUser user = service.findByName(username);
        if (virifyNotExist("username", user, model)
                || virifyNotEquals("password", VerificationUtils.encode(password), user.getPassword(), model)
                || virifyNotAdmin(user, model)) {
            model.addAttribute("username", username);
            model.addAttribute("returnUrl", returnUrl);
            LogLogin log = new LogLogin();
            log.setName(username);
            log.setErrorPassword(password);
            log.setIp(UserUtils.getIp(request));
            logLoginService.save(log);
            return "login";
        }
        UserUtils.setAdminToSession(session, user);

        String authToken = UUID.randomUUID().toString();
        UserUtils.addCookie(request, response, COOKIES_USER, authToken, Integer.MAX_VALUE, null);
        service.updateLoginStatus(user.getId(), username, authToken, UserUtils.getIp(request));
        if (notEmpty(returnUrl)) {
            try {
                returnUrl = URLDecoder.decode(returnUrl, "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
            return REDIRECT + returnUrl;
        } else
            return REDIRECT + "index.html";
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public String changeMyselfPassword(Integer id, String oldpassword, String password, String repassword,
            HttpServletRequest request, HttpSession session, ModelMap model) {
        SysUser user = UserUtils.getAdminFromSession(session);
        if (virifyNotEquals("password", user.getPassword(), VerificationUtils.encode(oldpassword), model)) {
            return TEMPLATE_ERROR;
        } else if (virifyNotEmpty("password", password, model) || virifyNotEquals("repassword", password, repassword, model)) {
            return TEMPLATE_ERROR;
        } else {
            UserUtils.clearAdminToSession(session);
            model.addAttribute("message", "message.needReLogin");
        }
        service.updatePassword(user.getId(), VerificationUtils.encode(password));
        return "common/ajaxTimeout";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        UserUtils.clearAdminToSession(session);
        return REDIRECT + "index.html";
    }


    @RequestMapping(value = "clearTemplateCache")
    public String clearTemplateCache(HttpServletRequest request) {
        cacheComponent.clearTemplateCache();
        return TEMPLATE_DONE;
    }

    protected boolean virifyNotAdmin(SysUser user, ModelMap model) {
        if (!user.isDisabled() && !user.isSuperuserAccess()) {
            model.addAttribute(ERROR, "verify.user.notAdmin");
            return true;
        }
        return false;
    }
}

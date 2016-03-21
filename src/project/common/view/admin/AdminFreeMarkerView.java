package project.common.view.admin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import project.common.tools.UserUtils;
import project.common.view.InitializeFreeMarkerView;

public class AdminFreeMarkerView extends InitializeFreeMarkerView {
    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.put(CONTEXT_ADMIN, UserUtils.getAdminFromSession(request.getSession()));
        super.exposeHelpers(model, request);
    }
}
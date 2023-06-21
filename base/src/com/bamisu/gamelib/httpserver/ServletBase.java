package com.bamisu.gamelib.httpserver;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.ServletUtil;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class ServletBase extends HttpServlet {

    private static final long serialVersionUID = 5832681505681017591L;
    protected Logger logger = Logger.getLogger(this.getClass());

    //	private static final String SECRET = ConfigHandle.instance().get("rest_secret");
    private static final String SECRET = ServerConstant.rest_secret;

    public int getContextUserId(HttpServletRequest req) {
        AppContext ctx = getAppContext(req);
        return ctx.userid;
    }

    public String getContextUserName(HttpServletRequest req) {
        AppContext ctx = getAppContext(req);
        return ctx.username;
    }

    /**
     * Lấy AppContext của request hiện tại, tạo mới nếu chưa có
     *
     * @param req
     * @return
     */
    public AppContext getAppContext(HttpServletRequest req) {
        AppContext ctx = null;
        try {
            ctx = (AppContext) req.getAttribute("app.context");
        } catch (Exception ex) {
        }
        if (ctx == null) {
            ctx = new AppContext();
            req.setAttribute("app.context", ctx);
        }
        return ctx;
    }

    public String getSign(long ownerId, long time) {
        String key = ownerId + SECRET + time;
        key = DigestUtils.md5Hex(key);
        return key;
    }

    public boolean checkRequest(HttpServletRequest req) {
        long ownerId = ServletUtil.getLongParameter(req, "ownerId");
        long time = ServletUtil.getLongParameter(req, "time");
        String sign = ServletUtil.getStringParameter(req, "sign");
        return checkSign(ownerId, time, sign);
    }

    public boolean checkSign(long ownerId, long time, String sign) {
        return getSign(ownerId, time).equals(sign);
    }

    public boolean postParamValidate(long ownerId, long time, String sign) {
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        if (currentTime - time > 86400) {
            return false;
        }
        return checkSign(ownerId, time, sign);
    }

    protected void responseText(Object text, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");
            out = response.getWriter();
            out.print(text);
        } catch (IOException ex) {
            logger.warn(ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        logger.info("RESPONSE: " + text);
    }

    protected void responseJson(Object text, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            out.print(text);
        } catch (IOException ex) {
            logger.warn(ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        logger.info("RESPONSE: " + text);
    }

    protected String getCookieValue(HttpServletRequest request,
                                    String cookieName) {
        String zAuthString = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie ck : cookies) {
                if (ck.getName().equalsIgnoreCase(cookieName)) {
                    zAuthString = ck.getValue();
                    break;
                }
            }
        }
        return zAuthString;
    }


    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doProcess(req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doProcess(req, resp);
    }

    private void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            process(req, resp);
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        }
    }

    protected void process(HttpServletRequest req, HttpServletResponse resp) {
        boolean signSuccess = checkRequest(req);
        writeLog(req);
    }

    @SuppressWarnings("rawtypes")
    private void writeLog(HttpServletRequest req) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = req.getHeader(key);
            map.put(key, value);
        }
        Enumeration params = req.getParameterNames();
        while (params.hasMoreElements()) {
            String key = (String) params.nextElement();
            String value = req.getParameter(key);
            map.put(key, value);
        }

        logger.trace("REQUEST:" + map);
    }

    public void fixHeaders(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "origin, x-requested-with, content-type");
        response.addHeader("Access-Control-Max-Age", "86400");
    }

    public ISFSObject sendCmd(String serverId, String cmd, ISFSObject params) {
        ISFSObject res = new SFSObject();
        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
        Zone zone = SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(serverId);

        if (zone == null) {
            return res;
        }

        return (ISFSObject) zone
                .getExtension()
                .handleInternalMessage(cmd, params);
    }

    public String setErrorCode(short errorCode) {
        ISFSObject res = new SFSObject();
        res.putShort(Params.ERROR_CODE, errorCode);
        return res.toJson();
    }

    public String setErrorCode(short errorCode, String msg) {
        ISFSObject res = new SFSObject();
        res.putShort(Params.ERROR_CODE, errorCode);
        res.putText(Params.MESS, msg);
        return res.toJson();
    }
}

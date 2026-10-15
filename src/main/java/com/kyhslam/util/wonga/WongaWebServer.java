package com.kyhslam.util.wonga;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * 원가절감 실적조회 화면 (ListOfBasedOnDrwNumber.jsp) 을 로컬 웹서버로 실행.
 * 실행 후 브라우저에서 http://localhost:8090/ 접속. (포트는 첫번째 인자로 변경 가능)
 *
 * 템플릿 : dyna/wonga/templates/search.html
 */
public class WongaWebServer {

    private static final int DEFAULT_PORT = 8090;

    private final TemplateEngine templateEngine;

    public WongaWebServer() {
        templateEngine = new TemplateEngine();
        // 1순위 : 클래스패스 (maven 빌드 시 src 의 html 이 target/classes 로 복사됨)
        templateEngine.addTemplateResolver(configure(new ClassLoaderTemplateResolver(), "dyna/wonga/templates/", 1));
        // 2순위 : 프로젝트 루트에서 실행 시 소스 경로 (eclipse 빌드는 src 의 html 을 복사하지 않음)
        templateEngine.addTemplateResolver(configure(new FileTemplateResolver(), "src/dyna/wonga/templates/", 2));
    }

    public static void main(String[] args) throws IOException {

        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        // 운영 DB 데이터를 조회하므로 로컬에서만 접속 가능하도록 localhost 에 바인딩
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/", new WongaWebServer().new SearchHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("원가절감 실적조회 화면 : http://localhost:" + port + "/");
    }

    private static AbstractConfigurableTemplateResolver configure(AbstractConfigurableTemplateResolver resolver, String prefix, int order) {
        resolver.setPrefix(prefix);
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        resolver.setCheckExistence(true);
        resolver.setOrder(order);
        return resolver;
    }

    private class SearchHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            try {
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();

                if ("/".equals(path) && "GET".equals(method)) {
                    render(exchange, 200, new HashMap<String, String>(), new Context(Locale.KOREA));
                } else if ("/search".equals(path) && "POST".equals(method)) {
                    Map<String, String> form = parseForm(readBody(exchange));
                    render(exchange, 200, form, search(form));
                } else if ("/search".equals(path) && "GET".equals(method)) {
                    exchange.getResponseHeaders().set("Location", "/");
                    send(exchange, 302, "text/plain", new byte[0]);
                } else {
                    send(exchange, 404, "text/plain; charset=UTF-8", "Not Found".getBytes(StandardCharsets.UTF_8));
                }
            } catch (Throwable e) {
                e.printStackTrace();
                send(exchange, 500, "text/plain; charset=UTF-8", ("오류 : " + e).getBytes(StandardCharsets.UTF_8));
            } finally {
                exchange.close();
            }
        }
    }

    /**
     * 조회 실행 후 화면에 표시할 값을 Context 에 담는다. (_right.jsp)
     */
    private Context search(Map<String, String> form) {

        Context ctx = new Context(Locale.KOREA);
        ctx.setVariable("searched", true);

        DrwNumberSearchResult result;
        try {
            result = DrwNumberSearchService.search(toCondition(form));
        } catch (IllegalArgumentException e) {
            ctx.setVariable("message", e.getMessage());
            return ctx;
        } catch (TimeoutException e) {
            ctx.setVariable("message", "실행 중 타임아웃으로 전체 종료 되었습니다. 조회 조건 추가 또는 기간을 줄인 후 재 실행 부탁드립니다.");
            return ctx;
        } catch (Exception e) {
            e.printStackTrace();
            ctx.setVariable("message", "조회 중 오류가 발생하였습니다. : " + e.getMessage());
            return ctx;
        }

        int cnt = result.size();
        System.out.println("cnt : " + cnt);

        if (cnt == 0) {
            ctx.setVariable("message", "데이터가 없습니다.");
        } else if (cnt >= DrwNumberSearchResult.MAX_ROWS) {
            ctx.setVariable("message", "최대로 불러올 수 있는 데이터 수를 초과하였습니다. 조회 조건 추가 또는 기간을 줄인 후 재 실행 부탁드립니다. (한번에 조회 가능한 최대 데이터 수 : 10,000건)");
        } else {
            List<List<String>> rows = new ArrayList<List<String>>();
            for (List<String> row : result.data) {
                List<String> cells = new ArrayList<String>();
                for (String col : row) {
                    cells.add(col == null ? "*" : col);
                }
                rows.add(cells);
            }
            ctx.setVariable("cnt", cnt);
            ctx.setVariable("mengeSum", result.mengeSum);
            ctx.setVariable("head", result.head);
            ctx.setVariable("rows", rows);
        }
        return ctx;
    }

    private static DrwNumberSearchCondition toCondition(Map<String, String> form) {

        DrwNumberSearchCondition cond = new DrwNumberSearchCondition();
        cond.gLCode = value(form, "g_l_code");
        cond.chkDetail = form.containsKey("chk_detail");
        cond.elAtyp = value(form, "EL_ATYP");
        cond.elEcsf = value(form, "EL_ECSF");
        cond.elEtm = value(form, "EL_ETM");
        cond.elAspd1 = value(form, "EL_ASPD_1");
        cond.elAspd2 = value(form, "EL_ASPD_2");
        cond.elAman1 = value(form, "EL_AMAN_1");
        cond.elAman2 = value(form, "EL_AMAN_2");
        cond.elEcjj1 = value(form, "EL_ECJJ_1");
        cond.elEcjj2 = value(form, "EL_ECJJ_2");
        cond.partNo = value(form, "PARTNO");
        cond.blockNo = value(form, "BLOCKNO");
        cond.specList = value(form, "specList");
        cond.startDate = value(form, "start_date_day");
        cond.endDate = value(form, "end_date_day");
        cond.chkGeneral = form.containsKey("chk_general");
        cond.chkBom = form.containsKey("chk_bom");
        return cond;
    }

    private void render(HttpExchange exchange, int status, Map<String, String> form, Context ctx) throws IOException {

        if (!form.containsKey("specList"))
            form.put("specList", DrwNumberSearchCondition.DEFAULT_SPEC_LIST);
        ctx.setVariable("form", form);

        String html = templateEngine.process("search", ctx);
        send(exchange, status, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8));
    }

    private static void send(HttpExchange exchange, int status, String contentType, byte[] body) throws IOException {

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, body.length == 0 ? -1 : body.length);
        if (body.length > 0) {
            OutputStream os = exchange.getResponseBody();
            os.write(body);
            os.close();
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {

        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) != -1) {
            bos.write(buf, 0, n);
        }
        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }

    private static Map<String, String> parseForm(String body) throws IOException {

        Map<String, String> form = new HashMap<String, String>();
        if (body == null || body.isEmpty())
            return form;

        for (String pair : body.split("&")) {
            int idx = pair.indexOf('=');
            String key = URLDecoder.decode(idx < 0 ? pair : pair.substring(0, idx), "UTF-8");
            String val = idx < 0 ? "" : URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
            form.put(key, val);
        }
        return form;
    }

    private static String value(Map<String, String> form, String key) {
        String v = form.get(key);
        return v == null ? "" : v;
    }
}

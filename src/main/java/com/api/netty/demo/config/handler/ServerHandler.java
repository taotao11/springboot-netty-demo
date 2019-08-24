package com.api.netty.demo.config.handler;


import com.api.netty.demo.config.router.Action;
import com.api.netty.demo.config.router.HttpLabel;
import com.api.netty.demo.config.router.HttpRouter;
import com.api.netty.demo.bean.vo.GeneralResponse;
import com.api.netty.demo.util.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

/**

* @Author:         QianJiaXiang
* @CreateDate:     2019-08-19 16:13
*/
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    HttpRouter httpRouter;

    public ServerHandler(HttpRouter httpRouter) {
        this.httpRouter = httpRouter;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        GeneralResponse generalResponse;
        if (uri.contains("?")) {
            uri = uri.substring(0, uri.indexOf("?"));
        }
        //根据不同的请求API做不同的处理(路由分发)
        Action<GeneralResponse> action = httpRouter.getRoute(new HttpLabel(uri, request.method()));
        if (action != null) {
            if (action.isInjectionFullhttprequest()) {
                ResponseUtil.response(ctx, request, action.call(request));
            } else {
                ResponseUtil.response(ctx, request, action.call());
            }
        } else {
            //错误处理
            generalResponse = new GeneralResponse(HttpResponseStatus.BAD_REQUEST, "请检查你的请求方法及url", null);
            ResponseUtil.response(ctx, request, generalResponse);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        ctx.close();
    }
}

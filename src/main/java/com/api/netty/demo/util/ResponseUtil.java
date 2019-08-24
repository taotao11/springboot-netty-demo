package com.api.netty.demo.util;


import com.api.netty.demo.bean.vo.GeneralResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

/**
* @Description:    reponseUtils
* @Author:         QianJiaXiang
* @CreateDate:     2019-08-19 16:11
*/
public class ResponseUtil {
    private ResponseUtil() {
    }

    private static final GeneralResponse notFoundGeneralResponse = new GeneralResponse(HttpResponseStatus.NOT_FOUND, "404 NOT_FOUND", null);

    public static void notFound(ChannelHandlerContext ctx, FullHttpRequest request) {
        response(ctx, request, notFoundGeneralResponse);
    }

    /**
     * 响应HTTP的请求
     *
     * @param ctx
     * @param request
     * @param generalResponse
     */
    public static void response(ChannelHandlerContext ctx, FullHttpRequest request, GeneralResponse generalResponse) {

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        byte[] jsonByteByte = JsonUtil.toJson(generalResponse).getBytes();
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, generalResponse.getStatus(),
                Unpooled.wrappedBuffer(jsonByteByte));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
        }
        ctx.flush();
    }
}

package com.aiocloud.gateway.cache.client.protocol;

import com.aiocloud.gateway.cache.base.constants.SystemConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @description: MessageDecoder.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 14:08
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        Message message = new Message();

        // 读取魔数
        message.setMagicNumber(in.readInt());

        // 读取主版本号
        message.setMainVersion(in.readByte());

        // 读取次版本号
        message.setSubVersion(in.readByte());

        // 读取修订版本号
        message.setModifyVersion(in.readByte());

        // 读取sessionId
        int sessionIdLength = in.readInt();
        CharSequence sessionId = in.readCharSequence(sessionIdLength, Charset.defaultCharset());
        message.setSessionId((String) sessionId);

        // 读取当前的消息类型
        byte type = in.readByte();
        MessageTypeEnum messageType = MessageTypeEnum.get(type);
        message.setMessageType(messageType);

        // 读取附件长度
        short attachmentSize = in.readShort();
        for (short i = 0; i < attachmentSize; i++) {
            int keyLength = in.readInt();
            CharSequence key = in.readCharSequence(keyLength, Charset.defaultCharset());
            int valueLength = in.readInt();
            CharSequence value = in.readCharSequence(valueLength, Charset.defaultCharset());
            message.addAttachment(key.toString(), value.toString());
        }

        // 读取消息体长度和数据
        int bodyLength = in.readInt();
        CharSequence body = in.readCharSequence(bodyLength, Charset.defaultCharset());
        message.setBody(body.toString());
        out.add(message);
    }
}

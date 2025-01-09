package com.aiocloud.gateway.cache.server.protocol;

import com.aiocloud.gateway.cache.base.utils.SerializationUtil;
import com.aiocloud.gateway.cache.model.CacheMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @description: MessageDecoder.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 14:08
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        try {

            doDecode(in, out);

        } catch (Exception ex) {
            log.error("decode message error, caused by:", ex);
            throw new Exception(ex);
        }
    }

    /**
     * 开始解码
     *
     * @param: in
     * @param: out
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-09 11:03
     * @since 1.0.0
     */
    private static void doDecode(ByteBuf in, List<Object> out) throws Exception {

        Message message = new Message();

        // 读取 id
        message.setId(in.readLong());

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
        CharSequence body = in.readCharSequence(bodyLength, StandardCharsets.ISO_8859_1);
        byte[] bytes = body.toString().getBytes(StandardCharsets.ISO_8859_1);
        if (bytes.length > 0) {
            CacheMessage cacheMessage = SerializationUtil.deserializeObject(bytes, CacheMessage.class);
            message.setBody(cacheMessage);
        }

        out.add(message);
    }

}

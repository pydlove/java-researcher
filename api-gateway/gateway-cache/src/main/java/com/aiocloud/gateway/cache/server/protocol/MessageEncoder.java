package com.aiocloud.gateway.cache.server.protocol;

import com.aiocloud.gateway.cache.base.constants.SystemConstant;
import com.aiocloud.gateway.cache.base.utils.SerializationUtil;
import com.aiocloud.gateway.cache.model.CacheMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 * @description: MessageEncoder.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 13:59
 */
@Slf4j
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {

        try {

            doEncode(message, out);

        } catch (Exception ex) {
            log.error("encode message error, caused by:", ex);
            throw new Exception(ex);
        }
    }

    /**
     * 开始编码
     *
     * @param: message
     * @param: out
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-09 11:02
     * @since 1.0.0
     */
    private static void doEncode(Message message, ByteBuf out) throws Exception {

        // 这里会判断消息类型是不是 EMPTY 类型，如果是 EMPTY 类型，则表示当前消息不需要写入到管道中
        if (message.getMessageType() != MessageTypeEnum.EMPTY) {

            // 写入 id
            Long id = Optional.ofNullable(message.getId()).orElse(0L);
            out.writeLong(id);

            // 写入当前的魔数
            out.writeInt(SystemConstant.MAGIC_NUMBER);

            // 写入当前的主版本号
            out.writeByte(SystemConstant.MAIN_VERSION);

            // 写入当前的次版本号
            out.writeByte(SystemConstant.SUB_VERSION);

            // 写入当前的修订版本号
            out.writeByte(SystemConstant.MODIFY_VERSION);

            // 生成一个sessionId，并将其写入到字节序列中
            String sessionId = Optional.ofNullable(message.getSessionId()).orElse(SessionIdGenerator.generateSessionId());
            out.writeInt(sessionId.length());
            out.writeCharSequence(sessionId, Charset.defaultCharset());

            // 写入当前消息的类型
            int type = message.getMessageType().getType();
            out.writeByte(type);

            // 写入当前消息的附加参数数量
            out.writeShort(message.getAttachments().size());

            message.getAttachments().forEach((key, value) -> {
                Charset charset = Charset.defaultCharset();
                out.writeInt(key.length());
                out.writeCharSequence(key, charset);
                out.writeInt(value.length());
                out.writeCharSequence(value, charset);
            });

            CacheMessage body = message.getBody();
            if (Objects.isNull(body)) {

                out.writeInt(0);
            } else {
                byte[] bodyBytes = SerializationUtil.serializeObject(body);
                out.writeInt(bodyBytes.length);

                String charSequence = new String(bodyBytes, StandardCharsets.ISO_8859_1);
                out.writeCharSequence(charSequence, StandardCharsets.ISO_8859_1);
            }
        }
    }

}

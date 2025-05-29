package com.aiocloud.llm.qwen;

import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.output.Response;

public class WxImageTest {

    public static void main(String[] args) {
        WanxImageModel model = WanxImageModel
                .builder()
                .apiKey("sk-37d80645c96d4a8184b7b52bb3bb0940")
                .modelName("wanx2.1-t2i-plus")
                .build();

        Response<Image> response = model.generate("田曦薇");
        System.out.println(response.content().url());
    }
}

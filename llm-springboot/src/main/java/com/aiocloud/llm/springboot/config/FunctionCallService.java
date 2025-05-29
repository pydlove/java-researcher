package com.aiocloud.llm.springboot.config;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class FunctionCallService {

    @Tool("田曦薇在某个地方有多少个粉丝")
    public Integer searchFansFromDb(@P("地区") String address) {

        if ("杭州".equals(address)) {
            return 100;
        }

        return 0;
    }
}

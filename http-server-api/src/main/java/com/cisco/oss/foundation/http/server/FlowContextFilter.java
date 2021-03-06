/*
 * Copyright 2015 Cisco Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cisco.oss.foundation.http.server;

import com.cisco.oss.foundation.flowcontext.FlowContextFactory;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Thia filter will extract the flow context from a known header and create it if ti doesn't exist.
 */
@Component
@Order(20)
public class FlowContextFilter extends AbstractInfraHttpFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FlowContextFilter.class);
	
	public FlowContextFilter(){
		super();
	}

	public FlowContextFilter(String serviceName){
		super(serviceName);
	}


	@Override
	public void doFilterImpl(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        final long startTime = System.currentTimeMillis();

        String actualFCHeader = HttpServerFactory.FLOW_CONTEXT_HEADER_UPPER;
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest)request;
            String flowCtxtStrLower = httpServletRequest.getHeader(HttpServerFactory.FLOW_CONTEXT_HEADER_LOWER);
            String flowCtxtStrUpper = httpServletRequest.getHeader(HttpServerFactory.FLOW_CONTEXT_HEADER_UPPER);
            if(!Strings.isNullOrEmpty(flowCtxtStrLower)){
                FlowContextFactory.deserializeNativeFlowContext(flowCtxtStrLower);
                actualFCHeader = HttpServerFactory.FLOW_CONTEXT_HEADER_LOWER;
            }else if(!Strings.isNullOrEmpty(flowCtxtStrUpper)){
                FlowContextFactory.deserializeNativeFlowContext(flowCtxtStrUpper);
            }else{
                FlowContextFactory.createFlowContext();
            }

		} catch (Exception e) {
			LOGGER.warn("problem setting flow context filter: " + e, e);
		}

        ((HttpServletResponse)response).setHeader(actualFCHeader, FlowContextFactory.serializeNativeFlowContext());
        chain.doFilter(request, response);

		if (request.isAsyncStarted()) {

            AsyncContext async = request.getAsyncContext();
            async.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent event) throws IOException {
                    long endTime = System.currentTimeMillis();
                    int processingTime = (int) (endTime - startTime);
                    LOGGER.debug("Processing time: {} milliseconds", processingTime);
                }

                @Override
                public void onTimeout(AsyncEvent event) throws IOException {

                }

                @Override
                public void onError(AsyncEvent event) throws IOException {

                }

                @Override
                public void onStartAsync(AsyncEvent event) throws IOException {

                }
            });


		}else{
			long endTime = System.currentTimeMillis();
			int processingTime = (int) (endTime - startTime);
			LOGGER.debug("Processing time: {} milliseconds", processingTime);
		}

	}

	
	@Override
	protected String getKillSwitchFlag() {
		return "http.flowContextFilter.isEnabled";
	}
	
	@Override
	protected boolean isEnabledByDefault() {
		return true;
	}
	
	

}

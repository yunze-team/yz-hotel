package com.yzly.job.core.handler.impl;

import com.yzly.job.core.biz.model.ReturnT;
import com.yzly.job.core.handler.IJobHandler;
import com.yzly.job.core.log.XxlJobLogger;

/**
 * glue job handler
 * @author yunze tech team 2016-5-19 21:05:45
 */
public class GlueJobHandler extends IJobHandler {

	private long glueUpdatetime;
	private IJobHandler jobHandler;
	public GlueJobHandler(IJobHandler jobHandler, long glueUpdatetime) {
		this.jobHandler = jobHandler;
		this.glueUpdatetime = glueUpdatetime;
	}
	public long getGlueUpdatetime() {
		return glueUpdatetime;
	}

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("----------- glue.version:"+ glueUpdatetime +" -----------");
		return jobHandler.execute(param);
	}

}

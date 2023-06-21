package com.bamisu.gamelib.metric;


import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.Utils;
import org.apache.log4j.Logger;

/**
 * Created by Popeye on 8/10/2018.
 */
public abstract class MetricLog {
    protected Logger logger;


    protected String name = "MetricLog";
    public int ss;

    public MetricLog() {
        this.logger = Logger.getLogger("MetricLog");
    }

    public String getName() {
        return name;
    }

    public void writeLog() {
        logger.info(Utils.toJson(this));
    }
}

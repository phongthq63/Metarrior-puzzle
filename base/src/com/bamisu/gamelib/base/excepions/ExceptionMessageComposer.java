package com.bamisu.gamelib.base.excepions;


import com.bamisu.gamelib.utils.Logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExceptionMessageComposer {

    private static final String NEW_LINE = System.getProperty("line.separator");
    public static volatile boolean globalPrintStackTrace = true;
    public static volatile boolean useExtendedMessages = true;
    private String mainErrorMessage;
    private String exceptionType;
    private String description;
    private String possibleCauses;
    private String stackTrace;
    private List additionalInfos;
    private StringBuilder buf;

    public ExceptionMessageComposer(Throwable t) {
        this(t, globalPrintStackTrace);
    }

    public ExceptionMessageComposer(Throwable t, boolean printStackTrace) {
        mainErrorMessage = t.getMessage();
        if (mainErrorMessage == null)
            mainErrorMessage = "*** Null ***";
        exceptionType = t.getClass().getName();
        buf = new StringBuilder();
        if (printStackTrace)
            setStackTrace(t);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPossibleCauses(String possibleCauses) {
        this.possibleCauses = possibleCauses;
    }

    private void setStackTrace(Throwable t) {
        stackTrace = Logging.formatStackTrace(t.getStackTrace());
    }

    public void addInfo(String infoMessage) {
        if (additionalInfos == null)
            additionalInfos = new ArrayList();
        additionalInfos.add(infoMessage);
    }

    public String toString() {
        if (!useExtendedMessages) {
            buf.append(exceptionType).append(" ").append(mainErrorMessage);
            return buf.toString();
        }
        buf.append(NEW_LINE);
        buf.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::").append(NEW_LINE);
        buf.append("Exception: ").append(exceptionType).append(NEW_LINE);
        buf.append("Message: ").append(mainErrorMessage).append(NEW_LINE);
        if (description != null)
            buf.append("Description: ").append(description).append(NEW_LINE);
        if (possibleCauses != null)
            buf.append("Possible Causes: ").append(possibleCauses).append(NEW_LINE);
        if (additionalInfos != null) {
            String info;
            for (Iterator iterator = additionalInfos.iterator(); iterator.hasNext(); buf.append(info).append(NEW_LINE))
                info = (String) iterator.next();

        }
        if (stackTrace != null) {
            buf.append("+--- --- ---+").append(NEW_LINE);
            buf.append("Stack Trace:").append(NEW_LINE);
            buf.append("+--- --- ---+").append(NEW_LINE);
            buf.append(stackTrace);
            buf.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::").append(NEW_LINE);
        } else {
            buf.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::").append(NEW_LINE);
        }
        return buf.toString();
    }

}

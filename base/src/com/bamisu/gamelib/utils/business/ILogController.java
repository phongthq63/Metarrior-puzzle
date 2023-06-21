package com.bamisu.gamelib.utils.business;

public interface ILogController {
    public enum LogMode {
        ERROR("error"),
        ACTION("action"),
        PAYMENT("payment"),
        INFO("info");
      
        private final String code;

        LogMode(String code) {
            this.code = code;
        }

        public String value() {
            return code;
        }
    }

    public void writeLog(LogMode mode, String data);

    public void writeLog(String mode, String data);
}

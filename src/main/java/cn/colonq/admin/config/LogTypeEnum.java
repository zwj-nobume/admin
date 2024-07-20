package cn.colonq.admin.config;

public enum LogTypeEnum {
    INFO {
        @Override
        public String toString() {
            return "info";
        }
    },
    INSERT {
        @Override
        public String toString() {
            return "insert";
        }
    },
    UPDATE {
        @Override
        public String toString() {
            return "update";
        }
    },
    DELETE {
        @Override
        public String toString() {
            return "delete";
        }
    },
    ERROR {
        @Override
        public String toString() {
            return "error";
        }
    },
}

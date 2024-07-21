package cn.colonq.admin.enumcfg;

public enum LogTypeEnum {
    INFO {
        @Override
        public String toString() {
            return "info";
        }
    },
    ADD {
        @Override
        public String toString() {
            return "add";
        }
    },
    EDIT {
        @Override
        public String toString() {
            return "edit";
        }
    },
    DELETE {
        @Override
        public String toString() {
            return "delete";
        }
    },
    DOWNLOAD {
        @Override
        public String toString() {
            return "download";
        }
    },
    LOGIN {
        @Override
        public String toString() {
            return "login";
        }
    },
    ERROR {
        @Override
        public String toString() {
            return "error";
        }
    },
}

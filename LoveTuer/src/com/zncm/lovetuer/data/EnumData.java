package com.zncm.lovetuer.data;

public class EnumData {

    public enum ContextMenuEnum {


        EDIT(0, "编辑"), DEL(1, "删除"), COPY(2, "复制");
        private int value;
        private String strName;

        private ContextMenuEnum(int value, String strName) {
            this.value = value;
            this.strName = strName;
        }

        public int getValue() {
            return value;
        }

        public String getStrName() {
            return strName;
        }


    }


}

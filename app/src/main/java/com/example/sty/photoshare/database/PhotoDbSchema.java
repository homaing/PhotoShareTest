package com.example.sty.photoshare.database;


public class PhotoDbSchema {
    public static final class PhotoTable {
        public static final String NAME = "photos";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SHARED = "shared";
            public static final String CONTACT = "contact";
            public static final String COMMENT = "comment";
        }
    }
}

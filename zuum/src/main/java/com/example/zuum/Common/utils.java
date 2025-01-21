package com.example.zuum.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class utils {
    public static <T> Logger getLogger(Class<T> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

}
